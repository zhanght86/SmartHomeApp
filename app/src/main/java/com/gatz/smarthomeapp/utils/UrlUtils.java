package com.gatz.smarthomeapp.utils;

/**
 * Created by zhouh on 2017/2/22.
 */
public class UrlUtils {
    //<tag>
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String MACADDRESS = "macAdress";
    public static final String TERMINAL = "terminal";
    public static final String USERTYPEVIEW = "usertypeView";
    public static final String USERTYPE = "user";
    public static final String terminal = "app";
    public static final String city = "北京市";


    public static final String STATUS = "status";
    public static final String MSG = "msg";
    public static final String T = "t";
    public static final String CODE = "code";
    public static final String LOCK = "lock";
    public static final String BEDROOMS = "bedrooms";
    public static final String PROFILES = "profiles";
    public static final String BEDROOMID = "bedroomid";
    public static final String AIRCONDITIONING = "airconditioning";
    public static final String BEDROOMNAME = "bedroomname";
    public static final String ROOMID = "roomid";
    public static final String IMGURL = "imgurl";
    public static final String EQUIPMENTS = "equipments";

    public static final String TEXT = "text";
    public static final String APIKEY = "apikey";
    public static final String FILENAME = "filename";

    public static final String URL = "url";
    public static final String VERSION = "version";
    public static final String MESSAGE = "message";

    // </tag>

    //<code>
    public static final int REQUEST_LOGIN_CODE = 0X01;// 登录
    public static final int GET_VERSION_CODE = 0x04;//版本信息
    public static final int FINDBEDROOM_CODE = 0x10;//获取住户家信息
    public static final int FINDEPBID_CODE = 0x11;//详情页
    public static final int GET_CONNECTIP_CODE = 0x12;//局域网ip
//    public static final int GETENVIEQPMSG_CODE = 0x14;//获取室内设备列表
//    public static final int SELECTROOM_CODE = 0x21;//获取用户选择房子信息
//    public static final int LOGOUT_CODE = 0x13;//登出
//    public static final int GETENVIRONMENT_CODE = 0x13;//获取室内环境信息
//    public static final int FUTUREWEATHER_CODE = 0x24;
//    public static final int FINDALLDEVICE_CODE = 0x25;
    public static final int CONTROL_TEXT_CODE = 0x26;
    public static final int VERSION_PAD_CODE =  0x40;

    public static final int LSR_HOME_CODE = 0x29;
    public static final int LSR_AIR_CODE = 0x30;
    public static final int LSR_ENVI_CODE = 0x31;
    public static final int LSR_SECURITY_CODE = 0x32;
    public static final int LSR_TALK_CODE = 0x33;
    public static final int LSR_CTRL_CODE = 0x34;
    public static final int LSR_MSG_CODE = 0x35;
    // </code>

    //<url>
    //测试环境
//	public static final String UPLOAD_APK_URL="http://172.16.7.234/";
//	public static final String ROOT_URL = "http://172.16.7.234/api/";
//	public static final String PUSH = "http://123.56.184.180:7081/message/send";

    //生产环境
    public static final String UPLOAD_APK_URL = "https://home.zeroiot.com/";//"https://101.201.176.80/";
    public static final String ROOT_URL = "https://home.zeroiot.com/api/";
    public static final String PUSH = "https://home.zeroiot.com/push/api/message/send";

    public static final String LOGIN_URL = ROOT_URL + "user/login"; //登陆
    public static final String LOGOUT = ROOT_URL + "user/logout"; //登出
    public static final String SELECTROOM = ROOT_URL + "user/select/";//选择房间
    public static final String FINDBEDROOM = ROOT_URL + "rest/findbedroom/";//获取房間列表
    public static final String FINDEPBID = ROOT_URL + "rest/findepbid/";//详情页根据房间id 获取设备数据
    public static final String FINDALLDEVICE = ROOT_URL + "rest/findep/"; //获取所有设备
    public static final String FUTUREWEATHER = "http://op.juhe.cn/onebox/weather/query";//未来几天天气预报
    public static final String GETENVIEQPMSG = ROOT_URL + "envi/getEnvieqpMsg";//获取环境设备列表
    public static final String GETENVIRONMENT = ROOT_URL + "envi/getEnvironment";//获取环境信息
    public static final String GETHISTORYDEVICEDATALIST = "http://101.201.212.112:8081/iot-aas/api/device/getHistoryDeviceDataList";//获取近19小时和19天的环境数据
    public static final String LOG_REPORT = ROOT_URL + "log/report";
    public static final String UPLOAD_URL = ROOT_URL + "file/upfile?apikey=";//文件上传
    public static final String CONTROL_TEXT = ROOT_URL + "file/ctl";//上传语音文字看是否控制设备
    public static final String FEED_BACK = ROOT_URL + "rest/fb?apikey=";// 意见反馈
    public static final String GET_VERSION = ROOT_URL + "rest/feedbackVersion/";//获取版本
    public static final String VERSION_TYPE = "user";//标示是住户端
    public static final String GET_CONNECT_IP = ROOT_URL + "rest/ctlip/";
    public static final String GET_VERSION_PAD = ROOT_URL + "rest/version/";

    // </url>
}
