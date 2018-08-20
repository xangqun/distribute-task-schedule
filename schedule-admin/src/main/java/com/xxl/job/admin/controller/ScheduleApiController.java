/**
 * Copyright 2017-2025 schedule Group.
 */
package com.xxl.job.admin.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xxl.job.admin.controller.annotation.PermessionLimit;
import com.xxl.job.admin.controller.resolver.BaseController;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.rpc.codec.RpcRequest;
import com.xxl.job.core.rpc.codec.RpcResponse;
import com.xxl.job.core.rpc.netcom.NetComServerFactory;
import com.xxl.job.core.util.HttpClientUtil;

/**
 * @author laixiangqun
 * @since 2018-6-29
 */
@RestController
public class ScheduleApiController extends BaseController {
  private static final Logger logger = LoggerFactory.getLogger(ScheduleApiController.class);

  @RequestMapping(value = AdminBiz.SERVER_MAPPING, method = RequestMethod.POST)
  @PermessionLimit(limit = false)
  public void schedule(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.info("schedule, request:{}", request);

    // invoke
    RpcResponse rpcResponse = doInvoke(request);
    response.setContentType("application/json;charset=utf-8");
    response.setStatus(HttpServletResponse.SC_OK);

    OutputStream out = response.getOutputStream();
    out.write(JSON.toJSONString(rpcResponse, SerializerFeature.WriteMapNullValue).getBytes());

    out.flush();
  }

  private RpcResponse doInvoke(HttpServletRequest request) {
    try {
      // deserialize request
      byte[] requestBytes = HttpClientUtil.readBytes(request);
      new String(requestBytes, "UTF-8");
      if (requestBytes == null || requestBytes.length == 0) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setError("RpcRequest byte[] is null");
        return rpcResponse;
      }
      RpcRequest rpcRequest = JSON.parseObject(new String(requestBytes), RpcRequest.class);// RpcRequest)
                                                                                           // HessianSerializer.deserialize(requestBytes,
                                                                                           // RpcRequest.class);
      Object[] params = rpcRequest.getParameters();
      int count = params == null ? 0 : params.length;
      Object[] parameters = new Object[count];
      for (int index = 0; params != null && index < params.length; index++) {
        parameters[index] =
            JSON.parseObject(params[index].toString(), rpcRequest.getParameterTypes()[index]);
      }
      rpcRequest.setParameters(parameters);
      // RpcRequest rpcRequest =(RpcRequest) HessianSerializer.deserialize(requestBytes,
      // RpcRequest.class);
      // invoke
      RpcResponse rpcResponse = NetComServerFactory.invokeService(rpcRequest, null);
      return rpcResponse;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);

      RpcResponse rpcResponse = new RpcResponse();
      rpcResponse.setError("Server-error:" + e.getMessage());
      return rpcResponse;
    }
  }
}
