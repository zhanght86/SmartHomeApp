package com.gatz.smarthomeapp.bean;

import java.util.List;

/**
 * 房间
 * Created by hx on 2016/7/8.
 */
public class Chamber {
    private String bedroomid;
    private String roomid;
    private String bedroomname;
    private String imgurl;
    private List<Profile> profiles;

    public String getBedroomid() {
        return bedroomid;
    }

    public void setBedroomid(String bedroomid) {
        this.bedroomid = bedroomid;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getBedroomname() {
        return bedroomname;
    }

    public void setBedroomname(String bedroomname) {
        this.bedroomname = bedroomname;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    @Override
    public String toString() {
        return "Chamber{" +
                "bedroomid='" + bedroomid + '\'' +
                ", roomid='" + roomid + '\'' +
                ", bedroomname='" + bedroomname + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", profiles=" + profiles +
                '}';
    }
}
