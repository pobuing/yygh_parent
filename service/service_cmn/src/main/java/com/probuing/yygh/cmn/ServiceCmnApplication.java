package com.probuing.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: com.probuing.yygh.cmn.ServiceCmnApplication
 * date: 2023/8/14 15:59
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.probuing.yygh")
@EnableDiscoveryClient
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
    }
}
