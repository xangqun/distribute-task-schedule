package com.xxl.job.admin.service.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.xxl.job.admin.core.enums.ExecutorFailStrategyEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobRegistryDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.JobInfo;
import com.xxl.job.core.biz.model.JobParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;

/**
 * @author xuxueli 2017-07-27 21:54:20
 */
@Service
public class AdminBizImpl implements AdminBiz {
    private static Logger logger = LoggerFactory.getLogger(AdminBizImpl.class);

    @Resource
    public XxlJobLogDao xxlJobLogDao;
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private XxlJobRegistryDao xxlJobRegistryDao;
    
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobService xxlJobServiceImpl;
    @Resource
    private XxlJobService xxlJobService;

    @Override
    public ReturnT<String> callback(Object... callbackParamList) {
        try {
            for (Object handleCallbackParam: callbackParamList) {
                ReturnT<String> callbackResult =null;
                if(handleCallbackParam instanceof HandleCallbackParam){
                    callbackResult = callback((HandleCallbackParam)handleCallbackParam);
                }else {
                    JSONArray jsonArray= (JSONArray) handleCallbackParam;
                    for(int index=0;index<jsonArray.size();index++){
                        JSONObject jsonObject= jsonArray.getObject(index,JSONObject.class);
                        HandleCallbackParam handleCallbackParam1=  JSON.parseObject(jsonObject.toJSONString(),HandleCallbackParam.class);
                        callbackResult = callback(handleCallbackParam1);
                    }
                }
                logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                        (callbackResult.getCode()==IJobHandler.SUCCESS.getCode()?"success":"fail"), handleCallbackParam, callbackResult);

            }
            return ReturnT.SUCCESS;
        }catch (Exception e){
            return ReturnT.FAIL;
        }

    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        XxlJobLog log = xxlJobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        // trigger success, to trigger child job
        String callbackMsg = null;
        if (IJobHandler.SUCCESS.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
            XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
            if (xxlJobInfo!=null && StringUtils.isNotBlank(xxlJobInfo.getChildJobId())) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_child_run") +"<<<<<<<<<<< </span><br>";

                String[] childJobIds = xxlJobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId = (StringUtils.isNotBlank(childJobIds[i]) && StringUtils.isNumeric(childJobIds[i]))?Integer.valueOf(childJobIds[i]):-1;
                    if (childJobId > 0) {
                        ReturnT<String> triggerChildResult = xxlJobServiceImpl.triggerJob(childJobId);

                        // add msg
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode()==ReturnT.SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        }else {
            boolean ifHandleRetry = false;
            if (IJobHandler.FAIL_RETRY.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
                ifHandleRetry = true;
            } else {
                XxlJobInfo xxlJobInfo = xxlJobInfoDao.loadById(log.getJobId());
                if (ExecutorFailStrategyEnum.FAIL_HANDLE_RETRY.name().equals(xxlJobInfo.getExecutorFailStrategy())) {
                    ifHandleRetry = true;
                }
            }
            if (ifHandleRetry){
                ReturnT<String> retryTriggerResult = xxlJobService.triggerJob(log.getJobId());
                callbackMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_fail_handle_retry") +"<<<<<<<<<<< </span><br>";

                callbackMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_msg1"),
                        (retryTriggerResult.getCode()==ReturnT.SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")), retryTriggerResult.getMsg());
            }
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg()!=null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        xxlJobLogDao.updateHandleInfo(log);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        int ret = xxlJobRegistryDao.registryUpdate(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret < 1) {
            xxlJobRegistryDao.registrySave(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        xxlJobRegistryDao.registryDelete(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> triggerJob(int jobId) {
        return xxlJobServiceImpl.triggerJob(jobId);
    }


    @Override
    public ReturnT<Integer> addJob(JobInfo jobInfo) {
        ReturnT<Integer> result =null;
        try {
            String appName = jobInfo.getAppName();
            XxlJobGroup group = findJobGroup(appName);
            if (group != null) {
                jobInfo.setJobGroup(group.getId());
            } else {
                String msg = MessageFormat.format(I18nUtil.getString("jobinfo_group_not_exist"), appName);
                return new ReturnT<Integer>(ReturnT.FAIL_CODE, msg, -1);
            }
            XxlJobInfo xxlJobInfo = new XxlJobInfo();
            BeanUtils.copyProperties(jobInfo, xxlJobInfo);
            result = xxlJobServiceImpl.addAndReturnJobId(xxlJobInfo);
            if (ReturnT.SUCCESS_CODE == result.getCode()) {
                logger.info("addJob success, jobInfo:{}", jobInfo);
            } else {
                logger.warn("addJob fail!, jobInfo:{}", jobInfo);
            }
        }catch (Exception e){
            result=new ReturnT(500, (String)null);
        }
      return result;
    }
    

    @Override
    public ReturnT<Integer> addJobIfNotExist(JobInfo jobInfo) {
        try {
            String appName = jobInfo.getAppName();
            XxlJobGroup group = findJobGroup(appName);
            if (group != null) {
                jobInfo.setJobGroup(group.getId());
            } else {
                String msg = MessageFormat.format(I18nUtil.getString("jobinfo_group_not_exist"), appName);
                return new ReturnT<Integer>(ReturnT.FAIL_CODE, msg, -1);
            }

            List<XxlJobInfo> jobs = this.findJobs(group.getId(), jobInfo.getExecutorHandler());
            if (jobs.size() > 0) {
                String msg = MessageFormat.format(I18nUtil.getString("jobinfo_job_already_exist"), appName, jobInfo.getExecutorHandler());
                return new ReturnT<Integer>(ReturnT.SUCCESS_CODE, msg, -1);
            } else {
                XxlJobInfo xxlJobInfo = new XxlJobInfo();
                BeanUtils.copyProperties(jobInfo, xxlJobInfo);
                ReturnT<Integer> result = xxlJobServiceImpl.addAndReturnJobId(xxlJobInfo);
                if (ReturnT.SUCCESS_CODE == result.getCode()) {
                    logger.info("addJob success, jobInfo:{}", jobInfo);
                } else {
                    logger.warn("addJob fail!, jobInfo:{}", jobInfo);
                }
                return result;
            }
        }catch (Exception e){
            return new ReturnT(500, null,-1);
        }
    }
    
    private XxlJobGroup findJobGroup(String appName) {
      List<XxlJobGroup> jobGroups = xxlJobGroupDao.findByAppName(appName);
      if (jobGroups.size() <= 0) {
        return null;
      } else {
        return jobGroups.get(0);
      }
    }
    
    @Override
    public ReturnT<String> removeJob(int jobId) {
        try {
          ReturnT<String> ret = xxlJobServiceImpl.remove(jobId);
          if (ReturnT.FAIL_CODE == ret.getCode()) {
            logger.warn("removeJob fail! jobId:{}", jobId);
          } else {
            logger.info("removeJob success, jobId:{}", jobId);
          }
            return ret;
        }catch (Exception e){
            return ReturnT.FAIL;
        }
    }


    @Override
    public ReturnT<String> removeJob(JobParam jobParam) {
        try {
          XxlJobGroup jobGroup = this.findJobGroup(jobParam.getAppName());
          if (jobGroup == null) {
            String msg = MessageFormat.format(I18nUtil.getString("jobinfo_group_not_exist"), jobParam.getAppName());
            logger.warn("removeJob ".concat(msg));
            return new ReturnT<String>(ReturnT.SUCCESS_CODE, msg);
          }
          List<XxlJobInfo> jobInfos = findJobs(jobGroup.getId(), jobParam.getExecutorHandler());
          if (jobInfos != null) {
            for (XxlJobInfo jobInfo : jobInfos) {
              ReturnT<String> ret = xxlJobServiceImpl.remove(jobInfo.getId());
              if (ReturnT.FAIL_CODE == ret.getCode()) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "error!");
              }
            }
          }
          return ReturnT.SUCCESS;
        }catch (Exception e){
            return ReturnT.FAIL;
        }
    }


    @Override
    public ReturnT<Boolean> checkJobExist(JobParam jobParam) {
        try {
          XxlJobGroup jobGroup = this.findJobGroup(jobParam.getAppName());
          if (jobGroup == null) {
            String msg = MessageFormat.format(I18nUtil.getString("jobinfo_group_not_exist"), jobParam.getAppName());
            logger.warn("checkJobExist ".concat(msg));
            return new ReturnT<Boolean>(ReturnT.SUCCESS_CODE, msg, false);
          }
          List<XxlJobInfo> jobInfos = findJobs(jobGroup.getId(), jobParam.getExecutorHandler());
          if (jobInfos != null && jobInfos.size() > 0) {
            return new ReturnT<Boolean>(true);
          } else {
            String msg = String.format("jobParam:%s not exist!", jobParam);
            logger.info("checkJobExist ".concat(msg));
            return new ReturnT<Boolean>(ReturnT.SUCCESS_CODE, msg, false);
          }
        }catch (Exception e){
            return new ReturnT(500, null,false);
        }
    }
    
    private List<XxlJobInfo> findJobs(int jobGroupId, String executorHandler) {
      List<XxlJobInfo> jobInfos = xxlJobInfoDao.getJobsByGroupAndExecutorHandler(jobGroupId, executorHandler);
      return jobInfos;
    }

    @Override
    public ReturnT<String> pause(JobParam jobParam) {
        try {
          ReturnT<String> result = ReturnT.SUCCESS;
          XxlJobGroup jobGroup = this.findJobGroup(jobParam.getAppName());
          if (jobGroup == null) {
            String msg = MessageFormat.format(I18nUtil.getString("jobinfo_group_not_exist"), jobParam.getAppName());
            logger.warn("pause job ".concat(msg));
            return new ReturnT<String>(ReturnT.SUCCESS_CODE, msg);
          }
          List<XxlJobInfo> jobs = findJobs(jobGroup.getId(), jobParam.getExecutorHandler());
          if (jobs != null && jobs.size() > 0) {
            for (XxlJobInfo job : jobs) {
              ReturnT<String> ret = this.xxlJobServiceImpl.pause(job.getId());
              if (ReturnT.FAIL_CODE == ret.getCode()) {
                result = ret;
              }
            }
          } else {
            //如果jobParam对应的数据查不到Job，这里也返回成功，只是msg里带有信息
            String msg = String.format("jobParam:%s not exist!", jobParam);
            logger.warn("pause job ".concat(msg));
            result = new ReturnT<String>(ReturnT.SUCCESS_CODE, msg);
          }
          return result;
        }catch (Exception e){
            return ReturnT.FAIL;
        }
    }

    @Override
    public ReturnT<String> reuse(JobParam jobParam) {
        try {
            ReturnT<String> result = ReturnT.SUCCESS;
              XxlJobGroup jobGroup = this.findJobGroup(jobParam.getAppName());
              if (jobGroup == null) {
                String msg = MessageFormat.format(I18nUtil.getString("jobinfo_group_not_exist"), jobParam.getAppName());
                logger.warn("pause job ".concat(msg));
                return new ReturnT<String>(ReturnT.SUCCESS_CODE, msg);
              }
              List<XxlJobInfo> jobs = findJobs(jobGroup.getId(), jobParam.getExecutorHandler());
              if (jobs != null && jobs.size() > 0) {
                for (XxlJobInfo job : jobs) {
                  ReturnT<String> ret = this.xxlJobServiceImpl.resume(job.getId());
                  if (ReturnT.FAIL_CODE == ret.getCode()) {
                    result = ret;
                  }
                }
              } else {
                String msg = String.format("jobParam:%s not exist!", jobParam);
                logger.warn("reuse job ".concat(msg));
                result = new ReturnT<String>(ReturnT.FAIL_CODE, msg);
              }
              return result;
        }catch (Exception e){
            return ReturnT.FAIL;
        }
    }



}
