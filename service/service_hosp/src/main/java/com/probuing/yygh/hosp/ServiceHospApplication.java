package com.probuing.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ServiceHospApplication
 * date: 2023/8/10 17:57
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.probuing.yygh")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.probuing.yygh")
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
