package com.gatz.smarthomeapp.model.netty.handler;

import android.util.Log;

import com.citic.zktd.saber.server.entity.json.announce.GreenCircleQuartzAnnounceMessage;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhouh on 2017/11/8.
 */
public class ClientGreenCircleTimeAnnounceHandler extends ClientBaseHandler{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {
        if (!(msg instanceof GreenCircleQuartzAnnounceMessage)) {
            ctx.fireChannelRead(msg);
            return;
        }
        GreenCircleQuartzAnnounceMessage greenCircleQuartzAnnounceMessage = (GreenCircleQuartzAnnounceMessage) msg;
        MsgUtils.dispatchEvent(ConnectResultEvent.GREENCIRCLE_TIME_RESPONSE, greenCircleQuartzAnnounceMessage);
    }
}
