package com.probuing.yygh.hosp.controller.api;


import com.probuing.yygh.common.result.R;
import com.probuing.yygh.hosp.service.DepartmentService;
import com.probuing.yygh.hosp.service.HospitalService;
import com.probuing.yygh.hosp.service.HospitalSetService;
import com.probuing.yygh.hosp.service.ScheduleService;
import com.probuing.yygh.model.hosp.Hospital;
import com.probuing.yygh.model.hosp.HospitalSet;
import com.probuing.yygh.model.hosp.Schedule;
import com.probuing.yygh.vo.hosp.DepartmentVo;
import com.probuing.yygh.vo.hosp.HospitalQueryVo;
import com.probuing.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {

    @Autowired
    HospitalService hospitalService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    HospitalSetService hospitalSetService;


    @GetMapping("/getApiUrlByHoscode/{hoscode}")
    public String getApiUrlByHoscode(@PathVariable String hoscode) {
        HospitalSet hospitalSet = hospitalSetService.findByHoccode(hoscode);
        return hospitalSet.getApiUrl();
    }

    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }


    //点击剩余按钮，查询排班详情
    @GetMapping("getSchedule/{id}")
    public R getSchedule(@PathVariable String id) {
        Schedule schedule = scheduleService.getById(id);
        return R.ok().data("schedule", schedule);
    }

    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public R getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return R.ok().data("scheduleList", list);
    }

    //跳转到挂号详情页面，调用该接口
    //日期分页
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public R getBookingSchedule(@PathVariable Integer page, @PathVariable Integer limit,
                                @PathVariable String hoscode, @PathVariable String depcode) {
        //map--List<BookingScheduleRuleVo>
        Map<String, Object> map = scheduleService.getBookingSchedule(page, limit, hoscode, depcode);
        return R.ok().data(map);//日期的总记录 ，  list<bsrvo> ,hosname depname 2022年11月
    }


    /**
     * 在医院详情页面，根据医院编号，查询科室列表
     *
     * @param hoscode
     * @return
     */
    @GetMapping("department/{hoscode}")
    public R department(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDepTree(hoscode);
        return R.ok().data("list", list);
    }

    /**
     * index首页上，选中某个医院之后被调用，查询医院详情
     *
     * @param hoscode
     * @return
     */
    @GetMapping("{hoscode}")
    public R item(@PathVariable String hoscode) {
        Map<String, Object> map = hospitalService.item(hoscode);
        return R.ok().data(map);
    }

    /**
     * 首页上查询医院列表
     *
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @GetMapping("{page}/{limit}")
    public R index(@PathVariable Integer page, @PathVariable Integer limit, HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitalPage = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return R.ok().data("pages", hospitalPage);
    }

    /**
     * 根据医院名称模糊查询
     *
     * @param hosname
     * @return
     */
    @GetMapping("findByHosname/{hosname}")
    public R findByHosname(@PathVariable String hosname) {
        //根据医院名称模糊查询
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return R.ok().data("list", list);
    }


}
