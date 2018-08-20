package com.xxl.job.core.biz;

import com.xxl.job.core.biz.model.JobInfo;
import com.xxl.job.core.biz.model.JobParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;

/**
 * @author xuxueli 2017-07-27 21:52:49
 */
public interface AdminBiz {

    public static final String MAPPING = "/job_schedule_trigger_api";
    public static final String SERVER_MAPPING = "/api";

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public ReturnT<String> callback(Object... callbackParamList);

    /**
     * registry
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    public ReturnT<String> registryRemove(RegistryParam registryParam);


    /**
     * trigger job for once
     *
     * @param jobId
     * @return
     */
    public ReturnT<String> triggerJob(int jobId);
    
    
    /**
     * 给其他应用动态添加job的接口
     * 
     * @Methods Name addJob
     * @Create In 2018年7月16日 By wangzhijie
     * @param jobInfo
     * @return ReturnT<String>
     */
    public ReturnT<Integer> addJob(JobInfo jobInfo);
    
    public ReturnT<Integer> addJobIfNotExist(JobInfo jobInfo);
    
    
    /**
     * 
     * 动态删除Job的接口
     * @Methods Name removeJob
     * @Create In 2018年7月20日 By wangzhijie
     * @param jobId
     * @return ReturnT<String>
     */
    public ReturnT<String> removeJob(int jobId);
    
    public ReturnT<String> removeJob(JobParam jobParam);
    
    public ReturnT<Boolean> checkJobExist(JobParam jobParam);
    
    
    /**
     * 暂停Job
     * @Methods Name pause
     * @Create In 2018年7月21日 By wangzhijie
     * @param jobParam
     * @return ReturnT<String>
     */
    public ReturnT<String> pause(JobParam jobParam);
    
    /**
     * 恢复执行job
     * @Methods Name reuse
     * @Create In 2018年7月21日 By wangzhijie
     * @param jobParam
     * @return ReturnT<String>
     */
    public ReturnT<String> reuse(JobParam jobParam);

}
