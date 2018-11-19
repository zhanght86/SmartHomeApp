package com.gatz.smarthomeapp.bean;

/**
 * Created by zhou on 2017/12/26.
 */
public class VersionBean {
    private String appType;
    private String version;
    private String url;

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public String getAppType() {
        return appType;
    }

    public String getUrl() {
        return url;
    }
}
