package com.gatz.smarthomeapp.model.netty;

import android.content.res.Resources;

import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.gatz.smarthomeapp.model.netty.coder.AppJsonDecoder;
import com.gatz.smarthomeapp.model.netty.coder.AppJsonEncoder;
import com.gatz.smarthomeapp.model.netty.common.AppConstants;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.handler.ClientAnnounceHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientBaseHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientConnectControlHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientCustomDefineMessageHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientErrorHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientGreenCircleControlHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientGreenCircleTimeAnnounceHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientGreenCircleTimeHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientKnxControlHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientPingHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientSecondLoginAnnounceHandler;
import com.gatz.smarthomeapp.model.netty.handler.ClientStatusHandler;
import com.gatz.smarthomeapp.model.netty.ssl.SSLMODE;
import com.gatz.smarthomeapp.model.netty.ssl.SecureChatSslContextFactory;
import com.gatz.smarthomeapp.utils.Utils;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;

public class AppClientChannelInitializer extends
        ChannelInitializer<SocketChannel> {
    private static final String TAG = "AppClientChannelInitializer";
    private AppGlobal appGlobal = AppGlobal.getInstance();

    private Resources resources;
    private static AppClientChannelInitializer instance = null;
    private static final int READ_IDEL_TIME_OUT = 80; // 读超时
    private static final int WRITE_IDEL_TIME_OUT = 1000;// 写超时
    private static final int ALL_IDEL_TIME_OUT = 1000; // 所有超时

    public AppClientChannelInitializer() {
    }

    public static AppClientChannelInitializer getInstance() {
        if (null == instance) {
            instance = new AppClientChannelInitializer();
        }
        return instance;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        Utils.showLogE(TAG, "初始化");
        ch.pipeline().addLast("LOGGING_HANDLER", new LoggingHandler(LogLevel.DEBUG));
        if (SSLMODE.CA.toString().equals(AppConstants.SSL_MODEL)) {
            String tlsMode = appGlobal.getNettySslModel();
            SSLEngine engine = SecureChatSslContextFactory.getClientContext(tlsMode, AppGlobal.getInstance().getResources()).createSSLEngine();
            engine.setUseClientMode(true);
            ch.pipeline().addLast("ssl", new SslHandler(engine));
        }

        //ch.pipeline().addLast(new IdleStateHandler(READ_IDEL_TIME_OUT, WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT, TimeUnit.SECONDS));
        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(4096, JsonMessage.LENGTH_OFFSET, 4, 0, 0));
        ch.pipeline().addLast("appJsonEncoder", new AppJsonEncoder());
        ch.pipeline().addLast("appJsonDecoder", new AppJsonDecoder());
        ch.pipeline().addLast("clientBaseHandler", new ClientBaseHandler());
        ch.pipeline().addLast("clientPingHandler", new ClientPingHandler());
        ch.pipeline().addLast("clientKnxControlHandler", new ClientKnxControlHandler());
        ch.pipeline().addLast("clientConnectControlHandler", new ClientConnectControlHandler());
        ch.pipeline().addLast("clientGreenCircleTimeHandler", new ClientGreenCircleTimeHandler());
        ch.pipeline().addLast("clientAnnounceHandler", new ClientAnnounceHandler());
        ch.pipeline().addLast("ClientGreenCircleControlHandler", new ClientGreenCircleControlHandler());
        ch.pipeline().addLast("clientGreenCircleTimeAnnounceHandler", new ClientGreenCircleTimeAnnounceHandler());
        ch.pipeline().addLast("clientSecondLoginAnnounceHandler", new ClientSecondLoginAnnounceHandler());
        ch.pipeline().addLast("clientCustomDefineMessageHandler", new ClientCustomDefineMessageHandler());
        ch.pipeline().addLast("clientStatusHandler", new ClientStatusHandler());
        ch.pipeline().addLast("clientErrorHandler", new ClientErrorHandler());
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

}
