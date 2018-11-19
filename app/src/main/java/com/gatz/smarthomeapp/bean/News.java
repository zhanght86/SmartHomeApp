package com.gatz.smarthomeapp.bean;

/**
 * Created by Debby on 2016/11/18.
 */
public class News {
    private String article;//新闻题目
    private String source;//来源
    private String icon;//图片
    private String detailurl;//详细地址

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }
}
