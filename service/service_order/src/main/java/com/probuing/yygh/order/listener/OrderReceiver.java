package com.probuing.yygh.order.listener;

import com.probuing.yygh.mq.mqconst.MqConst;
import com.probuing.yygh.order.service.OrderService;
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
public class OrderReceiver {

    @Autowired
    OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_8, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = {MqConst.ROUTING_TASK_8}
    ))
    public void receiver(OrderMqVo orderMqVo) throws IOException {
        System.out.println("OrderReceiver 接收到消息");
        orderService.patientTips();
    }
}
