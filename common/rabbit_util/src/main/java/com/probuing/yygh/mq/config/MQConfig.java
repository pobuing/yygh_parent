package com.probuing.yygh.mq.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: MQConfig
 * date: 2023/8/27 13:07
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Configuration

public class MQConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
