package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.GreenCircleResponse;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.response.ReturnCode;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.utils.Utils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Description: 三恒空调响应处理<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:22:16
 */
public class ClientGreenCircleControlHandler extends ClientBaseHandler {
    private static final String TAG = "ClientGreenCircleControlHandler";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {
        if (!(msg instanceof GreenCircleResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        GreenCircleResponse greenCircleControlResponse = (GreenCircleResponse) msg;
        ReturnCode returnCode = greenCircleControlResponse.getReturnCode();
        Utils.showLogE(TAG, "-------" + greenCircleControlResponse.toString());
        switch (returnCode) {
            case SUCCESS:
                MsgUtils.dispatchEvent(ConnectResultEvent.GREENCIRCLE_RESPONSE,
                        greenCircleControlResponse);
                break;
            default:
                break;
        }
    }
}
