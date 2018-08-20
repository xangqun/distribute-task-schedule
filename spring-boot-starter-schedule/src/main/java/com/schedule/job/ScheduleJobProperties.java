package com.schedule.job;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schedule.job")
public class ScheduleJobProperties {
    //admin服务器列表
    private String adminAddresses;
    //eureka调度服务名
    private String scheduleServerName;
    //执行器服务名
    private String appName;
    //执行器ip
    private String ip;
    //执行器端口
    private int port;
    //通信秘钥
    private String accessToken;
    //日志地址
    private String logPath;

    private Integer logRetentionDays;

    public Integer getLogRetentionDays() {
        return logRetentionDays;
    }

    public void setLogRetentionDays(Integer logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    public String getAdminAddresses() {
        return adminAddresses;
    }

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public String getScheduleServerName() {
        return scheduleServerName;
    }

    public void setScheduleServerName(String scheduleServerName) {
        this.scheduleServerName = scheduleServerName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}