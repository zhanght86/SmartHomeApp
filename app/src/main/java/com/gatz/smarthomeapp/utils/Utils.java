/*
 * Copyright (C) 2017 Gatz.
 * All rights, including trade secret rights, reserved.
 */
package com.gatz.smarthomeapp.utils;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.model.http.MySSLSocketFactory;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouh on 2017/3/03.
 */
public class Utils {
    private static final String TAG = "Utils";
    private static final boolean D = true;
    private static final String TAG_DELIMETER = "---";
    public static boolean isExit = false;
    public static final String LAMP_TYPE = "lamp";
    public static final String WINDOW_TYPE = "window";
    public static final String ACTION_AIR_ERR = "ACTION_AIR_ERR";
    public static final String ACTION_AIR_TIME = "ACTION_AIR_TIME";

    public static final String LSR_HOME = "lsr_home";
    public static final String LSR_AIR = "lsr_air";
    public static final String LSR_ENVI = "lsr_envi";
    public static final String LSR_SECURITY = "lsr_security";
    public static final String LSR_MSG = "lsr_msg";
    public static final String LSR_TALK = "lsr_talk";
    public static final String LSR_CTRL = "lsr_ctrl";

    public static final String GET_VERSION_LSR = "GET_VERSION_LSR";
    public static final String GET_CONNECT_IP = "GET_CONNECT_IP";
    //app name
    public static final String APP_NAME_HOME = "SmartHomeApp";
    public static final String APP_NAME_AIR = "SmartAirCtrlApp";
    public static final String APP_NAME_CTRL = "SmartCtrlApp";
    public static final String APP_NAME_ENVI = "SmartEnviApp";
    public static final String APP_NAME_MSG = "SmartMessage";
    public static final String APP_NAME_TALK = "talk";
    public static final String APP_NAME_SECURITY = "security";

    public static String roomIdNumber = "";

    public static void showLogE(String TAG, String msg) {
        if (D) {
            android.util.Log.e(TAG, TAG + TAG_DELIMETER + msg);
        }
    }

    public static String getMacAddr() {
        String mac = "000000";
        WifiManager wifiManager = (WifiManager) MyAppliCation.getInstance().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info == null) {
            return mac;
        }
        if (info.getMacAddress() == null) {
            return mac;
        }
        mac = info.getMacAddress().replace(":", "");
        return mac;
    }

    /**
     * 判断给定字符串是否空白串 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     */
    public static boolean isEmpty(CharSequence input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

//    /**
//     * 判断是否是手机号
//     *
//     * @param phoneNumber
//     * @return
//     */
//    public static boolean isPhoneNumber(String phoneNumber) {
//        boolean isValid = false;
//        String expression = "^1[3|4|5|7|8][0-9]{9}$";
//        CharSequence inputStr = phoneNumber;
//        Pattern pattern = Pattern.compile(expression);
//        Matcher matcher = pattern.matcher(inputStr);
//        if (matcher.matches()) {
//            isValid = true;
//        }
//        return isValid;
//    }

    public static boolean isUserIdLawful(String phoneNumber) {
        if(phoneNumber.length() != 16) {
            return false;
        }
        if (!phoneNumber.substring(0, 3).equals("ROM")) {
            return false;
        }
        String number = phoneNumber.substring(3);
        if (isNumeric(number)) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    /**
     * 获取当前版本号
     *
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 版本是否需要升级
     *
     * @param localVerison
     * @param severVersion
     * @return
     */
    public static boolean checkVersion(String localVerison, String severVersion) {
        String[] localVersions = localVerison.split("\\.");
        String[] severVersions = severVersion.split("\\.");
        if (Integer.parseInt(severVersions[0].trim()) > Integer.parseInt(localVersions[0].trim())) {
            return true;
        }
        if (Integer.parseInt(severVersions[0].trim()) == Integer.parseInt(localVersions[0].trim())) {
            if (Integer.parseInt(severVersions[1].trim()) > Integer.parseInt(localVersions[1].trim())) {
                return true;
            } else if (Integer.parseInt(severVersions[1].trim()) == Integer.parseInt(localVersions[1].trim())) {
                if (Integer.parseInt(severVersions[2].trim()) > Integer.parseInt(localVersions[2].trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 版本是否需要升级
     *
     * @param version
     * @return
     */
    public static boolean isUpGrade(String version) {
        String versionName = Utils.getVersion(MyAppliCation.getInstance());
        if (Utils.checkVersion(versionName, version)) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前是否有网络
     *
     * @param context
     * @return
     * @author
     */
    public static boolean isNetworkConnected(Context context) {
        boolean f = false;
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] infos = mConnectivityManager.getAllNetworkInfo();
            if (infos != null) {
                for (int i = 0; i < infos.length; i++) {
                    if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
                        f = true;
                    }
                }
            }
//            NetworkInfo mNetworkInfo = mConnectivityManager
//                    .getActiveNetworkInfo();
//            if (mNetworkInfo != null) {
//                f = mNetworkInfo.isAvailable();
//            }
        }
        return f;
    }

    /**
     * wake up the screen
     *
     * @param context
     */
    public static void lightUpScreen(Context context) {
        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!mPowerManager.isScreenOn()) {
            KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock
                    (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");
            mWakeLock.acquire();
            // 初始化键盘锁
            KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("");
            // 键盘解锁
            mKeyguardLock.disableKeyguard();
        }
    }

    /***
     * 删除文件
     * @param file
     */
    public static void deleteFile(File file) {
        boolean deleteSuccess = false;
        String fileName = file.getName();
        long fileNameLong = 0;
        if (!TextUtils.isEmpty(fileName)) {
            String fileNameLongStr = fileName.substring(0, fileName.lastIndexOf("."));
            fileNameLong = Long.valueOf(fileNameLongStr);
        }
        try {
            if (file.exists()) { // 判断文件是否存在
                if (file.isFile()) { // 判断是否是文件
                    file.delete();
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                    File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        if (files[i].isFile()) {
                            String tempFileName = files[i].getName();
                            if (!TextUtils.isEmpty(tempFileName)) {
                                String tempFileNameStr = tempFileName.substring(0, fileName.lastIndexOf("."));
                                Long temp = Long.valueOf(tempFileNameStr);
                                if (temp <= fileNameLong) {
                                    deleteSuccess = files[i].delete();
                                    Utils.showLogE(TAG, tempFileName + "删除文件:" + deleteSuccess);
                                }
                            }
                        }else{
                            deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    /**
     * 安装APK文件
     */
    public static void installApk(Context context, String file) {
        File apkfile = new File(file);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 重新打开一实例
        context.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void updataApp(String file, String name) {
        dmsg req = new dmsg();
        dxml p = new dxml();
        p.setText("/params/url", file);
        p.setText("/params/name", name);
        p.setInt("/params/mode", 1);
        req.to("/upgrade/system/apk", p.toString());
        Log.e("updata", "url::::" + file + ":::name:::" + name + ":::::" + p.toString());
    }

    public static String getUrl(String url) {
        String ip_devdiv = "";
        try {
            InetAddress x = java.net.InetAddress.getByName(url);
            ip_devdiv = x.getHostAddress();//得到字符串形式的ip地址
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip_devdiv;
    }

    public static void hideBottomUIMenu(Activity ac) {
        View decorView = ac.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
