/*
 * Copyright (C) 2017 Gatz.
 * All rights, including trade secret rights, reserved.
 */
package com.gatz.smarthomeapp.base;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.dnake.v700.smart;
import com.gatz.smarthomeapp.model.crash.CrashHandler;
import com.gatz.smarthomeapp.service.SmartHomeService;
import com.gatz.smarthomeapp.utils.Utils;
import com.gatz.smarthomeapp.utils.VoiceUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhouh on 2017/2/20.
 */
public class MyAppliCation extends MultiDexApplication {
    private static final String TAG = "MyAppliCation-";
    private static MyAppliCation instance = null;
    private List<Activity> mList = new LinkedList<>();

    public static MyAppliCation getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initSpeech();
        startCoreService();
        instance = this;
        Utils.showLogE(TAG, "MyAppliCation onCreate");

        //Crash handler
        CrashHandler crashHandler = new CrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    private void initSpeech() {
        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + VoiceUtils.APPID);
    }


    private void startCoreService() {
        Intent it = new Intent();
        it.setClass(getApplicationContext(), SmartHomeService.class);
        startService(it);
//        Intent voiceService = new Intent(getApplicationContext(), VoiceService.class);
//        startService(voiceService);
        Intent smartService = new Intent(getApplicationContext(),smart.class);
        startService(smartService);
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit(boolean isCrash) {

        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isCrash) {
                System.exit(0);
            }
            Utils.isExit = true;
        }
    }

    public void finishProcess() {
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
