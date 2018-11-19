package com.gatz.smarthomeapp.utils;

import android.os.Bundle;
import android.util.Log;

import com.gatz.smarthomeapp.bean.AirConditioning;
import com.gatz.smarthomeapp.bean.AirStateBean;
import com.gatz.smarthomeapp.bean.Chamber;
import com.gatz.smarthomeapp.bean.KnxEquiptment;
import com.gatz.smarthomeapp.bean.Lock;
import com.gatz.smarthomeapp.bean.Profile;
import com.gatz.smarthomeapp.bean.Result;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhouh on 2017/2/22.
 */
public class JsonUtil {

    /**
     * 用户解析
     *
     * @param wsResponse
     * @return
     */
    public static Result<LoginInfo> analyzeLoginInfo(String wsResponse) {
        Result<LoginInfo> result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().constructParametricType(Result.class, LoginInfo.class);
            result = mapper.readValue(wsResponse, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查找room底下的bedroom + 情景模式+空调+门锁
     *
     * @param wsResponse
     * @return
     */
    public static List<Object> analyzeFindRoom(String wsResponse) {
        List<Object> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(wsResponse);
            if (obj.has(UrlUtils.STATUS)) {
                list.add(0, obj.optString(UrlUtils.STATUS));
            } else {
                list.add(0, null);
            }
            if (obj.has(UrlUtils.MSG)) {
                list.add(1, obj.optString(UrlUtils.MSG));
            } else {
                list.add(1, null);
            }
            if (obj.has(UrlUtils.CODE)) {
                list.add(2, obj.optString(UrlUtils.CODE));
            } else {
                list.add(2, null);
            }
            if (obj.has(UrlUtils.T)) {
                JSONObject objT = obj.optJSONObject(UrlUtils.T);
                if (null != objT) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        //中央空调
                        if (objT.has(UrlUtils.AIRCONDITIONING)) {
                            JSONObject airobj = objT.optJSONObject(UrlUtils.AIRCONDITIONING);
                            AirConditioning airConditioning = new AirConditioning();
                            if (null != airobj) {
                                airConditioning = objectMapper.readValue(airobj.toString(), AirConditioning.class);
                            }
                            list.add(3, airConditioning);
                        } else {
                            list.add(3, null);
                        }
                        //场景
                        if (objT.has(UrlUtils.PROFILES)) {
                            JSONArray proArray = objT.optJSONArray(UrlUtils.PROFILES);
                            List<Profile> profiles = new ArrayList<>();
                            if (null != proArray && proArray.length() > 0) {
                                JavaType javaTypeProfile = objectMapper.getTypeFactory().constructParametricType(List.class, Profile.class);
                                profiles = objectMapper.readValue(proArray.toString(), javaTypeProfile);
                            }
                            list.add(4, profiles);
                        } else {
                            list.add(4, null);
                        }
                        //门锁设备
                        if (objT.has(UrlUtils.LOCK)) {
                            JSONObject lockObj = objT.optJSONObject(UrlUtils.LOCK);
                            Lock lock = new Lock();
                            if (null != lockObj) {
                                lock = objectMapper.readValue(lockObj.toString(), Lock.class);
                            }
                            list.add(5, lock);
                        } else {
                            list.add(5, null);
                        }
                        //房间
                        if (objT.has(UrlUtils.BEDROOMS)) {
                            JSONArray array = objT.optJSONArray(UrlUtils.BEDROOMS);
                            List<Chamber> chambers = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                Chamber chamber = new Chamber();
                                JSONObject jsonObject = array.optJSONObject(i);
                                chamber.setBedroomid(jsonObject.optString(UrlUtils.BEDROOMID));
                                chamber.setBedroomname(jsonObject.optString(UrlUtils.BEDROOMNAME));
                                chamber.setRoomid(jsonObject.optString(UrlUtils.ROOMID));
                                chamber.setImgurl(jsonObject.optString(UrlUtils.IMGURL));
                                if (jsonObject.has(UrlUtils.PROFILES)) {
                                    JSONArray proArray = jsonObject.optJSONArray(UrlUtils.PROFILES);
                                    if (null != proArray && proArray.length() > 0) {
                                        JavaType javaTypeProfile = objectMapper.getTypeFactory().constructParametricType(List.class, Profile.class);
                                        List<Profile> profiles = objectMapper.readValue(proArray.toString(), javaTypeProfile);
                                        chamber.setProfiles(profiles);
                                    }
                                }
                                chambers.add(chamber);
                            }
                            list.add(6, chambers);
                        } else {
                            list.add(6, null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /***
     * 解析各个房间里的设备数据
     *
     * @param wsResponse
     * @return
     */
    public static List<Object> analyzeEquipments(String wsResponse) {
        List<Object> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(wsResponse);
            if (obj.has(UrlUtils.STATUS)) {
                list.add(0, obj.optString(UrlUtils.STATUS));
            } else {
                list.add(0, null);
            }
            if (obj.has(UrlUtils.MSG)) {
                list.add(1, obj.optString(UrlUtils.MSG));
            } else {
                list.add(1, null);
            }
            if (obj.has(UrlUtils.CODE)) {
                list.add(2, obj.optString(UrlUtils.CODE));
            } else {
                list.add(2, null);
            }
            if (obj.has(UrlUtils.T)) {
                JSONObject objT = obj.optJSONObject(UrlUtils.T);
                if (null != objT) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    if (objT.has(UrlUtils.EQUIPMENTS)) {
                        JSONArray equipArray = objT.optJSONArray(UrlUtils.EQUIPMENTS);
                        if (null != equipArray && equipArray.length() > 0) {
                            JavaType javaTypeEequip = objectMapper.getTypeFactory().
                                    constructParametricType(List.class, KnxEquiptment.class);
                            List<KnxEquiptment> equiptments = objectMapper.
                                    readValue(equipArray.toString(), javaTypeEequip);
                            list.add(3, equiptments);
                            Log.e("AnalyzeResponse", "equiptments:" + equiptments.size());
                        }
                    } else {
                        list.add(3, null);
                    }
                    //场景
                    if (objT.has(UrlUtils.PROFILES)) {
                        JSONArray proArray = objT.optJSONArray(UrlUtils.PROFILES);
                        if (null != proArray && proArray.length() > 0) {
                            JavaType javaTypeProfile = objectMapper.getTypeFactory().constructParametricType(List.class, Profile.class);
                            List<Profile> profiles = objectMapper.readValue(proArray.toString(), javaTypeProfile);
                            list.add(4, profiles);
                            Log.e("AnalyzeResponse", "profiles :" + profiles.size());
                        } else {
                            list.add(4, null);
                        }
                    } else {
                        list.add(4, null);
                    }
                    if (objT.has(UrlUtils.BEDROOMID) && list.size() > 4) {
                        String bedroomId = objT.optString(UrlUtils.BEDROOMID);
                        list.add(5, bedroomId);
                    } else {
                        list.add(5, null);
                    }
                    if (objT.has(UrlUtils.IMGURL) && list.size() > 5) {
                        String imgUrl = objT.optString(UrlUtils.IMGURL);
                        list.add(6, imgUrl);
                    } else {
                        list.add(6, null);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取灯 窗帘的状态
     *
     * @param json
     * @return
     */
    public static String getDeviceStatus(String json) {
        String power = "";
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("power")) {
                power = object.getString("power");
            } else if (object.has("startStop")) {
                power = object.getString("startStop");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return power;
    }

    /**
     * 解析空调状态
     */
    public static List<AirStateBean> getAirStates(String json) {
        List<AirStateBean> airStateBeens = null;
        if (json != null) {
            airStateBeens = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(json);
                Iterator iterator = object.keys();
                while (iterator.hasNext()) {
                    AirStateBean bean = new AirStateBean();
                    String addr = iterator.next() + "";
                    bean.setProtocolAddr(addr);
                    JSONObject object2 = object.getJSONObject(addr);
                    bean.setValueType(object2.getString("valueType"));
                    bean.setValue(object2.getInt("value"));
                    airStateBeens.add(bean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return airStateBeens;
    }

    /**
     * 获取版本信息
     *
     * @param wsResponse
     * @return
     */
    public static Bundle parseGetVersion(String wsResponse) {
        Bundle bundle = new Bundle();
        try {
            JSONObject obj = new JSONObject(wsResponse);
            if (obj.has(UrlUtils.STATUS)) {
                bundle.putString(UrlUtils.STATUS, obj.optString(UrlUtils.STATUS));
                if ("200".equals(obj.optString(UrlUtils.STATUS))) {
                    if (obj.has(UrlUtils.MSG)) {
                        JSONObject msgObj = obj.optJSONObject(UrlUtils.MSG);
                        if (msgObj.has(UrlUtils.URL))
                            bundle.putString(UrlUtils.URL, msgObj.optString(UrlUtils.URL));
                        if (msgObj.has(UrlUtils.VERSION)) {
                            bundle.putString(UrlUtils.VERSION, msgObj.optString(UrlUtils.VERSION));
                        }
                    }
                } else if ("failed".equals(obj.optString(UrlUtils.STATUS))) {
                    if (obj.has(UrlUtils.CODE)) {
                        bundle.putString(UrlUtils.CODE, obj.optString(UrlUtils.CODE));
                    }
                    if (obj.has(UrlUtils.MESSAGE)) {
                        bundle.putString(UrlUtils.MESSAGE, obj.optString(UrlUtils.MESSAGE));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    /**
     * getIp
     *
     * @param str
     * @return
     */
    public static String getConnectIp(String str) {
        String ip = "";
        try {
            JSONObject obj = new JSONObject(str);
            if (obj.optString(UrlUtils.CODE).equals("200")) {
                ip = obj.optString("t");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
