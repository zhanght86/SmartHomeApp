package com.gatz.smarthomeapp.model.netty.coder;

import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * Description: 编码器<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月26日 上午10:56:08
 */
public class AppJsonEncoder extends MessageToByteEncoder<JsonMessage> {
    public static final String TAG = "AppJsonEncoder";

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonMessage msg, ByteBuf out) throws Exception {
        msg.encodeAsByteBuf(out);
        Utils.showInfoLog(TAG, msg.toString());
    }
}
