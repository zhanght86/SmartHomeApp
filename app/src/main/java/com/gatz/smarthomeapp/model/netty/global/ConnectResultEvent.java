package com.gatz.smarthomeapp.model.netty.global;

/**
 * netty连接结果事件
 * Created by Debby on 2017/2/7.
 */
public class ConnectResultEvent {
    //netty初始化信息
    public static final String INIT = "init";
    //连接失败
    public static final String CONNECT_FAILURE = "connect_failure";
    //连接成功
    public static final String CONNECT_SUCCESS = "connect_success";
    //智能网关不存在
    public static final String GATEWAY_UNEXIST = "gateway_unexist";
    //connect的session无效
    public static final String CONNECT_SESSION_INVALID = "connect_session_invalid";
    //session无效
    public static final String SESSION_INVALID = "session_invalid";
    //ID无效
    public static final String DEVICE_UNIQUE_ID_NOT_EMPTY = "device_unique_id_not_empty";
    //设备操作失败
    public static final String FAILURE = "failure";
    //心跳发送成功
    public static final String PING_SUCCESS = "ping_success";
    //用户被踢下线
    public static final String USER_KICKED = "user_kicked";
    //三衡空调响应
    public static final String GREENCIRCLE_RESPONSE = "greencircle_response";
    //knx设备响应
    public static final String KNX_RESPONSE = "knx_response";
    //语音会话
    public static final String VOICE_SESSION = "voice_session";
    //请求会话
    public static final String REQUEST_SESSION = "request_session";
    //KNX设备的状态
    public static final String KNX_STATUS_RESPONSE = "knx_status_response";
    //时间处理
    public static final String GREENCIRCLE_TIME_RESPONSE = "greencircle_time_response";
}
