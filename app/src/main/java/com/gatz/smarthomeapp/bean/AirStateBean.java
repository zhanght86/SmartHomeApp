package com.gatz.smarthomeapp.bean;

/**
 * Created by zhouh on 2017/3/24.
 */
public class AirStateBean {
    private String addr;
    private String valueType;
    private int value;

    public void setProtocolAddr(String addr){
        this.addr = addr;
    }

    public void setValueType(String valueType){
        this.valueType = valueType;
    }

    public void setValue(int value){
        this.value = value;
    }

    public String getProtocolAddr(){
        return this.addr;
    }

    public String getValueType(){
        return this.valueType;
    }

    public int getValue(){
        return this.value;
    }
}
