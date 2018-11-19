package com.gatz.smarthomeapp.bean;

import java.util.List;

/**
 * Created by hx on 2016/7/6.
 */
public class Profile {
    private String profileid;
    private String profilename;
    private String deviceid;
    private String statusitem;
    private String itemvalue;
    private String roomid;
    private String bedroomid;
    private String itemvalueview;
    private String bedroomname;
    private String imgurl;
    private String communityid;
    private String apartmentname;
    private String communityname;
    private String apartmentid;
    List<KnxProtocol> protocols;

    public String getProfileid() {
        return profileid;
    }

    public void setProfileid(String profileid) {
        this.profileid = profileid;
    }

    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }


    public String getStatusitem() {
        return statusitem;
    }

    public void setStatusitem(String statusitem) {
        this.statusitem = statusitem;
    }

    public String getItemvalue() {
        return itemvalue;
    }

    public void setItemvalue(String itemvalue) {
        this.itemvalue = itemvalue;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getBedroomid() {
        return bedroomid;
    }

    public void setBedroomid(String bedroomid) {
        this.bedroomid = bedroomid;
    }

    public String getItemvalueview() {
        return itemvalueview;
    }

    public void setItemvalueview(String itemvalueview) {
        this.itemvalueview = itemvalueview;
    }

    public String getBedroomname() {
        return bedroomname;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public void setCommunityname(String communityname) {
        this.communityname = communityname;
    }

    public void setBedroomname(String bedroomname) {
        this.bedroomname = bedroomname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getApartmentname() {
        return apartmentname;
    }

    public void setApartmentname(String apartmentname) {
        this.apartmentname = apartmentname;
    }

    public String getCommunityid() {
        return communityid;
    }

    public void setCommunityid(String communityid) {
        this.communityid = communityid;
    }

    public String getCommunityname() {
        return communityname;
    }

    public void setCommnityname(String commnityname) {
        this.communityname = commnityname;
    }

    public String getApartmentid() {
        return apartmentid;
    }

    public void setApartmentid(String apartmentid) {
        this.apartmentid = apartmentid;
    }

    public List<KnxProtocol> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<KnxProtocol> protocols) {
        this.protocols = protocols;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "profileid='" + profileid + '\'' +
                ", profilename='" + profilename + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", statusitem='" + statusitem + '\'' +
                ", itemvalue='" + itemvalue + '\'' +
                ", roomid='" + roomid + '\'' +
                ", bedroomid='" + bedroomid + '\'' +
                ", itemvalueview='" + itemvalueview + '\'' +
                ", bedroomname='" + bedroomname + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", communityid='" + communityid + '\'' +
                ", apartmentname='" + apartmentname + '\'' +
                ", commnityname='" + communityname + '\'' +
                ", apartmentid='" + apartmentid + '\'' +
                ", protocols=" + protocols +
                '}';
    }
}
