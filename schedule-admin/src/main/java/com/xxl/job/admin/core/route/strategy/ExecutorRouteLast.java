package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.admin.core.trigger.XxlJobTrigger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.ArrayList;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteLast extends ExecutorRouter {
    @Override
    public String route(int jobId, ArrayList<String> addressList) {
        return addressList.get(addressList.size()-1);
    }

    @Override
    public ReturnT<String> abstractRouteRun(TriggerParam triggerParam, ArrayList<String> addressList) {
        return null;
    }
}
