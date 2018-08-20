package com.xxl.job.admin.core.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public class XxlJobGroup {

    private int id;
    private String appName;
    private String title;
    private int order;
    private int addressType;    // 执行器地址类型：0=自动注册、1=手动录入、2=eureka注册
    private String addressList;    // 执行器地址列表，多地址逗号分隔(手动录入)
    private String oldAddressList;    // 历史执行器地址列表，多地址逗号分隔(手动录入)

    // registry list
    private List<String> registryList;  // 执行器地址列表(系统注册)
    public List<String> getRegistryList() {
        if (StringUtils.isNotBlank(addressList)) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public String getOldAddressList() {
        return oldAddressList;
    }

    public void setOldAddressList(String oldAddressList) {
        this.oldAddressList = oldAddressList;
    }

    @Override
    public String toString() {
        return "XxlJobGroup{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", title='" + title + '\'' +
                ", order=" + order +
                ", addressType=" + addressType +
                ", addressList='" + addressList + '\'' +
                ", oldAddressList='" + oldAddressList + '\'' +
                ", registryList=" + registryList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        XxlJobGroup group = (XxlJobGroup) o;

        return new EqualsBuilder()
                .append(id, group.id)
                .append(order, group.order)
                .append(addressType, group.addressType)
                .append(appName, group.appName)
                .append(title, group.title)
                .append(addressList, group.addressList)
                .append(oldAddressList, group.oldAddressList)
                .append(registryList, group.registryList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(appName)
                .append(title)
                .append(order)
                .append(addressType)
                .append(addressList)
                .append(oldAddressList)
                .append(registryList)
                .toHashCode();
    }
}
