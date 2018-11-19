package com.gatz.smarthomeapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;

import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.utils.AppProperties;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.service.aidl.DeviceCommandApi;
import com.gatz.smarthomeapp.service.aidl.UserManagerApi;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.Utils;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

public class SmartHomeService extends Service {
    private static final String TAG = "SmartHomeService";
    private UserManagerApi userManagerApi;
    private DeviceCommandApi deviceCommandApi;

    public SmartHomeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.showLogE(TAG, "onStartCommand");
        if (DbUttil.getConnectIp(getApplicationContext()).equals("0")) {  //数据库中没有数据
            return START_REDELIVER_INTENT;
        }
        if (!AppProperties.initProperties(getResources())) {
            Utils.showLogE(TAG,"netty初始化配置信息出错");
            MsgUtils.dispatchEvent(ConnectResultEvent.INIT, MsgUtils.PROTOCOL.NULL);
            return START_REDELIVER_INTENT;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.showLogE(TAG, "NettyUtils.connect@");
                NettyUtils.socketConnect(getResources(), getApplicationContext());
            }
        }).start();

        //启动广播 关闭定时器更新
        Intent intent1 = new Intent("UPDATA_ALARM_ACTION");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent1, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        //12个小时监测一次 RTC时钟夜间处理
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),  10 * 60 * 60 * 1000, pi);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        String action = intent.getAction();
        if(action.equals("com.gatz.smarthomeapp.service.aidl.UserManagerApi")){
            return userManagerApi;
        } else if(action.equals("com.gatz.smarthomeapp.service.aidl.DeviceCommandApi")){
            return deviceCommandApi;
        }
        return null;
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(Utils.GET_CONNECT_IP)})
    public void getIpResponseInfo(String r) {
        Utils.showLogE(TAG, "第一次连接ip::::" + r);
        if (!AppProperties.initProperties(getResources())) {
            Utils.showLogE(TAG,"netty初始化配置信息出错");
            MsgUtils.dispatchEvent(ConnectResultEvent.INIT, MsgUtils.PROTOCOL.NULL);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.showLogE(TAG, "NettyUtils.connect@");
                NettyUtils.socketConnect(getResources(), getApplicationContext());
            }
        }).start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RxBus.get().register(this);
        Utils.showLogE(TAG, "onCreate");
        ContentResolver r = this.getContentResolver();
        int val = Settings.System.getInt(r, Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        if (Settings.System.WIFI_SLEEP_POLICY_NEVER != val)
            Settings.System.putInt(r, Settings.System.WIFI_SLEEP_POLICY,
                    Settings.System.WIFI_SLEEP_POLICY_NEVER);
        //AIDL
        userManagerApi = new UserManagerApi();
        deviceCommandApi = new DeviceCommandApi(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}
