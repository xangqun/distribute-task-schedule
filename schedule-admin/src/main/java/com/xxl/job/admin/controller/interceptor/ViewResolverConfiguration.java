/**
 * Copyright 2017-2025 Evergrande Group.
 */
package com.xxl.job.admin.controller.interceptor;

import com.xxl.job.admin.core.druid.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * @author laixiangqun
 * @since 2018-6-28
 */
@Configuration
public class ViewResolverConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource getDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public SchedulerFactoryBean quartzScheduler(){
        SchedulerFactoryBean schedulerFactoryBean=new SchedulerFactoryBean();
        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setStartupDelay(20);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactoryBean.setDataSource(getDataSource());
        return schedulerFactoryBean;
    }

}
