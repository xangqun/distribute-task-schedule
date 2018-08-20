package com.xxl.job.admin.core.thread;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.schedule.XxlJobDynamicScheduler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * job registry instance
 * @author xuxueli 2016-10-02 19:10:24
 */
public class JobEurekaRegistryMonitorHelper {
	private static JobEurekaRegistryMonitorHelper instance = new JobEurekaRegistryMonitorHelper();
	private static Logger logger = LoggerFactory.getLogger(JobEurekaRegistryMonitorHelper.class);

	public static JobEurekaRegistryMonitorHelper getInstance(){
		return instance;
	}
	private static ConcurrentHashMap<String,XxlJobGroup> xxlJobGroupMap=new ConcurrentHashMap<>();

	private Thread registryThread;
	private volatile boolean toStop = false;
	public void start(){
		registryThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!toStop) {
						synchronized (this) {
							try {
							List<String> serviceList = XxlJobDynamicScheduler.discoveryClient.getServices();
//							logger.debug("discovery serviceList：" + JSON.toJSONString(serviceList));
							// auto registry group
							List<XxlJobGroup> groupList = XxlJobDynamicScheduler.xxlJobGroupDao.findByAddressType(2);
							int index = 1;
							for (XxlJobGroup group : groupList) {
								String groupName = group.getAppName();
								if (serviceList.contains(groupName)) {
									List<String> ips = new ArrayList<>();
									List<ServiceInstance> instances = XxlJobDynamicScheduler.discoveryClient.getInstances(groupName);
//									logger.debug("discovery groupName：" + groupName + "*****" + JSON.toJSONString(instances));
									for (ServiceInstance instance : instances) {
										ips.add(String.format("%s:%s", instance.getHost(), instance.getPort()));
									}
									group.setAddressList(StringUtils.join(ips, ","));
									group.setOldAddressList(group.getAddressList());
//									logger.debug("discovery getAddressList：" + groupName + "*****" + JSON.toJSONString(group.getAddressList()));
									putToMap(groupName, group);
									serviceList.remove(groupName);
									index++;
								} else {
									if (xxlJobGroupMap.get(groupName) == null) {
//										if (!xxlJobGroupMap.get(groupName).equals(group)) {
//											xxlJobGroupMap.put(groupName, group);
//											XxlJobDynamicScheduler.xxlJobGroupDao.update(xxlJobGroupMap.get(groupName));
//										}
//									} else {
//										List<XxlJobGroup> groups = XxlJobDynamicScheduler.xxlJobGroupDao.findByAppName(groupName);
//										if (groups != null && groups.size() > 0) {
//											for (XxlJobGroup groupx : groups) {
										group.setOldAddressList(group.getAddressList());
										group.setAddressList("");
										XxlJobDynamicScheduler.xxlJobGroupDao.update(group);
										xxlJobGroupMap.put(groupName, group);
//											}
										}
//									}
								}
							}

							for (String service : serviceList) {
								List<ServiceInstance> instances = XxlJobDynamicScheduler.discoveryClient.getInstances(service);
//								logger.debug("discovery groupNamex：" + service + "*****" + JSON.toJSONString(instances));
								XxlJobGroup xxlJobGroup = new XxlJobGroup();
								xxlJobGroup.setAppName(service);
								xxlJobGroup.setTitle(service);
								xxlJobGroup.setOrder(index);
								List<String> ips = new ArrayList<>();
								for (ServiceInstance instance : instances) {
									ips.add(String.format("%s:%s", instance.getHost(), instance.getPort()));
								}
								xxlJobGroup.setAddressList(StringUtils.join(ips, ","));
//								logger.debug("discovery getAddressListx：" + service + "*****" + JSON.toJSONString(xxlJobGroup.getAddressList()));
								xxlJobGroup.setAddressType(2);
								xxlJobGroupMap.put(service, xxlJobGroup);

								XxlJobDynamicScheduler.xxlJobGroupDao.save(xxlJobGroup);
							}

						} catch(Exception e){
							logger.error("job registry instance error:{}", e);
						}
					}
					try {
						TimeUnit.SECONDS.sleep(30);
					} catch (InterruptedException e) {
						logger.error("job registry instance error:{}", e);
					}
				}
			}
		},"JobEurekaRegistryMonitorHelper");
		registryThread.setDaemon(true);
		registryThread.start();
	}

	private void putToMap(String groupName,XxlJobGroup group){
		if(xxlJobGroupMap.get(groupName)!=null){
			if(!xxlJobGroupMap.get(groupName).equals(group)){
				xxlJobGroupMap.put(groupName,group);
				XxlJobDynamicScheduler.xxlJobGroupDao.update(group);
			}
		}else {
			xxlJobGroupMap.put(groupName,group);
			XxlJobDynamicScheduler.xxlJobGroupDao.update(group);
		}
	}

	public void toStop(){
		toStop = true;
		// interrupt and wait
		registryThread.interrupt();
		try {
			registryThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
