package com.probuing.yygh.user.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: UserConfig
 * date: 2023/8/24 19:24
 *
 * @author wangxin
 * @version 1.0
 * Description:
 * Good Luck
 */
@Configuration
public class UserConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
