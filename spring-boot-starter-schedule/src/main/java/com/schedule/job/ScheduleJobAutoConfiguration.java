package com.schedule.job;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.rpc.netcom.jetty.client.ScheduleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.xxl.job.core","com.schedule.job"})
@EnableConfigurationProperties(ScheduleJobProperties.class)
public class ScheduleJobAutoConfiguration {
	protected static final Logger logger = LoggerFactory.getLogger(ScheduleJobAutoConfiguration.class);

	@Autowired
	private ScheduleJobProperties scheduleJobProperties;

	@Autowired
	public ScheduleClient scheduleClient;

	@Value("spring.application.name")
	private String appName;
	/**
	 * 注册多个配置
	 * @return
	 * @throws Exception
	 */
	@Bean(initMethod = "start", destroyMethod = "destroy")
	public XxlJobExecutor xxlJobExecutor() {
		logger.info(">>>>>>>>>>> schedule job config init.");
		XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
		xxlJobExecutor.setAdminAddresses(scheduleJobProperties.getAdminAddresses());
		xxlJobExecutor.setScheduleServerName(scheduleJobProperties.getScheduleServerName()==null?"scheduler-admin":scheduleJobProperties.getScheduleServerName());
		xxlJobExecutor.setAppName(scheduleJobProperties.getAppName()==null?appName:scheduleJobProperties.getAppName());
		xxlJobExecutor.setIp(scheduleJobProperties.getIp());
		xxlJobExecutor.setPort(scheduleJobProperties.getPort());
		xxlJobExecutor.setAccessToken(scheduleJobProperties.getAccessToken()==null?"sdfadf23423423asdf452325343fgsfdgs":scheduleJobProperties.getAccessToken());
		xxlJobExecutor.setLogPath(scheduleJobProperties.getLogPath());
		xxlJobExecutor.setLogRetentionDays(scheduleJobProperties.getLogRetentionDays()==null?7:scheduleJobProperties.getLogRetentionDays());
		xxlJobExecutor.setScheduleClientx(scheduleClient);
		return xxlJobExecutor;
	}
	
}
