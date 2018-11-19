package com.gatz.smarthomeapp.utils;

import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.model.http.OkHttpManager;

import java.util.HashMap;

/**
 * Created by Debby on 2016/11/9.
 */
public class TulingRequest {
    public static final String url = "http://www.tuling123.com/openapi/api";
    public static final String NEWS_URL = "http://news.sina.cn/";
    public static final String COOKBOOK = "http://m.haodou.com/recipe/search/?keyword=";
    public static final int TULING_COED = 0x90;
    public static final String key = "d6a5cd83d15eec541f880b735f9099fe";
    public static final String CODE = "code";
    public static final String TEXT = "text";
    public static final String URL = "url";
    public static final String LIST = "list";

    /***
     * 请求图灵数据
     * @param requestInfo
     * @param uid
     * @param callBack
     * @param resultCode
     */
    public static void requestTuring(String requestInfo, String uid, ObserverCallBack callBack, int resultCode){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key",key);
        map.put("info", requestInfo);
        map.put("userid", uid);
        map.put("loc","北京市");
        OkHttpManager.getInstance().POST(url,map,callBack, resultCode);
    }
}
