package com.gatz.smarthomeapp.model.netty.handler;

import android.text.TextUtils;

import com.gatz.smarthomeapp.model.netty.AppClient;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.WriteTimeoutException;

public class ClientErrorHandler extends ClientBaseHandler {
    private static final String TAG = "ClientErrorHandler";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //Utils.showErrorLog(TAG, "发生异常：{}" + cause.printStackTrace());
        cause.printStackTrace();
        if (cause instanceof WriteTimeoutException || cause instanceof IOException) {
            // sendManager.closeChannel();
            //连接断开提示信息
            MsgUtils.dispatchEvent(ConnectResultEvent.CONNECT_FAILURE, MsgUtils.PROTOCOL.NULL);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Utils.showErrorLog(TAG, "连接断开");
        final AppClient appClient = AppClient.getInstance();
        AppGlobal appGlobal = AppGlobal.getInstance();

        if (TextUtils.isEmpty(AppGlobal.getInstance().getTempNettySaberIp())) {
            int seconds = new Random().nextInt(10000);
            Thread.sleep(seconds);
        }
        int seconds = new Random().nextInt(10000);
        Thread.sleep(seconds);
        EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                appClient.connect(new Bootstrap());
            }
        }, appGlobal.getNettyRetryConnectInterval(), TimeUnit.SECONDS);
        //连接断开提示信息
        MsgUtils.dispatchEvent(ConnectResultEvent.CONNECT_FAILURE, MsgUtils.PROTOCOL.NULL);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        super.userEventTriggered(ctx, evt);
//        if(evt instanceof IdleStateEvent) {  // 2
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state() == IdleState.READER_IDLE) {
//                SendManager.getInstance().closeChannel();
//            } else if (event.state() == IdleState.WRITER_IDLE) {
//
//            } else if (event.state() == IdleState.ALL_IDLE) {
//
//            }
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }

    }
}
