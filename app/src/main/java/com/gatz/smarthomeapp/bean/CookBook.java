package com.gatz.smarthomeapp.bean;

/**
 * 菜谱类
 * Created by Debby on 2016/11/18.
 */
public class CookBook {
    private String name;//名字
    private String icon;//图标
    private String info;//信息
    private String detailurl;//具体链接

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }
}
