package com.gatz.smarthomeapp.model.netty.utils;

import android.content.res.AssetManager;
import android.content.res.Resources;

import com.gatz.smarthomeapp.model.netty.common.AppConstants;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.ssl.SSLMODE;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class AppProperties {

    private static final String NETTY_RETRY_CONNECT_SABER_INTERVAL = "netty.retry.connect.saber.interval";
    private static final String NETTY_SSL_MODEL = "netty.ssl.model";
    private static final String NETTY_LOGIN_REQUEST_TIMEOUT = "netty.login.request.timeout";
    //private static final String NETTY_SABER_IP = "netty.saber.ip";
    private static final String NETTY_SABER_PORT = "netty.saber.port";
    private static final String NETTY_CONNECT_TIMEOUT = "netty.connect.timeout";
    private static final String NETTY_LOG = "netty.connect.showlog";
    private static final String NETTY_SEESION_TIME = "netty.connect.session.timeout";

    @SuppressWarnings("rawtypes")
    public static boolean initProperties(Resources resources) {
        Properties pps = new Properties();
        AppGlobal appGlobal = AppGlobal.getInstance();
        InputStream inputStream = null;
        try {
            inputStream = resources.getAssets().open("conf/" + AppConstants.APP_PROPERTIES_FILENAME);
            pps.load(inputStream);

            Enumeration enum1 = pps.propertyNames();// 得到配置文件的名字
            while (enum1.hasMoreElements()) {
                String strKey = (String) enum1.nextElement();
                String strValue = pps.getProperty(strKey);
                System.out.println(strKey + "=" + strValue);
                if (NETTY_RETRY_CONNECT_SABER_INTERVAL.equals(strKey)) {
                    appGlobal.setNettyRetryConnectInterval(Integer.valueOf(strValue));
                } else if (NETTY_SSL_MODEL.equals(strKey)) {
                    appGlobal.setNettySslModel(strValue);
                } else if (NETTY_LOGIN_REQUEST_TIMEOUT.equals(strKey)) {
                    appGlobal.setNettyLoginRequestTimeout(Integer.valueOf(strValue));
                } else if (NETTY_SABER_PORT.equals(strKey)) {
                    appGlobal.setNettySaberPort(Integer.valueOf(strValue));
                } else if (NETTY_CONNECT_TIMEOUT.equals(strKey)) {
                    appGlobal.setNettyConnectTimeout(Integer.valueOf(strValue));
                } else if (NETTY_LOG.equals(strKey)) {
                    if ("true".equals(strValue)) {
                        appGlobal.setShowLog(true);
                    } else if ("false".equals(strKey)) {
                        appGlobal.setShowLog(false);
                    }
                } else if (NETTY_SEESION_TIME.equals(strKey)) {
                    appGlobal.setSessionTime(Integer.valueOf(strValue));
                }
            }

            StringBuffer sb = new StringBuffer();
            if (null == appGlobal.getNettyConnectTimeout() || appGlobal.getNettyConnectTimeout().intValue() <= 0) {
                sb.append(NETTY_CONNECT_TIMEOUT + "必须是正整数 ");
            }

            if (null == appGlobal.getNettyLoginRequestTimeout() || appGlobal.getNettyLoginRequestTimeout().intValue() <= 0) {
                sb.append(NETTY_LOGIN_REQUEST_TIMEOUT + "必须是正整数 ");
            }

            if (null == appGlobal.getNettyRetryConnectInterval() || appGlobal.getNettyRetryConnectInterval().intValue() <= 0) {
                sb.append(NETTY_RETRY_CONNECT_SABER_INTERVAL + "必须是正整数 ");
            }

//            if (StringUtils.isBlank(appGlobal.getNettySaberIp())) {
//                sb.append(NETTY_SABER_IP + "不能为空 ");
//            }

            if (null == appGlobal.getNettySaberPort() || appGlobal.getNettySaberPort().intValue() <= 0) {
                sb.append(NETTY_SABER_PORT + "必须是正整数 ");
            }

            if (StringUtils.isNotBlank(appGlobal.getNettySslModel()) && !SSLMODE.CA.toString().equals(appGlobal.getNettySslModel())) {
                sb.append(NETTY_SSL_MODEL + "配置错误 ");
            }
            if (StringUtils.isNotBlank(sb.toString())) {
                Utils.showErrorLog("配置文件参数配置错误: {}", sb.toString());
                return false;
            }
        } catch (Exception e) {
            Utils.showErrorLog("", "初始化配置文件异常");
            return false;
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Utils.showErrorLog("关闭文件时发生异常: {}", e.getMessage());
                }
            }
        }
        return true;
    }
}
