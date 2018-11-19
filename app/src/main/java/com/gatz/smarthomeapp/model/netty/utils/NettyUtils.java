package com.gatz.smarthomeapp.model.netty.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.citic.zktd.saber.server.entity.json.GetStatusRequest;
import com.citic.zktd.saber.server.entity.json.PingRequest;
import com.citic.zktd.saber.server.entity.json.enums.ScheduleOptionType;
import com.citic.zktd.saber.server.entity.json.enums.StatusType;
import com.citic.zktd.saber.server.entity.quartz.JobDO;
import com.citic.zktd.saber.server.entity.quartz.JobDetailRequest;
import com.citic.zktd.saber.server.entity.quartz.QuartzType;
import com.citic.zktd.saber.server.entity.quartz.TriggerDO;
import com.gatz.smarthomeapp.model.netty.AppClient;
import com.gatz.smarthomeapp.model.netty.UserInfo;
import com.gatz.smarthomeapp.model.netty.common.AppConstants;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.task.SendHeartTask;
import com.gatz.smarthomeapp.utils.DbUttil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by zhouh on 2017/2/7.
 */
public class NettyUtils {
    public static SendHeartTask sendHeartTask;
    public static boolean socketTag = false;
    public static boolean connectTag = false;
    public static final String TAG = "NettyUtils--";
    public static final String TIME_TYPE_TAG = "TimeCmd_Type";
    public static final String TIME_AIR_OPEN = "air_open";
    public static final String TIME_AIR_CLOSE = "air_close";

    public static void socketConnect(Resources resources, Context context) {
        AppGlobal appGlobal = AppGlobal.getInstance();
        AppClient appClient = AppClient.getInstance();
        appClient.setContext(context);
        appClient.setPort(appGlobal.getNettySaberPort());
        appClient.setResources(resources);
        appGlobal.setResources(resources);
        appClient.run();
    }

    public static void sendHeart(Context context) {
        if (null != NettyUtils.sendHeartTask) {
            NettyUtils.sendHeartTask.stopTask();
        }
        long oT = DbUttil.getPingOt(context);
        long vT = DbUttil.getPingVt(context);
        //连接成功发送心跳
        AppGlobal.getInstance().setIntervalTime(vT);
        AppGlobal.getInstance().setTimeOut(oT);
        Utils.showErrorLog(TAG, "启动心跳任务.......");
        NettyUtils.sendHeartTask = new SendHeartTask();
        NettyUtils.sendHeartTask.init();
    }

    public static void pingRequest() {
        UserInfo bean = AppGlobal.getInstance().getUserInfo();
        if (bean != null) {
            String sessionid = bean.getSessionId();
            PingRequest pingRequest = new PingRequest();
            if (!TextUtils.isEmpty(sessionid)) {
                pingRequest.setSessionId(sessionid);
            }
            if (!TextUtils.isEmpty(bean.getRoomid())) {
                pingRequest.setRoomId(bean.getRoomid());
            }
            long seq = AppConstants.SEQ.incrementAndGet();
            pingRequest.setSeq(seq);
            Utils.showErrorLog(TAG, "@pingRequest------");
            SendManager.getInstance().sendMessage(pingRequest);
        }
    }

    public static void getDeviceStateRequset(StatusType statusType) {
        UserInfo bean = AppGlobal.getInstance().getUserInfo();
        String sessionid = bean.getSessionId();
        GetStatusRequest request = new GetStatusRequest();
        request.setStatusType(statusType);
        if (!TextUtils.isEmpty(sessionid)) {
            request.setSessionId(sessionid);
        }
        if (!TextUtils.isEmpty(bean.getRoomid())) {
            request.setRoomId(bean.getRoomid());
        }
        request.setSeq(AppConstants.SEQ.incrementAndGet());
        Utils.showErrorLog(TAG, "@getDeviceStateRequset------" + statusType.toString());
        SendManager.getInstance().sendMessage(request);
    }

    public static void sendRemoveAirTimeCmd() {
        final UserInfo bean = AppGlobal.getInstance().getUserInfo();
        if (bean != null) {
            JobDetailRequest request1 = new JobDetailRequest();
            String sessionid = bean.getSessionId();
            if (!TextUtils.isEmpty(sessionid)) {
                request1.setSessionId(sessionid);
            }
            if (!TextUtils.isEmpty(bean.getRoomid())) {
                request1.setRoomId(bean.getRoomid());
            }
            JobDO jobDO = new JobDO();
            jobDO.setGroup(bean.getRoomid());
            jobDO.setName("GreenCircleJob_close");
            jobDO.setDescription("空调定时器_关");
            jobDO.setQuartzType(QuartzType.GREEN_CIRCLE);
            request1.setJobDO(jobDO);
            request1.setScheduleOptionType(ScheduleOptionType.REMOVE);
            request1.setSeq(AppConstants.SEQ.incrementAndGet());
            SendManager.getInstance().sendMessage(request1);

            JobDetailRequest request2 = new JobDetailRequest();
            if (!TextUtils.isEmpty(sessionid)) {
                request2.setSessionId(sessionid);
            }
            if (!TextUtils.isEmpty(bean.getRoomid())) {
                request2.setRoomId(bean.getRoomid());
            }
            JobDO jobDO2 = new JobDO();
            jobDO2.setGroup(bean.getRoomid());
            jobDO2.setName("GreenCircleJob_open");
            jobDO2.setDescription("空调定时器_开");
            jobDO2.setQuartzType(QuartzType.GREEN_CIRCLE);
            request2.setJobDO(jobDO2);
            request2.setScheduleOptionType(ScheduleOptionType.REMOVE);
            request2.setSeq(AppConstants.SEQ.incrementAndGet());
            SendManager.getInstance().sendMessage(request2);
        }
    }

    public static void sendAirTimeCmd(String oh, String om, String ch, String cm, String days) {
        Utils.showErrorLog(TAG, "传递的空调定时器时间为:" + oh + "===" + om + "关机时间:" + ch + "==="
                + cm + "=====" +days);
        final UserInfo bean = AppGlobal.getInstance().getUserInfo();
        if (bean != null) {
            //重复天数
            StringBuffer sp =  new StringBuffer();
            if (days.contains("7")) {
                sp.append("1,");
            }
            if (days.contains("1")) {
                sp.append("2,");
            }
            if (days.contains("2")) {
                sp.append("3,");
            }
            if (days.contains("3")) {
                sp.append("4,");
            }
            if (days.contains("4")) {
                sp.append("5,");
            }
            if (days.contains("5")) {
                sp.append("6,");
            }
            if (days.contains("6")) {
                sp.append("7,");
            }
            sp.deleteCharAt(sp.length() - 1);
            String sessionid = bean.getSessionId();
            JobDetailRequest request = new JobDetailRequest();
            if (!TextUtils.isEmpty(sessionid)) {
                request.setSessionId(sessionid);
            }
            if (!TextUtils.isEmpty(bean.getRoomid())) {
                request.setRoomId(bean.getRoomid());
            }
            JobDO jobDO = new JobDO();
            jobDO.setGroup(bean.getRoomid());
            jobDO.setName("GreenCircleJob_open");
            jobDO.setDescription("空调定时器_开");
            jobDO.setQuartzType(QuartzType.GREEN_CIRCLE);
            Map<String, Object> infoMap = new HashMap<>();
            infoMap.put(TIME_TYPE_TAG, TIME_AIR_OPEN);
            jobDO.setExtInfo(infoMap);
            request.setJobDO(jobDO);
            //开机触发时间
            request.setSeq(AppConstants.SEQ.incrementAndGet());
            request.setScheduleOptionType(ScheduleOptionType.ADD);
            TriggerDO triggerDO1 = new TriggerDO();
            triggerDO1.setGroup(bean.getRoomid());
            triggerDO1.setName("GreenCircleTigger_open");
            String des1 = "开机时间:" + oh + om;
            String c1 = "0 " + om + " " + oh + " ? * " + sp.toString();
            //String c1 = "0/30 * * * * ?";
            //每周的day时间的h1小时m1分触发
            request.setRepeat(true);
            //秒 分 小时 日 月 星期 年
            triggerDO1.setDescription(des1);
            triggerDO1.setCronExpression(c1);
            Set<TriggerDO> triggerDOs1 = new HashSet<>();
            triggerDOs1.add(triggerDO1);
            request.setTriggerDOs(triggerDOs1);
            com.gatz.smarthomeapp.utils.Utils.showLogE(TAG, "开机请求::" + request.toString());
            SendManager.getInstance().sendMessage(request);

            JobDetailRequest request1 = new JobDetailRequest();
            if (!TextUtils.isEmpty(sessionid)) {
                request1.setSessionId(sessionid);
            }
            if (!TextUtils.isEmpty(bean.getRoomid())) {
                request1.setRoomId(bean.getRoomid());
            }

            JobDO jobDO1 = new JobDO();
            jobDO1.setGroup(bean.getRoomid());
            jobDO1.setName("GreenCircleJob_close");
            jobDO1.setDescription("空调定时器_关");
            jobDO1.setQuartzType(QuartzType.GREEN_CIRCLE);
            Map<String, Object> infoMap1 = new HashMap<>();
            infoMap1.put(TIME_TYPE_TAG, TIME_AIR_CLOSE);
            jobDO1.setExtInfo(infoMap1);
            request1.setJobDO(jobDO1);

            request1.setSeq(AppConstants.SEQ.incrementAndGet());
            request1.setScheduleOptionType(ScheduleOptionType.ADD);
            String des2 = "关机时间:" + ch + cm;
            String c2 = "0 " + cm + " " + ch + " ? * " + sp.toString();
            //String c2 = "0/59 * * * * ?";
            //每周的day时间的h1小时m1分触发
            request1.setRepeat(true);
            TriggerDO triggerDO2 = new TriggerDO();
            triggerDO2.setGroup(bean.getRoomid());
            triggerDO2.setName("GreenCircleTigger_close");
            //秒 分 小时 日 月 星期 年
            triggerDO2.setDescription(des2);
            triggerDO2.setCronExpression(c2);
            Set<TriggerDO> triggerDOs2 = new HashSet<>();
            triggerDOs2.add(triggerDO2);
            request1.setTriggerDOs(triggerDOs2);
            com.gatz.smarthomeapp.utils.Utils.showLogE(TAG, "关机请求::" + request1.toString());
            SendManager.getInstance().sendMessage(request1);
        }
    }

    private static String getConrTime(String time) {
        String c1 = time.substring(0, 1);
        if (c1.equals("0")) {
            return time.substring(1,2);
        }
        return time;
    }
}
