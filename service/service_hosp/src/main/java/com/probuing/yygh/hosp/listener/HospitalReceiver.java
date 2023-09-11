package com.probuing.yygh.hosp.listener;

import com.probuing.yygh.hosp.repository.ScheduleRepository;
import com.probuing.yygh.model.hosp.Schedule;
import com.probuing.yygh.mq.mqconst.MqConst;
import com.probuing.yygh.mq.service.RabbitService;
import com.probuing.yygh.vo.order.OrderMqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ClassName: HospitalReceiver
 * date: 2023/8/27 13:21
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Component
@Slf4j
public class HospitalReceiver {
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key = {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo) throws IOException {
        log.info("医院端收到信息"+orderMqVo.toString());
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        Integer reservedNumber = orderMqVo.getReservedNumber();
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setAvailableNumber(availableNumber);
        schedule.setReservedNumber(reservedNumber);

        //从mg中查询排班 更新num
        scheduleRepository.save(schedule);
        //发送消息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM,
                orderMqVo.getMsmVo());
    }
}
