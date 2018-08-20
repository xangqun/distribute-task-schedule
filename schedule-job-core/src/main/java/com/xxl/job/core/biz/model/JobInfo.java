package com.xxl.job.core.biz.model;

/**
 * @Class Name JobInfo
 * @Author wangzhijie
 * @Create In 2018年7月16日
 */
public class JobInfo {
  
  private int id;

  private String appName;

  private int jobGroup; // 执行器主键ID (JobKey.group)
  private String jobCron; // 任务执行CRON表达式 【base on quartz】
  private String jobDesc;

  private String author; // 负责人
  private String alarmEmail; // 报警邮件

  private String executorRouteStrategy; // 执行器路由策略
  private String executorHandler; // 执行器，任务Handler名称
  private String executorParam; // 执行器，任务参数
  private String executorBlockStrategy; // 阻塞处理策略
  private String executorFailStrategy; // 失败处理策略

  private String glueType; // GLUE类型 #com.xxl.job.core.glue.GlueTypeEnum
  private String glueSource; // GLUE源代码
  private String glueRemark; // GLUE备注

  private String childJobId; // 子任务ID，多个逗号分隔

  // copy from quartz
  private String jobStatus; // 任务状态 【base on quartz】
  
  private int executeTimeout;// 任务最多执行时间，超时后报警

  public int getJobGroup() {
    return jobGroup;
  }
  
  public JobInfo setJobGroup(int jobGroup) {
    this.jobGroup = jobGroup;
    return this;
  }

  public String getJobCron() {
    return jobCron;
  }

  public JobInfo setJobCron(String jobCron) {
    this.jobCron = jobCron;
    return this;
  }

  public String getJobDesc() {
    return jobDesc;
  }

  public JobInfo setJobDesc(String jobDesc) {
    this.jobDesc = jobDesc;
    return this;
  }

  public String getAuthor() {
    return author;
  }

  public JobInfo setAuthor(String author) {
    this.author = author;
    return this;
  }

  public String getAlarmEmail() {
    return alarmEmail;
  }

  public JobInfo setAlarmEmail(String alarmEmail) {
    this.alarmEmail = alarmEmail;
    return this;
  }

  public String getExecutorRouteStrategy() {
    return executorRouteStrategy;
  }

  public JobInfo setExecutorRouteStrategy(String executorRouteStrategy) {
    this.executorRouteStrategy = executorRouteStrategy;
    return this;
  }

  public String getExecutorHandler() {
    return executorHandler;
  }

  public JobInfo setExecutorHandler(String executorHandler) {
    this.executorHandler = executorHandler;
    return this;
  }

  public String getExecutorParam() {
    return executorParam;
  }

  public JobInfo setExecutorParam(String executorParam) {
    this.executorParam = executorParam;
    return this;
  }

  public String getExecutorBlockStrategy() {
    return executorBlockStrategy;
  }

  public JobInfo setExecutorBlockStrategy(String executorBlockStrategy) {
    this.executorBlockStrategy = executorBlockStrategy;
    return this;
  }

  public String getExecutorFailStrategy() {
    return executorFailStrategy;
  }

  public JobInfo setExecutorFailStrategy(String executorFailStrategy) {
    this.executorFailStrategy = executorFailStrategy;
    return this;
  }

  public String getGlueType() {
    return glueType;
  }

  public JobInfo setGlueType(String glueType) {
    this.glueType = glueType;
    return this;
  }

  public String getGlueSource() {
    return glueSource;
  }

  public JobInfo setGlueSource(String glueSource) {
    this.glueSource = glueSource;
    return this;
  }

  public String getGlueRemark() {
    return glueRemark;
  }

  public JobInfo setGlueRemark(String glueRemark) {
    this.glueRemark = glueRemark;
    return this;
  }


  public String getChildJobId() {
    return childJobId;
  }

  public JobInfo setChildJobId(String childJobId) {
    this.childJobId = childJobId;
    return this;
  }

  public String getJobStatus() {
    return jobStatus;
  }

  public JobInfo setJobStatus(String jobStatus) {
    this.jobStatus = jobStatus;
    return this;
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
  public JobInfo setAppName(String appName) {
    this.appName = appName;
    return this;
  }

  

  /**
   * @Return the int id
   */
  public int getId() {
    return id;
  }

  /**
   * @Param int id to set
   */
  public JobInfo setId(int id) {
    this.id = id;
    return this;
  }


  /**
   * @Return the int executeTimeout
   */
  public int getExecuteTimeout() {
    return executeTimeout;
  }

  /**
   * @Param int executeTimeout to set
   */
  public JobInfo setExecuteTimeout(int executeTimeout) {
    this.executeTimeout = executeTimeout;
    return this;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "JobInfo [id=" + id + ", appName=" + appName + ", jobGroup=" + jobGroup + ", jobCron="
        + jobCron + ", jobDesc=" + jobDesc + ", author=" + author + ", alarmEmail=" + alarmEmail
        + ", executorRouteStrategy=" + executorRouteStrategy + ", executorHandler="
        + executorHandler + ", executorParam=" + executorParam + ", executorBlockStrategy="
        + executorBlockStrategy + ", executorFailStrategy=" + executorFailStrategy + ", glueType="
        + glueType + ", glueSource=" + glueSource + ", glueRemark=" + glueRemark + ", childJobId="
        + childJobId + ", jobStatus=" + jobStatus + ", executeTimeout=" + executeTimeout + "]";
  }

  
}
