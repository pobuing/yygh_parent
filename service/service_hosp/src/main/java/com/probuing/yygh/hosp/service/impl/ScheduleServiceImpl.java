package com.probuing.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.probuing.yygh.hosp.repository.HospitalRepository;
import com.probuing.yygh.hosp.repository.ScheduleRepository;
import com.probuing.yygh.hosp.service.DepartmentService;
import com.probuing.yygh.hosp.service.ScheduleService;
import com.probuing.yygh.model.hosp.BookingRule;
import com.probuing.yygh.model.hosp.Department;
import com.probuing.yygh.model.hosp.Hospital;
import com.probuing.yygh.model.hosp.Schedule;
import com.probuing.yygh.vo.hosp.BookingScheduleRuleVo;
import com.probuing.yygh.vo.hosp.ScheduleOrderVo;
import com.probuing.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    HospitalRepository hospitalRepository;


    @Autowired
    DepartmentService departmentService;

    //调用医院端接口需要封装的参数从这个vo中取
    //创建平台订单时，也需要vo
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        Hospital hospital = hospitalRepository.findByHoscode(schedule.getHoscode());
        Department department = departmentService.findDepartment(schedule.getHoscode(), schedule.getDepcode());
        BookingRule bookingRule = hospital.getBookingRule();


        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();

        BeanUtils.copyProperties(schedule,scheduleOrderVo);
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setDepname(department.getDepname());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());

        Integer quitDay = bookingRule.getQuitDay();//-1 就诊前一天
        String quitTime = bookingRule.getQuitTime();//15:30

        String s = new DateTime(schedule.getWorkDate()).plusDays(quitDay).toString("yyyy-MM-dd") + " " + quitTime;
        Date date_quiteTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(s).toDate();
        scheduleOrderVo.setQuitTime(date_quiteTime);//退号时间

        //当天
        String releaseTime = bookingRule.getReleaseTime();//08:30
        Date date_startTime = this.getDateTime(new Date(), releaseTime).toDate();
        scheduleOrderVo.setStartTime(date_startTime);//预约开始时间

        String stopTime = bookingRule.getStopTime();//18:30
        Date date = new DateTime().plusDays(bookingRule.getCycle()).toDate();
        Date date_endTime = this.getDateTime(date, stopTime).toDate();

        scheduleOrderVo.setEndTime(date_endTime);//预约截止时间

        DateTime dateTime = this.getDateTime(new Date(), stopTime);
        scheduleOrderVo.setStopTime(dateTime.toDate());//

        return scheduleOrderVo;
    }

    @Override
    public Schedule getById(String id) {
        Optional<Schedule> byId = scheduleRepository.findById(id);
        Schedule schedule = byId.get();
        this.packSchedule(schedule);
        return schedule;
    }

    private void packSchedule(Schedule schedule) {
        //查询医院名称 + 科室名称
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();

        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        Department department = departmentService.findDepartment(hoscode, depcode);

        schedule.getParam().put("hosname",hospital.getHosname());
        schedule.getParam().put("depname",department.getDepname());
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {

        Date date = new DateTime(workDate).toDate();
//        List<Schedule> list = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, date);
        List<Schedule> list =
                scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, date);

        return list;
    }

    @Override
    public Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode) {

        //mongodb中的聚合查询--api---mongoTemplate

        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria), //1、需要对哪些数据进行聚合（分组）
                Aggregation.group("workDate")//2、指定按照哪个属性进行分组聚合

                        .count().as("docCount") //3、从每一组提取值 ， BookingScheduleRuleVo中的docCount
                        //sum("每一个排班的号源数量属性名")  //as -- 总和起了一个名字
                        .sum("reservedNumber").as("reservedNumber") //求和的值赋值给BookingScheduleRuleVo中的reservedNumber
                        .sum("availableNumber").as("availableNumber")// BookingScheduleRuleVo中的availableNumber

                        //第一个文档的workdate取出，赋值给BookingScheduleRuleVo中的workDate
                        .first("workDate").as("workDate"),


                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);


        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregate.getMappedResults();



        bookingScheduleRuleVoList.forEach(bookingScheduleRuleVo -> {
            bookingScheduleRuleVo.setWorkDateMd(bookingScheduleRuleVo.getWorkDate());
            bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime(bookingScheduleRuleVo.getWorkDate())));
        });

        /// 总的日期个数（前端分页）
        Integer total = this.getTotal(hoscode, depcode);


        //前端还需要一个医院名称
        String hosname = hospitalRepository.findByHoscode(hoscode).getHosname();

        Map<String, Object> result = new HashMap<>();
        result.put("total",total);
        result.put("bookingScheduleRuleList",bookingScheduleRuleVoList);

//        result.put("hosname",hosname);

//        Map<String,String> baseMap = new HashMap<>();
//        baseMap.put("hosname",hosname);
//        result.put("baseMap",baseMap);

        return result;
    }


    public Integer getTotal(String hoscode,String depcode){
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregate.getMappedResults();
        int total = bookingScheduleRuleVoList.size();
        return total;
    }

    @Override
    public void save(Map<String, Object> paramMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);

        Schedule schedule_mg = scheduleRepository.findByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if(schedule_mg==null){
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            scheduleRepository.save(schedule);
        }else{
            schedule.setId(schedule_mg.getId());
            schedule.setCreateTime(schedule_mg.getCreateTime());
            schedule.setUpdateTime(new Date());
            scheduleRepository.save(schedule);
        }

    }

    @Override
    public Page<Schedule> selectPage(int pageNum, int pageSize, ScheduleQueryVo scheduleQueryVo) {

        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        Pageable pageable = PageRequest.of(pageNum-1,pageSize,sort);

        Schedule schedule = new Schedule();//key1  key2   不等于null的情况下，就会拼接该条件 key=value and key2 = value2
        BeanUtils.copyProperties(scheduleQueryVo,schedule);

        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreCase(true).withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Schedule> example = Example.of(schedule,exampleMatcher);

        Page<Schedule> all = scheduleRepository.findAll(example, pageable);

        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(schedule!=null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    @Override
    public Map<String, Object> getBookingSchedule(Integer page, Integer limit, String hoscode, String depcode) {

        //1、根据医院编号查询医院对象，预约规则
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        BookingRule bookingRule = hospital.getBookingRule();

        //2、私有方法  IPage（mp）， 查询当前页的日期对象
        IPage iPage = this.getListDate(bookingRule, page, limit);

        List<Date> pageDateList = iPage.getRecords();//当前页日期对象
        long pages = iPage.getPages();//总页数
        long total = iPage.getTotal();


        //每一个日期，对应一个BookingScheduleRuleVo-->每个属性要有值
        //workDate相同的一组排班，统计一些数据，封装到BookingScheduleRuleVo对象中

        //针对指定医院指定科室下的排班按照workDate分组

        //写in查询  ，不要写成is
        //自己做点测试数据
        //3、查询当前页日期集合对应的排班集合，进行分组聚合
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode).and("workDate").in(pageDateList);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),//针对指定的排班进行分组聚合统计
                Aggregation.group("workDate")//
                        .count().as("docCount")//每一组的排班数量，赋值给BookingScheduleRuleVo中的docCount属性
                        .first("workDate").as("workDate")//这一组排班中的第一个排班的workDate取出，赋值给BookingScheduleRuleVo中的workDate属性
                        .sum("reservedNumber").as("reservedNumber")//这一组排班reservedNumber属性值的总和赋值给BookingScheduleRuleVo中reservedNumber属性
                        .sum("availableNumber").as("availableNumber")
        );

        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);

        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggregate.getMappedResults();

//        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
//            Date key = bookingScheduleRuleVo.getWorkDate();
//            map.put(key,bookingScheduleRuleVo);
//        }


        //16---22号，7个日期，每个日期对应的BookingScheduleRuleVo对象
        //但是目前只有18和20号有对应的vo对象，创建默认的rulevo对象

        //4、bookingScheduleRuleVoList转成map ， key：workDate ， value：ruleVo对象本身
        //  16-22 ， 根据每一个日期从list集合中查询对应的ruleVo对象
        Map<Date, BookingScheduleRuleVo> map = bookingScheduleRuleVoList.stream().collect(Collectors.toMap(
                BookingScheduleRuleVo::getWorkDate, bookingScheduleRuleVo -> bookingScheduleRuleVo
        ));

//        int n = 0;
//        for (Date date : pageDateList) {


        List<BookingScheduleRuleVo> bookingScheduleRuleList = new ArrayList<>();
        for (int i = 0 ; i < pageDateList.size() ; i++) {

            Date date = pageDateList.get(i);

//            BookingScheduleRuleVo ruleVo = this.getRuleVoByDate(bookingScheduleRuleVoList,date);
            BookingScheduleRuleVo ruleVo = map.get(date);
            if( ruleVo == null ){
                //创建一个默认的ruleVo对象
                ruleVo = new BookingScheduleRuleVo();
                ruleVo.setDocCount(0);//排班的数量
                ruleVo.setWorkDate(date);
                ruleVo.setReservedNumber(-1);
                ruleVo.setAvailableNumber(-1);//没有排班
            }

            //额外的字段需要赋值
            ruleVo.setWorkDateMd(date);//另一种日期格式
            ruleVo.setDayOfWeek(this.getDayOfWeek(new DateTime(date)));//星期

//            状态
//            0：正常
//            1：即将放号
//            -1：当天已停止挂号

            //最后一页的最后一条，显示即将放号   0  6
//            if(page==pages && n == pageDateList.size()-1){
//
//            }
//            n++;

            if(page==pages && i==pageDateList.size()-1){
                ruleVo.setStatus(1);
            }else{
                ruleVo.setStatus(0);
            }

            //当天已经停止挂号 status=-1
            //第一页的第一条
            if( page==1 && i==0 ){
                String stopTime = bookingRule.getStopTime();
                DateTime dateTime = this.getDateTime(new Date(), stopTime);//11:30

                if(dateTime.isBeforeNow()){
                    ruleVo.setStatus(-1);
                }
            }

            //
            bookingScheduleRuleList.add(ruleVo);

        }


        Map<String,Object> result = new HashMap<>();
        result.put("bookingScheduleList",bookingScheduleRuleList);
        result.put("total",total);//总日期个数


        Map<String,Object> baseMap = new HashMap<>();

        baseMap.put("hosname",hospital.getHosname());

        //depcode
        Department department = departmentService.findDepartment(hoscode,depcode);
        baseMap.put("bigname",department.getBigname());//大科室名称
        baseMap.put("depname",department.getDepname());//小科室名称
        baseMap.put("workDateString",new DateTime().toString("yyyy年MM月"));//2022年11月
        baseMap.put("releaseTime",bookingRule.getReleaseTime());//放号时间
        baseMap.put("stopTime",bookingRule.getStopTime());

        result.put("baseMap",baseMap);

        return result;
    }

    @Override
    public void getBookingScheduleRule(int i, int i1, String number, String s) {

    }

    private BookingScheduleRuleVo getRuleVoByDate(List<BookingScheduleRuleVo> bookingScheduleRuleVoList,Date date) {
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            if(bookingScheduleRuleVo.getWorkDate().equals(date)){
                // == 判断地址    Date中的equal方法判断的是两个日期对象的long类型值
                return bookingScheduleRuleVo;
            }
        }
        return null;
    }

    private IPage getListDate(BookingRule bookingRule, Integer page, Integer limit){
        Integer cycle = bookingRule.getCycle();//预约周期

        DateTime dateTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());

        if(dateTime.isBeforeNow()){
            //当天已经开始放号
            cycle+=1;
        }

        //按照预约周期创建cycle个日期对象（总的日期个数）
        List<Date> dateList = new ArrayList<>();
        //根据cycle创建日期对象
        for (Integer i = 0; i < cycle; i++) {
//            Date date = new Date(i);
            Date date = new DateTime().plusDays(i).toDate();//带有时分秒的
            String yyyyMMdd = new DateTime(date).toString("yyyy-MM-dd");
            Date workDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(yyyyMMdd).toDate();
            dateList.add(workDate);
        }

        //分页  page=1  limit=7
        int begin = (page-1)*limit;//0
        int end = (page-1)*limit + limit;//7

        //最后一页可能不够7条
        if(end>dateList.size()){
            end = dateList.size();
        }

        List<Date> pageDateList = new ArrayList<>();
        for (int i = begin; i < end ; i++) {
            Date date = dateList.get(i);
            pageDateList.add(date);
        }

        // 总记录数   总页数
//        int size = dateList.size();//总记录数
//        int pages  = size/limit + size%limit==0?0:1;  // 43/7 = 7   49/7=7

        com.baomidou.mybatisplus.extension.plugins.pagination.Page datePage =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page(page, limit, dateList.size());
        datePage.setRecords(pageDateList);//当前页数

        //
//        long total = page1.getTotal();
//        long pages = page1.getPages();
//        List records = page1.getRecords();

        return datePage;
    }

    //time = HH:mm
    private DateTime getDateTime(Date date,String time){

//        String releaseTime = bookingRule.getReleaseTime(); //08:30

        String string = new DateTime(date).toString("yyyy-MM-dd") +" "+ time;

        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(string);

        return dateTime;
    }


    private String getDayOfWeek(DateTime dateTime){
        List<String> strings = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");
        int dayOfWeek = dateTime.getDayOfWeek();
        return strings.get(dayOfWeek-1);
    }
}
