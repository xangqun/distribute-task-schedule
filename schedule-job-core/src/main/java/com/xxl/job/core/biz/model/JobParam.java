/**
 * @Probject Name: starnetwork-job-core
 * @Path: com.xxl.job.core.biz.modelJobParam.java
 * @Create By jakic
 * @Create In 2018年7月20日 下午9:29:04
 * TODO
 */
package com.xxl.job.core.biz.model;

import java.io.Serializable;

/**
 * @Class Name JobParam
 * @Author wangzhijie
 * @Create In 2018年7月20日
 */
public class JobParam implements Serializable {
  
  /**
   * @Field long serialVersionUID 
   */
  private static final long serialVersionUID = 5753562284988230417L;

  private String appName;
  
  private String executorHandler;
  
  public JobParam() {}

  /**
   * @param appName
   * @param executorHandler
   */
  public JobParam(String appName, String executorHandler) {
    super();
    this.appName = appName;
    this.executorHandler = executorHandler;
  }

  /**
   * @Return the String appName
   */
  public String getAppName() {
    return appName;
  }

  /**
   * @Param String appName to set
   */
  public void setAppName(String appName) {
    this.appName = appName;
  }

  /**
   * @Return the String executorHandler
   */
  public String getExecutorHandler() {
    return executorHandler;
  }

  /**
   * @Param String executorHandler to set
   */
  public void setExecutorHandler(String executorHandler) {
    this.executorHandler = executorHandler;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "JobParam [appName=" + appName + ", executorHandler=" + executorHandler + "]";
  }
  
  

}
