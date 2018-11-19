package com.gatz.smarthomeapp.model.netty;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import com.citic.zktd.saber.server.entity.json.ConnectRequest;
import com.citic.zktd.saber.server.entity.json.enums.DeviceType;
import com.gatz.smarthomeapp.model.netty.common.AppConstants;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.session.AppSession;
import com.gatz.smarthomeapp.model.netty.session.AppStandardSessionManager;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.utils.Utils;
import com.gatz.smarthomeapp.utils.DbUttil;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class AppClient implements Runnable {
    private static final String TAG = "AppClient";

    private String host;
    private int port;

    private EventLoopGroup eventLoop;

    private Resources resources;

    private Context context;

    public void setContext(Context ctx) {
        this.context = ctx;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    private SendManager sendManager = SendManager.getInstance();

    private static AppClient instance = null;
    private AppGlobal appGlobal = AppGlobal.getInstance();

    private AppClient() {
    }

    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    public void run() {
        eventLoop = new NioEventLoopGroup(BIZGROUPSIZE);
        this.connect(new Bootstrap());
    }

    public static synchronized AppClient getInstance() {
        if (null == instance) {
            instance = new AppClient();
        }
        return AppClient.instance;
    }

    public void connect(final Bootstrap bootstrap) {

        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoop);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new AppClientChannelInitializer());
        host = DbUttil.getConnectIp(context);
        //host = "172.16.7.222";//
        if (TextUtils.isEmpty(appGlobal.getTempNettySaberIp())) {
            bootstrap.remoteAddress(host, port);
        } else {
            bootstrap.remoteAddress(appGlobal.getTempNettySaberIp(), port);
            appGlobal.setTempNettySaberIp("");
        }
        ChannelFuture future = bootstrap.connect();
        Utils.showLogE(TAG, "连接的host:::::" + host + "=====" + port);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture f) throws Exception {
                if (f.isSuccess()) {
                    Utils.showLogE(TAG, "连接成功");
                    Channel channel = f.channel();
                    sendManager.setChannel(channel);
                    MsgUtils.dispatchEvent(ConnectResultEvent.CONNECT_SUCCESS, MsgUtils.PROTOCOL.NULL);
                    NettyUtils.socketTag = true;
                } else {
                    int seconds = new Random().nextInt(10);
                    //TODO 连接失败了  使用新解析的域名来连接一次
                    MsgUtils.dispatchEvent(ConnectResultEvent.CONNECT_FAILURE, MsgUtils.PROTOCOL.NULL);
                    final EventLoop eventLoop = f.channel().eventLoop();
                    eventLoop.schedule(new Runnable() {
                        @Override
                        public void run() {
                            connect(new Bootstrap());
                        }
                    }, seconds, TimeUnit.SECONDS);
                    NettyUtils.socketTag = false;
                    Utils.showLogE(TAG, "连接失败,等待重连!!!!!!!!!!!!!");
                }
            }
        });
    }

//    public String getHost() {
//        return host;
//    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }

    public void setPort(int port) {
        this.port = port;
    }

    public void doConnect() {
        ConnectRequest connectRequest = new ConnectRequest();
        if (!TextUtils.isEmpty(appGlobal.getUserInfo().getSessionId())) {
            connectRequest.setSessionId(appGlobal.getUserInfo().getSessionId());
        }
        if (!TextUtils.isEmpty(appGlobal.getUserInfo().getUserName()))
            connectRequest.setUserName(appGlobal.getUserInfo().getUserName());
        if (!TextUtils.isEmpty(appGlobal.getUserInfo().getRoomid())) {
            connectRequest.setRoomId(appGlobal.getUserInfo().getRoomid());
            connectRequest.setSwitchRoom(true);
        } else {
            connectRequest.setSwitchRoom(false);
        }
        if (AppConstants.SYSTEM_V901.equals(Build.MODEL) || AppConstants.SYSTEM_V902.equals(Build.MODEL)) {
            connectRequest.setDeviceType(DeviceType.ZEROIOT_PAD);
        } else {
            connectRequest.setDeviceType(DeviceType.ANDROID_PAD);
        }
        if (!TextUtils.isEmpty(appGlobal.getUserInfo().getUnitId())){
            connectRequest.setDeviceUniqueId(appGlobal.getUserInfo().getUnitId());
        }
        connectRequest.setSeq(AppConstants.SEQ.incrementAndGet());
        String versionName = "0.0.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(
                    "com.gatz.smarthomeapp", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        connectRequest.setVersion(versionName);
        com.gatz.smarthomeapp.utils.Utils.showLogE(TAG, "doConnect::::"
                + appGlobal.getUserInfo().getUserName() + "--sessionId--"
                + appGlobal.getUserInfo().getSessionId() + "--roomId--"
                + appGlobal.getUserInfo().getRoomid() + "--unitId--"
                + appGlobal.getUserInfo().getUnitId() + "======" + versionName);
        sendManager.sendMessage(connectRequest);
    }
}