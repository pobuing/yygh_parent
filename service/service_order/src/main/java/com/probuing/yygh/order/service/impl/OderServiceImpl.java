package com.probuing.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.probuing.yygh.common.exp.YyghException;
import com.probuing.yygh.common.utils.HttpRequestHelper;
import com.probuing.yygh.enums.OrderStatusEnum;
import com.probuing.yygh.hosp.client.HospitalFeignClient;
import com.probuing.yygh.model.order.OrderInfo;
import com.probuing.yygh.model.user.Patient;
import com.probuing.yygh.mq.mqconst.MqConst;
import com.probuing.yygh.mq.service.RabbitService;
import com.probuing.yygh.order.mapper.OrderInfoMapper;
import com.probuing.yygh.order.service.OrderService;
import com.probuing.yygh.user.client.PatientFeignClient;
import com.probuing.yygh.vo.hosp.ScheduleOrderVo;
import com.probuing.yygh.vo.msm.MsmVo;
import com.probuing.yygh.vo.order.OrderCountQueryVo;
import com.probuing.yygh.vo.order.OrderCountVo;
import com.probuing.yygh.vo.order.OrderMqVo;
import com.probuing.yygh.vo.order.OrderQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * ClassName: OderServiceImpl
 * date: 2023/8/25 16:44
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Service
@Slf4j
public class OderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {

    @Autowired
    PatientFeignClient patientFeighClient;

    @Autowired
    HospitalFeignClient hospitalFeignClient;

    @Autowired
    RabbitService rabbitService;

    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //判断是否已经挂号

        //1、调用医院端接口 (医院端创建订单 并返回订单数据)
        Patient patient = patientFeighClient.getPatientById(patientId);
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
        //2、创建平台端自己的订单
        //查询医院url 远程调用
        String api_url = hospitalFeignClient.getApiUrlByHoscode(scheduleOrderVo.getHoscode());

        String url = "http://" + api_url + "/order/submitOrder";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", scheduleOrderVo.getHoscode());
        paramMap.put("depcode", scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate", new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        paramMap.put("amount", scheduleOrderVo.getAmount()); //挂号费用
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        //联系人
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
//        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", "");
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, url);
        if (result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");
            OrderInfo orderInfo = new OrderInfo();
            BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
            String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setScheduleId(scheduleId);
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
            //设置添加数据--医院接口返回数据
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.insert(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //更新mg中的号源数量
            //rabbitmq向第一个队列中发送消息
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setAvailableNumber(availableNumber);
            orderMqVo.setReservedNumber(reservedNumber);
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
//            msmVo.setTemplateCode("template");
            HashMap<String, Object> msmParam = new HashMap<>();
            msmParam.put("message", "asdasdadasd");
            msmVo.setParam(msmParam);
            orderMqVo.setMsmVo(msmVo);
            log.info("消息发送");
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,
                    MqConst.ROUTING_ORDER, orderMqVo);
            return orderInfo.getId();
        } else {
            throw new YyghException(20001, (String) result.get("message"));
        }
    }

    @Override
    public OrderInfo getOrderInfo(Long id) {
        OrderInfo orderInfo = baseMapper.selectById(id);
        packOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        if (orderQueryVo != null) {
            Long userId = orderQueryVo.getUserId();
            if (!StringUtils.isEmpty(userId)) {
                queryWrapper.eq("user_id", userId);
            }
            Long patientId = orderQueryVo.getPatientId();
            if (!StringUtils.isEmpty(patientId)) {
                queryWrapper.eq("patient_id", patientId);
            }
            String orderStatus = orderQueryVo.getOrderStatus();
            if (!StringUtils.isEmpty(orderStatus)) {
                queryWrapper.eq("order_status", orderStatus);
            }
        }
        this.page(pageParam, queryWrapper);
        pageParam.getRecords().forEach(orderInfo -> {
            packOrderInfo(orderInfo);
        });
        return pageParam;
    }

    @Override
    public void patientTips() {
        //查询所有符合条件的订单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        String date = new DateTime().toString("yyyy-MM-dd");
        queryWrapper.eq("reserve_date", date);
        List<OrderInfo> list = this.list(queryWrapper);
        list.forEach(orderInfo -> {
            String patientName = orderInfo.getPatientName();//就诊人名称
            String patientPhone = orderInfo.getPatientPhone();//就诊人手机号
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patientPhone);
            msmVo.getParam().put("message", patientName + "你好,请及时就诊");
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM,
                    msmVo);
        });
    }

    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> map = new HashMap<>();
        List<OrderCountVo> orderCountVoList
                = baseMapper.selectOrderCount(orderCountQueryVo);
        //日期列表
        List<String> dateList
                = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //统计列表
        List<Integer> countList
                = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }

    private void packOrderInfo(OrderInfo orderInfo) {
        Integer orderStatus = orderInfo.getOrderStatus();
        String statusNameByStatus = OrderStatusEnum.getStatusNameByStatus(orderStatus);
        orderInfo.getParam().put("orderStatusString", statusNameByStatus);
    }


}
