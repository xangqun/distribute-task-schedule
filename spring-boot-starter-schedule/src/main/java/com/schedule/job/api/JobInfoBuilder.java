/**
 * @Probject Name: spring-boot-starter-starnetworkjob
 * @Path: com.starnetwork.job.apiJobInfoBuilder.java
 * @Create By jakic
 * @Create In 2018年7月19日 上午10:11:24
 * TODO
 */
package com.schedule.job.api;

import com.xxl.job.core.biz.model.JobInfo;

/**
 * @Class Name JobInfoBuilder
 * @Author wangzhijie
 * @Create In 2018年7月19日
 */
public class JobInfoBuilder {
  
  public static JobInfo newDefaultJob() {
    JobInfo jobInfo = new JobInfo();
    jobInfo.setExecutorRouteStrategy("ROUND");
    jobInfo.setGlueType("BEAN");
    jobInfo.setExecutorBlockStrategy("SERIAL_EXECUTION");
    jobInfo.setExecutorFailStrategy("NULL");
    jobInfo.setExecutorParam("");
    jobInfo.setGlueRemark("");
    jobInfo.setGlueSource("");
    jobInfo.setExecuteTimeout(0);
    return jobInfo;
  }

}
