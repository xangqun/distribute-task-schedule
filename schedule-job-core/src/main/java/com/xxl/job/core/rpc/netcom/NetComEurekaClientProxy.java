/**
 * Copyright 2017-2025 Evergrande Group.
 */
package com.xxl.job.core.rpc.netcom;

import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.netcom.jetty.client.ScheduleClient;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author laixiangqun
 * @since 2018-6-29
 */
public class NetComEurekaClientProxy  implements FactoryBean<Object> {
    private static final Logger logger = LoggerFactory.getLogger(NetComEurekaClientProxy.class);

    // ---------------------- config ----------------------
    private Class<?> iface;
    private String serverAddress;
    private String accessToken;
    private ScheduleClient client;
    private String scheduleServerName;
    public NetComEurekaClientProxy(Class<?> iface, String serverAddress, String accessToken,ScheduleClient client,String scheduleServerName) {
        this.iface = iface;
        this.serverAddress = serverAddress;
        this.accessToken = accessToken;
        this.client=client;
        this.scheduleServerName=scheduleServerName;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread()
                        .getContextClassLoader(), new Class[] { iface },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                           if (Object.class.getName().equals(method.getDeclaringClass().getName())) {
                               logger.error(">>>>>>>>>>> xxl-rpc proxy class-method not support [{}.{}]", method.getDeclaringClass().getName(), method.getName());
                               throw new RuntimeException("xxl-rpc proxy class-method not support");
//                               return null;
                           }

                           // request
                           RpcRequest request = new RpcRequest();
                           request.setServerAddress(serverAddress);
                           request.setCreateMillisTime(System.currentTimeMillis());
                           request.setAccessToken(accessToken);
                           request.setClassName(method.getDeclaringClass().getName());
                           request.setMethodName(method.getName());
                           request.setParameterTypes(method.getParameterTypes());
                           request.setParameters(args);
                           RpcResponse response =null;
                           // send
                           if(scheduleServerName!=null){
                                response = client.post(serverAddress,null,request);
                           }else {
                               response = client.post(serverAddress,AdminBiz.MAPPING,request);
                           }

                           // valid response
                           if (response == null) {
                               logger.error(">>>>>>>>>>> xxl-rpc netty response not found.");
                               throw new Exception(">>>>>>>>>>> xxl-rpc netty response not found.");
                           }
                           if (response.isError()) {
                               throw new RuntimeException(response.getError());
                           } else {
                               return JSON.parseObject(JSON.toJSONString((Map) response.getResult()),ReturnT.class);
//                               return response.getResult();
                           }
                    }
                });
    }
    @Override
    public Class<?> getObjectType() {
        return iface;
    }
    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NetComEurekaClientProxy that = (NetComEurekaClientProxy) o;

        return new EqualsBuilder()
                .append(iface, that.iface)
                .append(serverAddress, that.serverAddress)
                .append(accessToken, that.accessToken)
                .append(client, that.client)
                .append(scheduleServerName, that.scheduleServerName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(iface)
                .append(serverAddress)
                .append(accessToken)
                .append(client)
                .append(scheduleServerName)
                .toHashCode();
    }
}
