package com.gatz.smarthomeapp.model.netty.coder;

import com.citic.zktd.saber.server.entity.json.ConnectResponse;
import com.citic.zktd.saber.server.entity.json.CustomDefineMessage;
import com.citic.zktd.saber.server.entity.json.GetStatusResponse;
import com.citic.zktd.saber.server.entity.json.GreenCircleResponse;
import com.citic.zktd.saber.server.entity.json.KnxResponse;
import com.citic.zktd.saber.server.entity.json.PingResponse;
import com.citic.zktd.saber.server.entity.json.SecondLoginAnnounceMessage;
import com.citic.zktd.saber.server.entity.json.announce.GatewayAnnounceMessage;
import com.citic.zktd.saber.server.entity.json.announce.GreenCircleQuartzAnnounceMessage;
import com.citic.zktd.saber.server.entity.json.enums.JsonMessageType;
import com.citic.zktd.saber.server.entity.json.enums.ObjectMessageType;
import com.citic.zktd.saber.server.entity.protocol.header.Message;
import com.citic.zktd.saber.server.entity.quartz.JobDetailResponse;
import com.gatz.smarthomeapp.model.netty.handler.ClientStatusHandler;
import com.gatz.smarthomeapp.model.netty.utils.Utils;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.InputStreamReader;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Description: 解码器<br/>
 * Copyright (c) 2015, 中信国安
 *
 * @author david
 * @date 2015年8月26日 上午10:56:21
 */
public class AppJsonDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final String TAG = "AppJsonDecoder";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int protocol = in.readUnsignedByte();
        if (protocol != Message.PROTOCOL) {
            Utils.showInfoLog(TAG, "协议不正确");
            throw new IllegalArgumentException("协议不正确");
        }

        Integer version = (int) in.readUnsignedShort();
        if ((version == null) || (!version.equals(Message.VERSION))) {
            Utils.showInfoLog(TAG, "协议版本不正确！");
            throw new IllegalArgumentException("协议版本不正确");
        }

        int jsonMessageTypeInt = in.readUnsignedByte();
        Utils.showInfoLog(TAG, "jsonMessageTypeInt={}" + jsonMessageTypeInt);
        JsonMessageType jsonMessageType = JsonMessageType.getMessageType(jsonMessageTypeInt);
        if (null == jsonMessageType) {
            Utils.showInfoLog(TAG, "JsonMessageType={}不正确" + jsonMessageTypeInt);
            throw new IllegalArgumentException("消息类型{Request Response}不正确");
        }

        int objMessageTypeInt = (int) in.readUnsignedShort();
        Utils.showInfoLog(TAG, "objMessageTypeInt={}" + objMessageTypeInt);
        ObjectMessageType objMessageType = ObjectMessageType.getObjectMessageType(objMessageTypeInt);
        if (null == objMessageType) {
            Utils.showInfoLog(TAG, "ObjectMessageType={}不正确" + objMessageTypeInt);
            throw new IllegalArgumentException("消息类型不正确");
        }

        in.readUnsignedInt();
        ObjectMapper om = new ObjectMapper();
        InputStreamReader reader = new InputStreamReader(new ByteBufInputStream(in));
        switch (jsonMessageType) {
            case ANNOUNCE:
                switch (objMessageType) {
                    case GATEWAY_STATUS_ANNOUNCE_MESSAGE:
                        GatewayAnnounceMessage gatewayAnnounceMessage = om.readValue(reader, GatewayAnnounceMessage.class);
                        out.add(gatewayAnnounceMessage);
                        break;
                    case SECOND_LOGIN_ANNOUNCE_MESSAGE:
                        SecondLoginAnnounceMessage secondLoginAnnounceMessage = om.readValue(reader, SecondLoginAnnounceMessage.class);
                        out.add(secondLoginAnnounceMessage);
                        break;
                    case GREEN_CIRCLE_QUARTZ_ANNOUNCE_MESSAGE:
                        GreenCircleQuartzAnnounceMessage greenCircleQuartzAnnounceMessage = om.readValue(reader, GreenCircleQuartzAnnounceMessage.class);
                        out.add(greenCircleQuartzAnnounceMessage);
                        break;
                    default:
                        break;
                }
                break;
            case MESSAGE:
                switch (objMessageType) {
                    case CUSTOM_DEFINE_MESSAGE:
                        CustomDefineMessage customDefineMessage = om.readValue(reader, CustomDefineMessage.class);
                        out.add(customDefineMessage);
                        Utils.showInfoLog(TAG, "CUSTOM_DEFINE_MESSAGE" + customDefineMessage.toString());
                        break;
                    default:
                        break;
                }
                break;
            case REQUEST:
                switch (objMessageType) {
                    default:
                        break;
                }
                break;
            case RESPONSE:
                switch (objMessageType) {
                    case KNX_RESPONSE:
                        KnxResponse knxResponse = om.readValue(reader, KnxResponse.class);
                        out.add(knxResponse);
                        break;
                    case PING_RESPONSE:
                        PingResponse pingResponse = om.readValue(reader, PingResponse.class);
                        out.add(pingResponse);
                        break;
                    case CONNECT_RESPONSE:
                        ConnectResponse connectResponse = om.readValue(reader, ConnectResponse.class);
                        out.add(connectResponse);
                        break;
                    case GREEN_RESPONSE:
                        GreenCircleResponse greenCircleResponse = om.readValue(reader, GreenCircleResponse.class);
                        out.add(greenCircleResponse);
                        break;
                    case GET_STATUS_RESPONSE:
                        GetStatusResponse getStatusResponse = om.readValue(reader, GetStatusResponse.class);
                        out.add(getStatusResponse);
                        break;

                    case GREEN_CIRCLE_SCHEDULE_RESPONSE:
                        JobDetailResponse jobDetailResponse = om.readValue(reader, JobDetailResponse.class);
                        out.add(jobDetailResponse);
                        break;

                    default:
                        Utils.showInfoLog(TAG, "未知响应");
                        break;
                }
                break;
            default:
                break;
        }
    }
}
