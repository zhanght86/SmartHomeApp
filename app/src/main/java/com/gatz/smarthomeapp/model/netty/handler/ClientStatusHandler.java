package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.GetStatusResponse;
import com.citic.zktd.saber.server.entity.json.enums.StatusType;
import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.response.ReturnCode;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.utils.Utils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhouh on 2017/5/31.
 */
public class ClientStatusHandler extends ClientBaseHandler{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {
        if (!(msg instanceof GetStatusResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        GetStatusResponse getStatusResponse = (GetStatusResponse) msg;
        ReturnCode returnCode = getStatusResponse.getReturnCode();
        StatusType statusType = getStatusResponse.getStatusType();
        switch (returnCode) {
            case SUCCESS:
                if(statusType.toString().equals(StatusType.GREEN_CIRCLE.name())) {
                    MsgUtils.dispatchEvent(ConnectResultEvent.GREENCIRCLE_RESPONSE, getStatusResponse);
                } else if (statusType.toString().equals(StatusType.KNX.name())) {
                    MsgUtils.dispatchEvent(ConnectResultEvent.KNX_STATUS_RESPONSE, getStatusResponse);
                }
                break;
            default:
                break;
        }
    }
}
