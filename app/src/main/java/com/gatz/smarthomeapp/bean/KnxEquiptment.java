package com.gatz.smarthomeapp.bean;

import java.util.List;

/**
 * 灯窗帘等设备
 * Created by hx on 2016/7/15.
 */
public class KnxEquiptment extends BaseEquipment{
    private List<KnxProtocol> protocols;//协议
    public List<KnxProtocol> getProtocols() {
        return protocols;
    }
    public void setProtocols(List<KnxProtocol> protocols) {
        this.protocols = protocols;
    }
}
