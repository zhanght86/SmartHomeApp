package com.gatz.smarthomeapp.model.netty.utils;

import android.util.Log;

import com.gatz.smarthomeapp.model.netty.global.AppGlobal;


/**
 * Created by Debby on 2017/2/7.
 */
public class Utils {
    private static AppGlobal appGlobal = AppGlobal.getInstance();

    /**
     * 打印错误日志
     *
     * @param tag
     * @param msg
     */
    public static void showErrorLog(String tag, String msg) {
        if (appGlobal.isShowLog()) {
            Log.e(tag, msg);
        }
    }

    /**
     * 打印信息日志
     *
     * @param tag
     * @param msg
     */
    public static void showInfoLog(String tag, String msg) {
        if (appGlobal.isShowLog()) {
            Log.i(tag, msg);
        }
    }

    /**
     * 打印debug日志
     *
     * @param tag
     * @param msg
     */
    public static void showDebugLog(String tag, String msg) {
        if (appGlobal.isShowLog()) {
            Log.d(tag, msg);
        }
    }
}
