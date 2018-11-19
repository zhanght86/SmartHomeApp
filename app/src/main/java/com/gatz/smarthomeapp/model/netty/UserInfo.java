package com.gatz.smarthomeapp.model.netty;

/**
 * Created by Debby on 2017/2/7.
 */
public class UserInfo {
    private String userName;
    private String sessionId;
    private String roomid;
    private String unitId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitId() {
        return this.unitId;
    }
}

