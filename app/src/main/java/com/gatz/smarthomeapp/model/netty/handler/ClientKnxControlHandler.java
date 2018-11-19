package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.KnxResponse;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.response.ReturnCode;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.utils.Utils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Description: 接入<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月25日 下午3:22:16
 */
public class ClientKnxControlHandler extends ClientBaseHandler {
    private static final String TAG = "ClientKnxControlHandler";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {

        if (!(msg instanceof KnxResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        KnxResponse knxControlResponse = (KnxResponse) msg;
        ReturnCode returnCode = knxControlResponse.getReturnCode();
        Utils.showLogE(TAG, "-------" + knxControlResponse.toString());
        switch (returnCode) {
            case SUCCESS://设备操作成功,设备状态更新
                MsgUtils.dispatchEvent(ConnectResultEvent.KNX_RESPONSE, knxControlResponse);
                break;
            case FAILURE:
                break;
            default:
                break;
        }
    }
}
