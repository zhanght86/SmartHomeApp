package com.gatz.smarthomeapp.service;

import android.app.IntentService;
import android.content.Intent;

import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.utils.HttpUtil;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;
import com.hwangjr.rxbus.RxBus;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouh on 2017/6/20.
 */
public class UpDataAppService extends IntentService {

    private int count = 0;
    private String l = "";
    private int c = 0;
    //处理线程池
    private ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpDataAppService() {
        super("UpDataAppService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Utils.showLogE("UpDataAppService", "onHandleIntent");
        count = 0;
        //监控空调的app
        l = Utils.LSR_HOME;
        c = UrlUtils.LSR_HOME_CODE;
        String cid = intent.getStringExtra("cId");
        String sid = intent.getStringExtra("sId");
        scheduledThreadPool.scheduleAtFixedRate(new updataRunnable(cid, sid), 60, 180, TimeUnit.SECONDS);
    }

    private class updataRunnable implements Runnable {
        private String communityId;
        private String sessionId;

        public updataRunnable(String cId, String sId) {
            this.communityId = cId;
            this.sessionId = sId;
        }

        @Override
        public void run() {

            try {
                if (c != 0) {
                    HttpUtil.getVersion(communityId, UrlUtils.VERSION_TYPE,
                            l,
                            sessionId,
                            o, c);
                }
                count++;
                if (count == 1) {
                    l = Utils.LSR_AIR;
                    c = UrlUtils.LSR_AIR_CODE;
                }
                if (count == 2) {  //检测控制
                    l = Utils.LSR_CTRL;
                    c = UrlUtils.LSR_CTRL_CODE;
                } else if (count == 3) { //监测环境app
                    l = Utils.LSR_ENVI;
                    c = UrlUtils.LSR_ENVI_CODE;
                } else if (count == 4) { //监测talk
                    l = Utils.LSR_TALK;
                    c = UrlUtils.LSR_TALK_CODE;
                } else if (count == 5) { //监测门禁
                    l = Utils.LSR_SECURITY;
                    c = UrlUtils.LSR_SECURITY_CODE;
                } else if (count == 6) { //监测MSG
                    l = Utils.LSR_MSG;
                    c = UrlUtils.LSR_MSG_CODE;
                } else if (count == 7) {
                    Utils.showLogE("OkHttpManager", "关闭升级!");
                    scheduledThreadPool.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
                scheduledThreadPool.shutdown();
            }
        }
    }

    private ObserverCallBack o = new ObserverCallBack() {
        @Override
        public void onSuccessHttp(String responseInfo, int resultCode) {
            RxBus.get().post(Utils.GET_VERSION_LSR, resultCode + "@@@" + responseInfo);
        }

        @Override
        public void onFailureHttp(IOException e, int resultCode) {

        }

        @Override
        public void setData(Object obj) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showLogE("UpDataAppService", "onDestroy");
    }
}
