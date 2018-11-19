package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.SecondLoginAnnounceMessage;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Description: 接入<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:22:16
 */
public class ClientSecondLoginAnnounceHandler extends ClientBaseHandler {
    private static final String TAG = "ClientSecondLoginAnnounceHandler";
    SendManager sendManager = SendManager.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg)
            throws Exception {

        if (!(msg instanceof SecondLoginAnnounceMessage)) {
            ctx.fireChannelRead(msg);
            return;
        }

        SecondLoginAnnounceMessage secondLoginAnnounceMessage = (SecondLoginAnnounceMessage) msg;
        com.gatz.smarthomeapp.utils.Utils.showLogE(TAG, this.getClass().getName() + "接收到的响应为={}" + secondLoginAnnounceMessage);
        //停止心跳
        if (null != NettyUtils.sendHeartTask) {
            NettyUtils.sendHeartTask.stopTask();
        }
        MsgUtils.dispatchEvent(ConnectResultEvent.USER_KICKED, MsgUtils.PROTOCOL.NULL);
        sendManager.closeChannel();
    }
}
