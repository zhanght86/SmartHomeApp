package com.gatz.smarthomeapp.model.netty.manager;

import com.citic.zktd.saber.server.entity.json.ConnectRequest;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.gatz.smarthomeapp.model.netty.common.AppConstants;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.session.AppSession;
import com.gatz.smarthomeapp.model.netty.session.AppStandardSessionManager;
import com.gatz.smarthomeapp.model.netty.session.RequestSessionListener;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import io.netty.channel.Channel;

public class SendManager {
    private static SendManager instance = null;
    private Channel channel;
    private static final String TAG = "SendManager";

    private SendManager() {
    }

    public static synchronized SendManager getInstance() {
        if (instance == null) {
            instance = new SendManager();
        }
        return SendManager.instance;
    }

    public synchronized void login(ConnectRequest appLoginRequest) {
        this.channelWriteAndFlush(appLoginRequest);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void sendMessage(JsonMessage message) {
        this.channelWriteAndFlush(message);
    }

    private void channelWriteAndFlush(JsonMessage message) {
        if (null != channel && channel.isActive()) {
            channel.writeAndFlush(message);
        } else {
            Utils.showErrorLog(TAG, "当前连接已断,消息发送失败!{}" + message);
            closeChannel();
        }
    }

    public void closeChannel() {
        if (null != channel)
            channel.close();
    }

}
