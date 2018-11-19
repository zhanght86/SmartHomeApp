package com.gatz.smarthomeapp.model.netty.task;

import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SendHeartTask {
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private AppGlobal appGlobal = AppGlobal.getInstance();

    private SendManager sendManager = SendManager.getInstance();
    private boolean stop = false;

    public void init() {
        RunTask task = new RunTask();
        try {
            scheduledExecutorService.scheduleAtFixedRate(task, 0, appGlobal.getIntervalTime(), TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RunTask implements Runnable {

        public RunTask() {
        }

        public void run() {
            // 心跳
            if (!stop) {
                NettyUtils.pingRequest();
            }
        }


    }

    public void stopTask() {
        this.stop = true;
        scheduledExecutorService.shutdownNow();
    }
}
