package com.gatz.smarthomeapp.bean;

import java.io.Serializable;

public class HouseInfo implements Serializable {
    private String roomID; // 房屋ID
    private String communityID; // 所属小区ID
    private String buildingID; // 所属楼号
    private String unitID; // 所属单元id
    private String unitname;//所属单元名
    private String houseID; // 房间号
    private Integer apartmentid; // 户型ID
    private String builname;// 楼号名称
    private String communityname;// 小区名称
    private String apartmentname; // 户型名称

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(String buildingID) {
        this.buildingID = buildingID;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getHouseID() {
        return houseID;
    }

    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    public Integer getApartmentid() {
        return apartmentid;
    }

    public void setApartmentid(Integer apartmentid) {
        this.apartmentid = apartmentid;
    }

    public String getBuilname() {
        return builname;
    }

    public void setBuilname(String builname) {
        this.builname = builname;
    }

    public String getCommunityname() {
        return communityname;
    }

    public void setCommunityname(String communityname) {
        this.communityname = communityname;
    }

    public String getApartmentname() {
        return apartmentname;
    }

    public void setApartmentname(String apartmentname) {
        this.apartmentname = apartmentname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getUnitname() {
        return unitname;
    }

}
