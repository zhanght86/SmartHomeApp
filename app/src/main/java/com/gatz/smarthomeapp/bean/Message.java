package com.gatz.smarthomeapp.bean;

/**
 * Created by Debby on 2017/10/20.
 */

public class Message {
    private String content;
    private String type;
    private String time;
    private String title;
    private String isRead;
    private String imgUrl;
    private int id;

    public Message() {
    }

    public Message(String isRead, String title, String content) {
        this.content = content;
        this.title = title;
        this.isRead = isRead;
    }

    public Message(String content, String type, String time, String title, String isRead, String imgUrl, int id) {
        this.content = content;
        this.type = type;
        this.time = time;
        this.title = title;
        this.isRead = isRead;
        this.imgUrl = imgUrl;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
