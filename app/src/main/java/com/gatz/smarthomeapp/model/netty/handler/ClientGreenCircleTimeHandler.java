package com.gatz.smarthomeapp.model.netty.handler;

import com.citic.zktd.saber.server.entity.json.header.JsonMessage;
import com.citic.zktd.saber.server.entity.protocol.response.ReturnCode;
import com.citic.zktd.saber.server.entity.quartz.JobDetailResponse;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by dell on 2017/11/7.
 */
public class ClientGreenCircleTimeHandler extends ClientBaseHandler{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonMessage msg) throws Exception {
        if (!(msg instanceof JobDetailResponse)) {
            ctx.fireChannelRead(msg);
            return;
        }
        JobDetailResponse jobDetailResponse = (JobDetailResponse) msg;
        ReturnCode returnCode = jobDetailResponse.getReturnCode();
        switch (returnCode) {
            case SUCCESS://设备操作成功,设备状态更新
                MsgUtils.dispatchEvent(ConnectResultEvent.GREENCIRCLE_TIME_RESPONSE, jobDetailResponse);
                break;
            case FAILURE:
                break;
            default:
                break;
        }
    }
}
