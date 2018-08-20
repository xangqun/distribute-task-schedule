/**
 * @Probject Name: spring-boot-starter-starnetworkjob
 * @Path: com.starnetwork.job.apiJobApi.java
 * @Create By jakic
 * @Create In 2018年7月19日 上午9:07:40 TODO
 */
package com.schedule.job.api;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.JobInfo;
import com.xxl.job.core.biz.model.JobParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.springframework.beans.factory.FactoryBean;

/**
 * @Class Name JobApi
 * @Author wangzhijie
 * @Create In 2018年7月19日
 */
public class JobApi {
  
  protected static final Logger logger = LoggerFactory.getLogger(JobApi.class);

  public static ReturnT<Integer> addJob(JobInfo jobInfo) {
    ReturnT<Integer> addJobResult = new ReturnT<Integer>(ReturnT.FAIL_CODE, "fail", -1);
    String msg = validate(jobInfo);
    if (msg != null) {
      addJobResult.setMsg(msg);
      return addJobResult;
    }
    for (FactoryBean adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        addJobResult = ((AdminBiz)adminBiz.getObject()).addJob(jobInfo);
        if (addJobResult != null && ReturnT.SUCCESS_CODE == addJobResult.getCode()) {
          logger.info("addJob success, jobInfo:{}", jobInfo);
          break;
        }
      } catch (Exception e) {
        logger.error(String.format("addJob exception, jobInfo:{}", jobInfo), e);
      }
    }
    if (ReturnT.FAIL_CODE == addJobResult.getCode()) {
      logger.error("addJob error, jobInfo:{}, addJobResult:{}", jobInfo, addJobResult);
    }
    return addJobResult;
  }
  

  public static ReturnT<Integer> addJobIfNotExist(JobInfo jobInfo) {
    ReturnT<Integer> addJobResult = new ReturnT<Integer>(ReturnT.FAIL_CODE, "fail", -1);
    String msg = validate(jobInfo);
    if (msg != null) {
      addJobResult.setMsg(msg);
      return addJobResult;
    }
    for (FactoryBean adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        addJobResult = ((AdminBiz)adminBiz.getObject()).addJobIfNotExist(jobInfo);
        if (addJobResult != null && ReturnT.SUCCESS_CODE == addJobResult.getCode()) {
          logger.info("addJobIfNotExist success, jobInfo:{}", jobInfo);
          break;
        }
      } catch (Exception e) {
        logger.error(String.format("addJob exception, jobInfo:{}", jobInfo), e);
      }
    }
    if (ReturnT.FAIL_CODE == addJobResult.getCode()) {
      logger.error("addJob error, jobInfo:{}, addJobResult:{}", jobInfo, addJobResult);
    }
    return addJobResult;
  }
  
  
  private static String validate(JobInfo jobInfo) {
    String appName = jobInfo.getAppName();
    if (StringUtils.isBlank(appName)) {
      return "appName can not be empty!";
    } 
    if (StringUtils.isBlank(jobInfo.getJobCron())) {
      return "jobCron can not be empty";
    }
    if (StringUtils.isBlank(jobInfo.getJobDesc())) {
      return "jobDesc can not be empty";
    } 
    if (StringUtils.isBlank(jobInfo.getAuthor())) {
      return "author can not be empty";
    } 
    if (StringUtils.isBlank(jobInfo.getExecutorRouteStrategy())) {
      return "executorRouteStrategy can not be empty";
    }     
    if (ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), null) == null) {
      return "executorBlockStrategy is unvalid";
    } 
    if (StringUtils.isBlank(jobInfo.getExecutorFailStrategy())) {
      return "executorFailStrategy can not be empty";
    }
    if (GlueTypeEnum.match(jobInfo.getGlueType()) == null) {
      return "glueType is unvalid";
    } 
    if (GlueTypeEnum.BEAN==GlueTypeEnum.match(jobInfo.getGlueType()) && StringUtils.isBlank(jobInfo.getExecutorHandler())) {
      return "executorHandler can not be empty";
    }          
    return null;
  }
  
  public static ReturnT<String> removeJob(int jobId) {
    ReturnT<String> addJobResult = ReturnT.FAIL;
    for (FactoryBean adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        addJobResult = ((AdminBiz)adminBiz.getObject()).removeJob(jobId);
        if (addJobResult != null && ReturnT.SUCCESS_CODE == addJobResult.getCode()) {
          logger.info("removeJob success, jobId:{}", jobId);
          break;
        }
      } catch (Exception e) {
        logger.error(String.format("removeJob exception, jobId:{}", jobId), e);
      }
    }
    if (ReturnT.FAIL_CODE == addJobResult.getCode()) {
      logger.error("removeJob error, jobId:{}, addJobResult:{}", jobId, addJobResult);
    }
    return addJobResult;
  }
  
  public static ReturnT<Boolean> checkJobExist(String appName, String executorHandler) {
    ReturnT<Boolean> addJobResult = new ReturnT<Boolean>(ReturnT.FAIL_CODE, "", false);
    if (StringUtils.isBlank(appName)) {
      addJobResult.setMsg("appName can not be empty!");
      return addJobResult;
    }
    if (StringUtils.isBlank(executorHandler)) {
      addJobResult.setMsg("executorHandler can not be empty!");
      return addJobResult;
    }
    for (FactoryBean adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        addJobResult = ((AdminBiz)adminBiz.getObject()).checkJobExist(new JobParam(appName, executorHandler));
        if (addJobResult != null && ReturnT.SUCCESS_CODE == addJobResult.getCode()) {
          logger.info("checkJobExist success, appName:{}, executorHandler:{}", appName, executorHandler);
          break;
        }
      } catch (Exception e) {
        logger.error(String.format("checkJobExist exception, appName:{}, executorHandler:{}", appName, executorHandler), e);
        addJobResult = new ReturnT<Boolean>(ReturnT.FAIL_CODE, e.getMessage(), false);
      }
    }
    if (ReturnT.FAIL_CODE == addJobResult.getCode()) {
      logger.error("checkJobExist error, appName:{}, executorHandler:{}", appName, executorHandler);
    }
    return addJobResult;
  }
  
  public static ReturnT<String> removeJob(String appName, String executorHandler) {
    ReturnT<String> addJobResult = new ReturnT<String>(ReturnT.FAIL_CODE, "");
    if (StringUtils.isBlank(appName)) {
      addJobResult.setMsg("appName can not be empty!");
      return addJobResult;
    }
    if (StringUtils.isBlank(executorHandler)) {
      addJobResult.setMsg("executorHandler can not be empty!");
      return addJobResult;
    }
    for (FactoryBean adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        addJobResult = ((AdminBiz)adminBiz.getObject()).removeJob(new JobParam(appName, executorHandler));
        if (addJobResult != null && ReturnT.SUCCESS_CODE == addJobResult.getCode()) {
          logger.info("removeJob success, appName:{}, executorHandler:{}", appName, executorHandler);
          break;
        }
      } catch (Exception e) {
        logger.error(String.format("removeJob exception, appName:{}, executorHandler:{}", appName, executorHandler), e);
        addJobResult = new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
      }
    }
    if (ReturnT.FAIL_CODE == addJobResult.getCode()) {
      logger.error("removeJob error, appName:{}, executorHandler:{}", appName, executorHandler);
    }
    return addJobResult;
  }
  
  public static ReturnT<String> pauseJob(String appName, String executorHandler) {
    ReturnT<String> addJobResult = new ReturnT<String>(ReturnT.FAIL_CODE, "");
    if (StringUtils.isBlank(appName)) {
      addJobResult.setMsg("appName can not be empty!");
      return addJobResult;
    }
    if (StringUtils.isBlank(executorHandler)) {
      addJobResult.setMsg("executorHandler can not be empty!");
      return addJobResult;
    }
    for (FactoryBean adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        addJobResult = ((AdminBiz)adminBiz.getObject()).pause(new JobParam(appName, executorHandler));
        if (addJobResult != null && ReturnT.SUCCESS_CODE == addJobResult.getCode()) {
          logger.info("pauseJob success, appName:{}, executorHandler:{}", appName, executorHandler);
          break;
        }
      } catch (Exception e) {
        logger.error(String.format("pauseJob exception, appName:{}, executorHandler:{}", appName, executorHandler), e);
        addJobResult = new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
      }
    }
    if (ReturnT.FAIL_CODE == addJobResult.getCode()) {
      logger.error("pauseJob error, appName:{}, executorHandler:{}", appName, executorHandler);
    }
    return addJobResult;
  }
  
  
  public static ReturnT<String> reuseJob(String appName, String executorHandler) {
    ReturnT<String> addJobResult = new ReturnT<String>(ReturnT.FAIL_CODE, "");
    if (StringUtils.isBlank(appName)) {
      addJobResult.setMsg("appName can not be empty!");
      return addJobResult;
    }
    if (StringUtils.isBlank(executorHandler)) {
      addJobResult.setMsg("executorHandler can not be empty!");
      return addJobResult;
    }
    for (FactoryBean adminBiz : XxlJobExecutor.getAdminBizList()) {
      try {
        addJobResult = ((AdminBiz)adminBiz.getObject()).reuse(new JobParam(appName, executorHandler));
        if (addJobResult != null && ReturnT.SUCCESS_CODE == addJobResult.getCode()) {
          logger.info("reuseJob success, appName:{}, executorHandler:{}", appName, executorHandler);
          break;
        }
      } catch (Exception e) {
        logger.error(String.format("reuseJob exception, appName:{}, executorHandler:{}", appName, executorHandler), e);
        addJobResult = new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
      }
    }
    if (ReturnT.FAIL_CODE == addJobResult.getCode()) {
      logger.error("reuseJob error, appName:{}, executorHandler:{}", appName, executorHandler);
    }
    return addJobResult;
  }

}
