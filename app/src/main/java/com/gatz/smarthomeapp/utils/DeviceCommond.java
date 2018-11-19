package com.gatz.smarthomeapp.utils;

import android.content.Context;
import android.util.Log;

import com.citic.zktd.saber.server.entity.json.GreenCircleRequest;
import com.citic.zktd.saber.server.entity.json.GreenValue;
import com.citic.zktd.saber.server.entity.json.KnxRequest;
import com.citic.zktd.saber.server.entity.json.enums.GreenCommandAddress;
import com.citic.zktd.saber.server.entity.json.enums.GreenValueType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxCommandType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxControlType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxPowerType;
import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.model.netty.common.AppConstants;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceCommond {
    private static final String TAG = "DeviceCommond====";
    /***
     * 发送单一开关指令如灯、窗帘的开关
     *
     * @param knxCommondType
     * @param knxControlType
     * @param value
     * @param sendManager
     */
    public static void sendKnxCommond(Context context, KnxProtocol protocol, KnxCommandType knxCommondType,
                                      KnxControlType knxControlType, String value,
                                      SendManager sendManager) {
        if (null != protocol) {
            String functionName = protocol.getFunctionname();
            if (knxCommondType == KnxCommandType.POWER) {
                if ("knx".equals(protocol.getProtocoltype()) && ("开关".equals(functionName))) {
                    KnxRequest knxRequest = new KnxRequest();
                    setHead(knxRequest, context);
                    knxRequest.setKnxCommandType(knxCommondType);
                    knxRequest.setKnxControlType(knxControlType);
                    knxRequest.setKnxAddress(protocol.getProtocolAddr());
                    knxRequest.setValue(value);
                    Log.e(TAG, "knxRequest::操作地址:::" + protocol.getProtocolAddr() + "===操作符===" + value);
                    sendManager.sendMessage(knxRequest);
                }

            } else if (knxCommondType == KnxCommandType.START_STOP) {
                if ("knx".equals(protocol.getProtocoltype()) && ("暂停".equals(functionName))) {
                    KnxRequest knxRequest = new KnxRequest();
                    setHead(knxRequest, context);
                    knxRequest.setKnxCommandType(knxCommondType);
                    knxRequest.setKnxControlType(knxControlType);
                    knxRequest.setKnxAddress(protocol.getProtocolAddr());
                    knxRequest.setValue(value);
                    Log.e(TAG, "knxRequest:::::" + knxRequest.toString());
                    sendManager.sendMessage(knxRequest);
                }
            }
        }
    }

    /**
     * 设置请求基本信息
     *
     * @param knxRequest
     */
    private static void setHead(KnxRequest knxRequest, Context context) {
        if (null != knxRequest) {
            UserInfoBean bean = DbUttil.getUser(context);
            if (bean != null) {
                knxRequest.setSeq(AppConstants.SEQ.incrementAndGet());
                knxRequest.setRoomId(bean.getRoomId());
                knxRequest.setSessionId(bean.getSessionId());
                knxRequest.setUserName(bean.getUserName());
            }
        }
    }

    /***
     * 发送某一情景模式指令
     *
     * @param protocolList
     */
    public static void sendProfileCommond(Context context, List<KnxProtocol> protocolList, SendManager sendManager) {
        for (int i = 0; i < protocolList.size(); i++) {
            KnxRequest knxRequest = new KnxRequest();
            setHead(knxRequest, context);
            KnxProtocol protocol = protocolList.get(i);
            if ("1".equals(protocol.getCmdvaule())) {
                knxRequest.setKnxAddress(protocol.getProtocolAddr());
                knxRequest.setKnxCommandType(KnxCommandType.POWER);
                knxRequest.setKnxControlType(KnxControlType.WRITE);
                knxRequest.setValue(KnxPowerType.ON.getValue());
            } else {
                knxRequest.setKnxAddress(protocol.getProtocolAddr());
                knxRequest.setKnxCommandType(KnxCommandType.POWER);
                knxRequest.setKnxControlType(KnxControlType.WRITE);
                knxRequest.setValue(KnxPowerType.OFF.getValue());
            }
            Log.e(TAG, "knxRequest:::::" + knxRequest.toString());
            sendManager.sendMessage(knxRequest);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param addressMode 对应寄存器地址
     * @param value       值
     * @param sendManager channel
     *                        MODEL(1),
    REST(2),
    TEMPERATURE(3),
    WET(4),
    WIND_SPEED(5),
    ERROR(6),
    INTEGER(7);
     *
     *
     */
    public static void sendGreenCommand(Context context, GreenCommandAddress addressMode,
                                        int value, SendManager sendManager) {
        Map<GreenCommandAddress, GreenValue> commondMap = new HashMap<>();
        GreenValue v = new GreenValue();
        if(addressMode.getValue() == GreenCommandAddress.MODEL.getValue()){
            v.setValueType(GreenValueType.MODEL);
        } else if (addressMode.getValue() == GreenCommandAddress.WET.getValue()){
            v.setValueType(GreenValueType.WET);
        } else if (addressMode.getValue() == GreenCommandAddress.RESET.getValue()){
            v.setValueType(GreenValueType.REST);
        } else if (addressMode.getValue() == GreenCommandAddress.WIND_SPEED.getValue()){
            v.setValueType(GreenValueType.WIND_SPEED);
        } else if ((addressMode.getValue() > 5)&&(addressMode.getValue() < 19)){
            v.setValueType(GreenValueType.TEMPERATURE);
        } else {
            v.setValueType(GreenValueType.INTEGER);
        }
        v.setValue(value);
        commondMap.put(addressMode, v);
        GreenCircleRequest greenCircleRequest = new GreenCircleRequest();
        UserInfoBean bean = DbUttil.getUser(context);
        if (bean != null) {
            greenCircleRequest.setSessionId(bean.getSessionId());
            greenCircleRequest.setRoomId(bean.getRoomId());
            greenCircleRequest.setSeq(AppConstants.SEQ.incrementAndGet());
            greenCircleRequest.setUserName(bean.getUserName());
        }
        greenCircleRequest.setCommandMap(commondMap);
        Utils.showLogE(TAG, "greenRequest:::::" + greenCircleRequest.toString());
        sendManager.sendMessage(greenCircleRequest);
    }

}
