/*
 * Copyright (C) 2017 Gatz.
 * All rights, including trade secret rights, reserved.
 */
package com.gatz.smarthomeapp.model.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.activity.home.HomeActivity;
import com.gatz.smarthomeapp.activity.login.view.LoginActivity;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.utils.ToastUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhouh on 2017/2/21.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    // 系统默认UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private MyAppliCation application;
    public static final String CrashFilesName = Environment.getExternalStorageDirectory()
            + "/" + "SmartHomeCrash";
    public static final String CrashFileTxtName = CrashFilesName + "/" + "crash.txt";
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public CrashHandler(MyAppliCation application) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        } else {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e1) {

            }
            Intent intent = new Intent(application.getApplicationContext(), LoginActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    application.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            //退出程序重启
            AlarmManager mgr = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1500,
                    restartIntent); // 1秒钟后重启应用
            MyAppliCation.getInstance().exit(true);
            this.application.finishProcess();
        }
    }

    /**
     * 错误处理,收集错误信息
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtil.makeShortText(application, application.getString(R.string.crash_text));
                Looper.loop();
            }
        }.start();
        saveCrashInfo2File(ex);
        return true;
    }

    //捕捉崩溃信息

    /**
     * 保存错误信息到文件中
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer stringBuffer = null;
        stringBuffer = new StringBuffer();
        long timestamp = System.currentTimeMillis();
        String time = formatter.format(new Date());
        String crashTime = "crash-time:::::" + time;
        stringBuffer.append(crashTime + "\n");

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        stringBuffer.append(result);
        CreatFile(stringBuffer.toString());
        return null;
    }

    private void CreatFile(String result) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File files = new File(CrashFilesName);
            if (!files.exists()) {
                if (!files.mkdirs()) {
                    return;
                }
            }
            File logFile = new File(CrashFileTxtName);
            if (logFile.exists()) {
                logFile.delete();
            }
            try {
                if (logFile.createNewFile()) {
                    writeLogToFile(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeLogToFile(String result) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(CrashFileTxtName);
            if (fw != null) {
                fw.write(result.toString());
                fw.flush();
                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                fw.close();
            } catch (Exception e1) {

            }
        }
    }
}
