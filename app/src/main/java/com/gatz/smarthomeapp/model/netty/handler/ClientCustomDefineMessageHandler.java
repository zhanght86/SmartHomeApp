package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.CustomDefineMessage;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Description: 接入<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:22:16
 */
public class ClientCustomDefineMessageHandler extends ClientBaseHandler {
    private static final String TAG = "ClientCustomDefineMessageHandler";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg)
            throws Exception {

        if (!(msg instanceof CustomDefineMessage)) {
            ctx.fireChannelRead(msg);
            return;
        }

        CustomDefineMessage message = (CustomDefineMessage) msg;
        Utils.showInfoLog(TAG, this.getClass().getName() + "接收到的响应为={}" + message);
        //message处理 TODO

    }
}
