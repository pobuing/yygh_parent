package com.probuing.yygh.service.sms.listener;

import com.probuing.yygh.mq.mqconst.MqConst;
import com.probuing.yygh.vo.msm.MsmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
public class MsmReceiver {


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    public void receiver(MsmVo msmVo) throws IOException {
        log.info("短信渠道收到信息"+msmVo.toString());

        System.out.println("模拟给就诊人发送短信消息通知");
        String phone = msmVo.getPhone();
        String templateCode = msmVo.getTemplateCode();
        Object message = msmVo.getParam().get("message");
        log.info("手机号" + phone + ".短信内容" + message);
        //发送消息
//        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM,
//                orderMqVo.getMsmVo());
    }
}
