package com.probuing.yygh.task;

import com.probuing.yygh.mq.mqconst.MqConst;
import com.probuing.yygh.mq.service.RabbitService;
import com.probuing.yygh.vo.order.OrderMqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ClassName: ScheduledTask
 * date: 2023/9/11 15:19
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@EnableScheduling
@Component
public class ScheduledTask {
    @Autowired
    RabbitService rabbitService;

    @Scheduled(cron = "8 * * * * ?")
    public void task1() {
        System.out.println(new Date());
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8,
                new OrderMqVo());
    }
}
