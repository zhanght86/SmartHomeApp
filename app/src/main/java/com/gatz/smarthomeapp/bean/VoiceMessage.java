package com.gatz.smarthomeapp.bean;

import java.util.Date;

/**
 * Created by Debby on 2016/11/10.
 */
public class VoiceMessage {
    private String message;
    private int type;
    private int iMsgViewType;
    private String url ;
    private Date date;

    public VoiceMessage() {
    }

    public VoiceMessage(String message, int type, int iMsgViewType, Date date) {
        this.message = message;
        this.type = type;
        this.iMsgViewType = iMsgViewType;
        this.date = date;
    }

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;// 收到对方的消息
        int IMVT_TO_MSG = 1;// 自己发送出去的消息
        int IMVT_COM_MSG_URL = 2;//收到对方消息带URL
    }


    public static interface  Type{
       int TEXT = 1;
       int URL = 2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getiMsgViewType() {
        return iMsgViewType;
    }

    public void setiMsgViewType(int iMsgViewType) {
        this.iMsgViewType = iMsgViewType;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
