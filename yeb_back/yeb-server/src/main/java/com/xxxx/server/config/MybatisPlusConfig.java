package com.xxxx.server.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  mybatis-plus的配置类
 * @author ppliang
 * @date 2020/12/19 15:53
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 打开mybatis-plus分页功能
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}