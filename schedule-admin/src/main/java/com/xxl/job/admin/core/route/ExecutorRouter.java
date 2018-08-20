package com.xxl.job.admin.core.route;

import com.xxl.job.admin.core.trigger.XxlJobTrigger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by xuxueli on 17/3/10.
 */
public abstract class ExecutorRouter {
    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    /**
     * 路由策略
     * @param jobId
     * @param addressList
     * @return
     */
    public abstract String route(int jobId, ArrayList<String> addressList);

    /**
     * route run executor
     *
     * @param triggerParam
     * @param addressList
     * @return  ReturnT.content: final address
     */
    public ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList){
        // address
        String address = route(triggerParam.getJobId(), addressList);
        ReturnT<String> runResult= abstractRouteRun(triggerParam,addressList);
        if(runResult!=null){
            return runResult;
        }
        // run executor
        runResult = XxlJobTrigger.runExecutor(triggerParam, address);
        runResult.setContent(address);
        return runResult;
    }


    /**
     * 自定义运行实现
     * @param triggerParam
     * @param addressList
     * @return
     */
    public abstract ReturnT<String> abstractRouteRun(TriggerParam triggerParam, ArrayList<String> addressList);

}
