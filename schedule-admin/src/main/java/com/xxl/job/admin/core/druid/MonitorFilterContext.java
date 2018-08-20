/**
 * Copyright 2017-2025 schedule Group.
 */
package com.xxl.job.admin.core.druid;

import com.alibaba.druid.filter.stat.StatFilterContext;

/**
 * @author laixiangqun
 * @since 2018-7-11
 */
public class MonitorFilterContext extends StatFilterContext{

    private static final MonitorFilterContext instance  = new MonitorFilterContext();

    public final static MonitorFilterContext getMonitorInstance() {
        return instance;
    }


}
