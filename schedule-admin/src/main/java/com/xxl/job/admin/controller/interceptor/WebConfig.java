/**
 * Copyright 2017-2025 Evergrande Group.
 */
package com.xxl.job.admin.controller.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author laixiangqun
 * @since 2018-6-28
 */
@Configuration
public class WebConfig  extends WebMvcConfigurerAdapter {


    @Bean
    public CookieInterceptor getCookieInterceptor() {
        return new CookieInterceptor();
    }

    @Bean
    @DependsOn("xxlJobAdminConfig")
    public PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptor = registry.addInterceptor(getCookieInterceptor());
        InterceptorRegistration permissionInterceptor = registry.addInterceptor(getPermissionInterceptor());
        // 排除配置
//        permissionInterceptor.excludePathPatterns("/error");

        // 拦截配置
        interceptor.addPathPatterns("/**");
        permissionInterceptor.addPathPatterns("/**");

    }

}
