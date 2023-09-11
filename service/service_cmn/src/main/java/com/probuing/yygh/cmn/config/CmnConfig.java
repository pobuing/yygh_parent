package com.probuing.yygh.cmn.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ClassName: HospConfig
 * date: 2023/8/10 18:31
 *
 * @author wangxin
 * @version 1.0
 * Description:配置类
 * Good Luck
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.probuing.yygh.cmn.mapper")

public class CmnConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
