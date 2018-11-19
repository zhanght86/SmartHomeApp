package com.gatz.smarthomeapp.model.netty.global;

import android.content.res.Resources;

import com.gatz.smarthomeapp.model.netty.UserInfo;

import java.util.Map;

public class AppGlobal {

    private static AppGlobal instance = null;

    private Long intervalTime;

    private Long timeOut;

    private Integer nettyRetryConnectInterval;

    private Integer nettyLoginRequestTimeout;

    private String nettySslModel;

    private Integer nettyConnectTimeout;

    private String nettySaberIp;

    private Integer nettySaberPort;

    private Resources resources;

    private boolean isShowLog = true;//是否显示日志

    private int sessionTime;//响应超时时间

    private boolean gateway_reconnect;//网关是否重新连接

    //用户信息
    private Map<String, String> useinfoMap;
    //netty重连ip
    private String tempNettySaberIp;
    private UserInfo userInfo;

    private AppGlobal() {
        intervalTime = 60L;
        timeOut = 80L;
    }

    static {
        instance = new AppGlobal();
    }

    public static AppGlobal getInstance() {
        if (instance == null) {
            instance = new AppGlobal();
        }
        return AppGlobal.instance;
    }

    public Integer getNettyRetryConnectInterval() {
        return nettyRetryConnectInterval;
    }

    public void setNettyRetryConnectInterval(Integer nettyRetryConnectInterval) {
        this.nettyRetryConnectInterval = nettyRetryConnectInterval;
    }

    public Integer getNettyLoginRequestTimeout() {
        return nettyLoginRequestTimeout = nettyLoginRequestTimeout * 1000;
    }

    public void setNettyLoginRequestTimeout(Integer nettyLoginRequestTimeout) {
        this.nettyLoginRequestTimeout = nettyLoginRequestTimeout;
    }

    public String getNettySslModel() {
        return nettySslModel;
    }

    public void setNettySslModel(String nettySslModel) {
        this.nettySslModel = nettySslModel;
    }

    public Integer getNettyConnectTimeout() {
        return nettyConnectTimeout;
    }

    public void setNettyConnectTimeout(Integer nettyConnectTimeout) {
        this.nettyConnectTimeout = nettyConnectTimeout;
    }

    public String getNettySaberIp() {
        return nettySaberIp;
    }

    public void setNettySaberIp(String nettySaberIp) {
        this.nettySaberIp = nettySaberIp;
    }

    public Integer getNettySaberPort() {
        return nettySaberPort;
    }

    public void setNettySaberPort(Integer nettySaberPort) {
        this.nettySaberPort = nettySaberPort;
    }

    public Resources getResources() {
        return resources;
    }

    public static void setInstance(AppGlobal instance) {
        AppGlobal.instance = instance;
    }

    public boolean isShowLog() {
        return isShowLog;
    }

    public void setShowLog(boolean showLog) {
        isShowLog = showLog;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(int sessionTime) {
        this.sessionTime = sessionTime;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public String getTempNettySaberIp() {
        return tempNettySaberIp;
    }

    public void setTempNettySaberIp(String tempNettySaberIp) {
        this.tempNettySaberIp = tempNettySaberIp;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isGateway_reconnect() {
        return gateway_reconnect;
    }

    public void setGateway_reconnect(boolean gateway_reconnect) {
        this.gateway_reconnect = gateway_reconnect;
    }

    public Long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }
}
