package com.gatz.smarthomeapp.utils;

import com.gatz.smarthomeapp.bean.HouseInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class LoginInfo implements Serializable {
    // sessionid
    private String sessionid;
    // 聊天好友(物业 )
    private String property;
    // 所属群组
    private String groupid;
    // 登录环信密码
    private String xorpwd;
    // 用户名
    private String username;
    // 小区ID
    private String communityID;
    // 房屋ID
    private String roomID;

    private String apikey;

    private String viDid;
    /***
     * 该用户所有的房子
     */
    private ArrayList<HouseInfo> room = new ArrayList<HouseInfo>();

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getXorpwd() {
        return xorpwd;
    }

    public void setXorpwd(String xorpwd) {
        this.xorpwd = xorpwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getViDid() {
        return viDid;
    }

    public void setViDid(String viDid) {
        this.viDid = viDid;
    }

    public ArrayList<HouseInfo> getRoom() {
        return room;
    }

    public void setRoom(ArrayList<HouseInfo> room) {
        this.room = room;
    }
}
