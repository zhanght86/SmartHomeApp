package com.gatz.smarthomeapp.bean;


public class KnxProtocol extends WebIdEntity {

    private String deviceid;
    private String protocolid;
    private String functionname;
    private String protocolAddr;
    private String dpId;
    private String span;
    private String cmdvaule;
    private String protocoltype;
    private String bedroomid;
    private String bedroomName;

    public String getProtocolid() {
        return protocolid;
    }

    public void setProtocolid(String protocolid) {
        this.protocolid = protocolid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getFunctionname() {
        return functionname;
    }

    public void setFunctionname(String functionname) {
        this.functionname = functionname;
    }

    public String getProtocolAddr() {
        return protocolAddr;
    }

    public void setProtocolAddr(String protocolAddr) {
        this.protocolAddr = protocolAddr;
    }

    public String getDpId() {
        return dpId;
    }

    public void setDpId(String dpId) {
        this.dpId = dpId;
    }

    public String getSpan() {
        return span;
    }

    public void setSpan(String span) {
        this.span = span;
    }

    public String getCmdvaule() {
        return cmdvaule;
    }

    public void setCmdvaule(String cmdvaule) {
        this.cmdvaule = cmdvaule;
    }

    public String getProtocoltype() {
        return protocoltype;
    }

    public void setProtocoltype(String protocoltype) {
        this.protocoltype = protocoltype;
    }

    public String getBedroomid() {
        return bedroomid;
    }

    public void setBedroomid(String bedroomid) {
        this.bedroomid = bedroomid;
    }

    public String getBedroomName() {
        return bedroomName;
    }

    public void setBedroomName(String bedroomName) {
        this.bedroomName = bedroomName;
    }

    @Override
    public String toString() {
        return "KnxProtocol{" +
                "deviceid='" + deviceid + '\'' +
                ", protocolid='" + protocolid + '\'' +
                ", functionname='" + functionname + '\'' +
                ", protocolAddr='" + protocolAddr + '\'' +
                ", dpId='" + dpId + '\'' +
                ", span='" + span + '\'' +
                ", cmdvaule='" + cmdvaule + '\'' +
                ", protocoltype='" + protocoltype + '\'' +
                ", bedroomid='" + bedroomid + '\'' +
                ", bedroomName='" + bedroomName + '\'' +
                '}';
    }
}
