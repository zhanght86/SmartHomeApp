/*
 * Copyright (C) 2017 Gatz.
 * All rights, including trade secret rights, reserved.
 */
package com.gatz.smarthomeapp.utils;

import android.util.Log;

import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.model.http.OkHttpManager;

import java.util.HashMap;

/**
 * Created by zhouh on 2017/2/22.
 */
public class HttpUtil {

    /**
     * 登录
     *
     * @param macAddress
     * @param phone
     * @param pwd
     * @param terminal
     * @param callBack
     * @param resultCode
     */
    public static void doLogin(String macAddress, String phone, String pwd, String terminal,
                               ObserverCallBack callBack, int resultCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put(UrlUtils.USERNAME, phone);
        map.put(UrlUtils.PASSWORD, pwd);
        map.put(UrlUtils.MACADDRESS, macAddress);
        map.put(UrlUtils.USERTYPEVIEW, UrlUtils.USERTYPE);
        map.put(UrlUtils.TERMINAL, terminal);
        OkHttpManager.getInstance().POST(UrlUtils.LOGIN_URL, map, callBack, resultCode);
    }

    /**
     * 登出
     *
     * @param sessionId
     * @param callBack
     * @param resultCode
     */
    public static void doLogout(String sessionId, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.LOGOUT + "?apikey=" + sessionId;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    /**
     * 选择房屋
     *
     * @param sessionId
     * @param roomId
     * @param callBack
     * @param resultCode
     */
    public static void selectRoom(String sessionId, String roomId, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.SELECTROOM + roomId + "?apikey=" + sessionId;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    /**
     * 获取未来几天的天气情况
     *
     * @param cityName
     * @param key
     * @param callback
     * @param resultCode
     */
    public static void getFutureWeather(String cityName, String key, ObserverCallBack callback, int resultCode) {
        String url = UrlUtils.FUTUREWEATHER + "?cityname=" + cityName + "&key=" + key;
        OkHttpManager.getInstance().GET(url, callback, resultCode);
    }

    /**
     * 获取环境设备 云罐
     *
     * @param sessionId
     * @param roomId
     * @param callBack
     * @param resultCode
     */
    public static void getEnvieqpMsg(String sessionId, String roomId, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.GETENVIEQPMSG + "?apikey=" + sessionId + "&roomId=" + roomId;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    /**
     * 获取房屋列表
     *
     * @param sessionId
     * @param roomID
     * @param callBack
     * @param resultCode
     */
    public static void getRooms(String sessionId, String roomID,
                                ObserverCallBack callBack, int resultCode) {
        OkHttpManager.getInstance().GET(
                UrlUtils.FINDBEDROOM + roomID + "?apikey=" + sessionId, callBack, resultCode);
    }

    /**
     * 获取房间的设备列表
     *
     * @param sessionId
     * @param roomID
     * @param bedroomid
     * @param callBack
     * @param resultCode
     */
    public static void getDevices(String sessionId, String roomID, String bedroomid,
                                  ObserverCallBack callBack, int resultCode) {
        OkHttpManager.getInstance().GET(
                UrlUtils.FINDEPBID + roomID + "/" + bedroomid + "?apikey=" + sessionId, callBack, resultCode);
    }

    /**
     * 获取房屋内所有的设备
     *
     * @param sessionId
     * @param roomId
     * @param callBack
     * @param resultCode
     */
    public static void getRoomAllDevice(String sessionId, String roomId, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.FINDALLDEVICE + roomId + "?apikey=" + sessionId;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    /**
     * 环境设备(设备号did)采集到的信息
     *
     * @param sessionId
     * @param did
     * @param uid
     * @param detail
     * @param callBack
     * @param resultCode
     */
    public static void getEnvironment(String sessionId, String did, String uid, boolean detail,
                                      ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.GETENVIRONMENT + "?apikey=" + sessionId + "&did=" + did + "&uid=" + uid + "&detail=" + detail;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    /**
     * 环境设备(云罐)的历史数据
     *
     * @param uid
     * @param did
     * @param callBack
     * @param resultCode
     */
    public static void getHistoryDeviceDataList(String uid, String did, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.GETHISTORYDEVICEDATALIST + "?uid=" + uid + "&did=" + did;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    /**
     * Upload text to determine whether it is a control device command
     * @param sessionId
     * @param filename
     * @param text
     * @param callBack
     * @param resultCode
     */
    public static void uploadVoiceText(String sessionId, String filename, String text, ObserverCallBack callBack, int resultCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UrlUtils.TEXT, text);
        map.put(UrlUtils.APIKEY, sessionId);
        map.put(UrlUtils.FILENAME, filename);
        OkHttpManager.getInstance().POST(UrlUtils.CONTROL_TEXT, map, callBack,resultCode);
    }

    /**
     * 获取版本信息
     *
     * @param communityid
     * @param versionType
     * @param apikey
     * @param callBack
     * @param resultCode
     */
    public static void getVersion(String communityid, String versionType, String appType,
                                  String apikey, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.GET_VERSION + appType + "/" + communityid + "/" + versionType + "?apikey=" + apikey;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    /**
     * 获取局域网ip
     *
     * @param apikey
     * @param roomId
     * @param callBack
     * @param resultCode
     */
    public static void getConnectIp(String apikey, String roomId, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.GET_CONNECT_IP + roomId + "?apikey=" + apikey;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

    public static void getVersionPad(String communityid, String versionType, String appType, String apikey, ObserverCallBack callBack, int resultCode) {
        String url = UrlUtils.GET_VERSION_PAD + appType + "/" + communityid + "/" + versionType + "?apikey=" + apikey;;
        OkHttpManager.getInstance().GET(url, callBack, resultCode);
    }

}
