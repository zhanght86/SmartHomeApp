package com.gatz.smarthomeapp.bean;

/**
 * Created by zhouh on 2017/2/22.
 */
public class UserInfoBean {
    private String userName;
    private String psw;
    private String sessionId;
    private String roomId;//房间id
    private String communityId;//小区id
    private String bedroomId;
    private String unitId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public void setBedroomId(String bedroomId){
        this.bedroomId = bedroomId;
    }

    public String getBedroomId(){
        return this.bedroomId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitId(){
        return this.unitId;
    }

}
