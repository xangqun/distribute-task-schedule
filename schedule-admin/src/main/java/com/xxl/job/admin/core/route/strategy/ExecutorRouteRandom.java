package com.xxl.job.admin.core.route.strategy;

import com.xxl.job.admin.core.route.ExecutorRouter;
import com.xxl.job.admin.core.trigger.XxlJobTrigger;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteRandom extends ExecutorRouter {

    private static Random localRandom = new Random();
    @Override
    public String route(int jobId, ArrayList<String> addressList) {
        // Collections.shuffle(addressList);
        return addressList.get(localRandom.nextInt(addressList.size()));
    }

    @Override
    public ReturnT<String> abstractRouteRun(TriggerParam triggerParam, ArrayList<String> addressList) {
        return null;
    }

}
