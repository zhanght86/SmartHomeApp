package com.gatz.smarthomeapp.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gatz.smarthomeapp.bean.AirStateBean;
import com.gatz.smarthomeapp.bean.KnxEquiptment;
import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.bean.Message;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.provider.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouh on 2017/3/6.
 */
public class DbUttil {
    public static final String DB_DIR = "/dnake/cfg/smart_db";
    public static final String DB_DIR_JOURNAL = "/dnake/cfg/smart_db-journal";

    public static final String DATA_BASE_AUTHORITY = "com.gatz.smarthomeapp.service.smartHomeservice.provider";
    public static final String VISIT_DB_URL = "content://"
            + DATA_BASE_AUTHORITY + "/";
    // user_table
    public static final String USER_TABLE = "user_table";
    public static final String I_PHONE = "phone";
    public static final String I_PWD = "password";
    public static final String I_SESSOION_ID = "sessoion_id";
    public static final String I_ROOM_ID = "room_id";
    public static final String I_UNIT_ID = "unit_id";
    public static final String I_COMMUNITY_ID = "community_id";
    public static final String I_BEDROOM_ID = "bedroom_ids";
    public static final String I_GATEWAY_STATUS = "gateway_status";
    public static final String I_CONNECT_IP = "connect_ip";
    public static final String I_BUILDING_NAME = "building_name";
    // knxdevice_table
    public static final String DEVICE_TABLE = "device_table";
    public static final String DEVICE_ROOM = "room";
    public static final String DEVICE_ROOM_ID = "room_id";
    public static final String DEVICE_NAME = "name";
    public static final String DEVICE_ID = "device_id";
    public static final String DEVICE_KNX_ADDRESS1 = "knx_address1";
    public static final String DEVICE_KNX_ADDRESS2 = "knx_address2";
    public static final String DEVICE_PROTOCOLS = "protocols";
    public static final String DEVICE_TYPE = "devicestype";
    public static final String DEVICE_KEY_WORDS = "keywords";
    public static final String DEVICE_STATUS = "status";
    // scene_table
    public static final String SCENE_TABLE = "scene_table";
    public static final String SCENE_NAME = "name";
    public static final String SCENE_LIST = "profiles";
    //air_protoclo_table
    public static final String AIR_STATES_TABLE = "air_states_table";
    public static final String AIR_DEVICE_ID = "device_id";
    public static final String AIR_PROTOCOL_ID = "protocol_id";
    public static final String AIR_FUNCTION_NAME = "functionname";
    public static final String AIR_PROTOCOL_ADDR = "protocol_addr";
    public static final String AIR_VALUETYPE = "value_type";
    public static final String AIR_DPID = "dpid";
    public static final String AIR_SPAN = "span";
    public static final String AIR_VALUE = "value";
    public static final String AIR_BEDROOM_ID = "bedroomid";
    //message table
    public static final String TABLE_NAME = "message";
    public static final String LI_TITLE = "title";
    public static final String LI_TYPE = "type";
    public static final String LI_CONTENT = "content";
    public static final String LI_TIME = "time";
    public static final String LI_READ = "read";
    public static final String LI_ID = "id";
    public static final String LI_URL = "imgurl";

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    //ping table
    public static final String PING_TIME_TABLE = "ping_time_table";
    public static final String PING_INTERVAL_TIME = "interval_time";
    public static final String PING_OUT_TIME = "out_time";
    //version table
    public static final String VERSION_TABLE = "version_table";
    public static final String VERSION_APP_NAME = "app_name";
    public static final String VERSION_NAME = "version_name";


    /**
     * 添加心跳时间
     * @param context
     * @param vT
     * @param oT
     * @return
     */
    public static boolean addPingTime(Context context, long vT, long oT){
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PING_INTERVAL_TIME, vT);
        values.put(PING_OUT_TIME, oT);
        Uri uri = cResolver
                .insert(Uri.parse(VISIT_DB_URL
                        + PING_TIME_TABLE), values);
        if (uri == null) {
            return false;
        }
        return true;
    }

    /**
     * 更新心跳参数时间
     * @param context
     * @param vT
     * @param oT
     * @return
     */
    public static int updataPingTime(Context context, long vT, long oT) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PING_INTERVAL_TIME, vT);
        values.put(PING_OUT_TIME, oT);
        return cResolver.update(Uri.parse(VISIT_DB_URL + PING_TIME_TABLE),
                values,
                null,
                null);
    }

    /**
     * 获取心跳vt时间
     * @param context
     * @return
     */
    public static long getPingVt(Context context){
        long time = 0;
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + PING_TIME_TABLE),
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int i = cursor.getColumnIndex(PING_INTERVAL_TIME);
                time = cursor.getInt(i);
            }
            cursor.close();
        }
        return time;
    }

    /**
     *  获取心跳超时时间
     * @param context
     * @return
     */
    public static long getPingOt(Context context){
        long time = 0;
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + PING_TIME_TABLE),
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int i = cursor.getColumnIndex(PING_OUT_TIME);
                time = cursor.getInt(i);
            }
            cursor.close();
        }
        return time;
    }


    /**
     * 添加用户信息
     *
     * @param context
     * @param phone
     * @param pwd
     * @param sessionId
     * @param roomId
     * @param communityId
     * @return
     */
    public static boolean addUser(Context context, String phone, String pwd, String sessionId,
                                  String roomId,
                                  String communityId,
                                  String unitId,
                                  int status, String ip, String building_name) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(I_PHONE, phone);
        values.put(I_PWD, pwd);
        values.put(I_SESSOION_ID, sessionId);
        values.put(I_UNIT_ID, unitId);
        values.put(I_ROOM_ID, roomId);
        values.put(I_COMMUNITY_ID, communityId);
        values.put(I_BEDROOM_ID, "default");
        values.put(I_GATEWAY_STATUS, status);
        values.put(I_CONNECT_IP, ip);
        values.put(I_BUILDING_NAME, building_name);
        Uri uri = cResolver
                .insert(Uri.parse(VISIT_DB_URL
                        + USER_TABLE), values);
        if (uri == null) {
            return false;
        }
        return true;
    }

    //添加房屋
    public static int updataUserBuildingName(Context context, String name) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(I_BUILDING_NAME, name);
        return cResolver.update(Uri.parse(VISIT_DB_URL + USER_TABLE), values, null, null);
    }

    public static String getUserBuildingName(Context context) {
        String name = "国安府";
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + USER_TABLE),
                null, null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(I_BUILDING_NAME));
            }
            cursor.close();
        }
        return name;
    }

    /**
     * 更新连接的ip
     * @param context
     * @param ip
     * @return
     */
    public static int updataConnectIp(Context context, String ip) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.I_CONNECT_IP, ip);
        return cResolver.update(Uri.parse(VISIT_DB_URL + USER_TABLE),
                values,
                null,
                null);
    }

    /**
     * 获取连接ip的值
     * @param context
     * @return
     */
    public static String getConnectIp(Context context) {
        String ip = "0";
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + USER_TABLE),
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int i = cursor.getColumnIndex(I_CONNECT_IP);
                ip = cursor.getString(i);
            }
            cursor.close();
        }
        return ip;
    }

    /**
     * 更新网关状态
     *
     * @param context
     * @param status
     * @return
     */
    public static int updataGatewayStatus(Context context, int status) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.I_GATEWAY_STATUS, status);
        return cResolver.update(Uri.parse(VISIT_DB_URL + USER_TABLE),
                values,
                null,
                null);
    }

    /**
     * 更新sessionId
     * @param context
     * @param sId
     * @return
     */
    public static int updataUserSessionId(Context context, String sId){
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.I_SESSOION_ID, sId);
        return cResolver.update(Uri.parse(VISIT_DB_URL + USER_TABLE),
                values,
                null,
                null);
    }

    /**
     * 获取网关状态 0:故障 1：连接成功
     *
     * @param context
     * @return
     */
    public static int getGatewayStatus(Context context) {
        int status = 0;
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + USER_TABLE),
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int i = cursor.getColumnIndex(I_GATEWAY_STATUS);
                status = cursor.getInt(i);
            }
            cursor.close();
        }
        return status;
    }

    /**
     * 添加bedroomIds
     *
     * @param context
     * @param s
     * @return
     */
    public static int updataUser(Context context, String s) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.I_BEDROOM_ID, s);
        return cResolver.update(Uri.parse(VISIT_DB_URL + USER_TABLE),
                values,
                null,
                null);
    }

    /**
     * @param context
     * @return
     */
    public static boolean isUserExist(Context context) {
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + USER_TABLE),
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getColumnIndex(DbUttil.I_PHONE);
                String phone = cursor.getString(id);
                if (!Utils.isEmpty(phone)) {
                    cursor.close();
                    return true;
                } else {
                    cursor.close();
                    return false;
                }
            }
            cursor.close();
        } else {
            return false;
        }
        return false;
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static UserInfoBean getUser(Context context) {
        UserInfoBean bean = new UserInfoBean();
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + USER_TABLE),
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                bean.setUserName(cursor.getString(1));
                bean.setPsw(cursor.getString(2));
                bean.setSessionId(cursor.getString(3));
                bean.setRoomId(cursor.getString(4));
                bean.setUnitId(cursor.getString(5));
                bean.setCommunityId(cursor.getString(6));
                bean.setBedroomId(cursor.getString(7));
                cursor.close();
                return bean;
            }
            cursor.close();
        }
        return null;
    }

    /**
     * 添加设备
     *
     * @param context
     * @param deviceName
     * @param room
     * @param deviceId
     * @param protocols
     * @param status
     * @return
     */
    public static boolean addDevice(Context context, String deviceName, String room, String roomId,
                                    String deviceId, String knxAddr1, String knxAddr2, String protocols, String devicestype, String keywords, String status) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.DEVICE_NAME, deviceName);
        values.put(DbUttil.DEVICE_ROOM, room);
        values.put(DbUttil.DEVICE_ROOM_ID, roomId);
        values.put(DbUttil.DEVICE_ID, deviceId);
        values.put(DbUttil.DEVICE_KNX_ADDRESS1, knxAddr1);
        values.put(DbUttil.DEVICE_KNX_ADDRESS2, knxAddr2);
        values.put(DbUttil.DEVICE_PROTOCOLS, protocols);
        values.put(DbUttil.DEVICE_TYPE, devicestype);
        values.put(DbUttil.DEVICE_KEY_WORDS, keywords);
        values.put(DbUttil.DEVICE_STATUS, status);
        Uri uri = cResolver.insert(Uri.parse(VISIT_DB_URL + DEVICE_TABLE), values);
        if (uri == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取设备
     *
     * @param context
     * @param deviceType
     * @return
     */
    public static List<KnxEquiptment> getDevices(Context context, String deviceType) {
        List<KnxEquiptment> list = null;
        ContentResolver cResolver = context.getContentResolver();
        String selection = DEVICE_KEY_WORDS + "=?";
        String[] selectionArgs = {deviceType};
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + DEVICE_TABLE),
                null, selection, selectionArgs, null);
        if (cursor == null) {
            return null;
        }
        list = new ArrayList<>();
        while (cursor.moveToNext()) {
            KnxEquiptment equiptment = new KnxEquiptment();
            equiptment.setDevicename(cursor.getString(cursor.getColumnIndex(DEVICE_NAME)));
            equiptment.setBedroomname(cursor.getString(cursor.getColumnIndex(DEVICE_ROOM)));
            equiptment.setBedroomid(cursor.getString(cursor.getColumnIndex(DEVICE_ROOM_ID)));
            equiptment.setDeviceid(cursor.getString(cursor.getColumnIndex(DEVICE_ID)));
            String json = cursor.getString(cursor.getColumnIndex(DEVICE_PROTOCOLS));
            List<KnxProtocol> protocols = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    KnxProtocol protocol = new KnxProtocol();
                    JSONObject object = array.getJSONObject(i);
                    protocol.setProtocolid(object.getString("protocolid"));
                    protocol.setProtocoltype(object.getString("protocoltype"));
                    protocol.setDpId(object.getString("dpId"));
                    protocol.setProtocolAddr(object.getString("protocolAddr"));
                    protocol.setFunctionname(object.getString("functionname"));
                    protocol.setSpan(object.getString("span"));
                    protocols.add(protocol);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            equiptment.setProtocols(protocols);
            equiptment.setDevicestype(cursor.getString(cursor.getColumnIndex(DEVICE_TYPE)));
            equiptment.setKeywords(cursor.getString(cursor.getColumnIndex(DEVICE_KEY_WORDS)));
            equiptment.setState(cursor.getString(cursor.getColumnIndex(DEVICE_STATUS)));
            list.add(equiptment);
        }
        cursor.close();
        return list;
    }

    /**
     * 添加场景
     *
     * @param context
     * @param name
     * @param protocols
     * @return
     */
    public static boolean addScenes(Context context, String name, String protocols) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.SCENE_NAME, name);
        values.put(DbUttil.SCENE_LIST, protocols);
        Uri uri = cResolver.insert(Uri.parse(VISIT_DB_URL + SCENE_TABLE), values);
        if (uri == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取房屋场景
     *
     * @param context
     * @param name
     * @return
     */
    public static List<KnxProtocol> getSceneProtocols(Context context, String name) {
        List<KnxProtocol> list = null;
        ContentResolver cResolver = context.getContentResolver();
        String selection = SCENE_NAME + "=?";
        String[] selectionArgs = {name};
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + SCENE_TABLE),
                null, selection, selectionArgs, null);
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            list = new ArrayList<>();
            int index = cursor.getColumnIndex(SCENE_LIST);
            String json = cursor.getString(index);
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    KnxProtocol knxProtocol = new KnxProtocol();
                    knxProtocol.setProtocolid(object.getString("protocolid"));
                    knxProtocol.setProtocoltype(object.getString("protocoltype"));
                    knxProtocol.setDpId(object.getString("dpId"));
                    knxProtocol.setProtocolAddr(object.getString("protocolAddr"));
                    knxProtocol.setFunctionname(object.getString("functionname"));
                    knxProtocol.setCmdvaule(object.getString("cmdvaule"));
                    knxProtocol.setSpan(object.getString("span"));
                    list.add(knxProtocol);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    /**
     * 通过deviceId获取设备的protocol
     *
     * @param context
     * @param deviceId
     * @return
     */
    public static List<KnxProtocol> getKnxProtocol(Context context, String deviceId) {
        List<KnxProtocol> knxProtocols = new ArrayList<>();
        ContentResolver cResolver = context.getContentResolver();
        String selection = DEVICE_ID + "=?";
        String[] selectionArgs = {deviceId};
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + DEVICE_TABLE),
                null, selection, selectionArgs, null);
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(DEVICE_PROTOCOLS);
            String json = cursor.getString(index);
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    KnxProtocol knxProtocol = new KnxProtocol();
                    knxProtocol.setProtocolid(object.getString("protocolid"));
                    knxProtocol.setProtocoltype(object.getString("protocoltype"));
                    knxProtocol.setDpId(object.getString("dpId"));
                    knxProtocol.setProtocolAddr(object.getString("protocolAddr"));
                    knxProtocol.setFunctionname(object.getString("functionname"));
                    knxProtocol.setSpan(object.getString("span"));
                    knxProtocols.add(knxProtocol);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return knxProtocols;
    }

    /**
     * 通过knxAddr更新数据库中设备的状态信息
     *
     * @param knxAddr
     * @param value
     */
    public static int updataDeviceStatus(Context context, String knxAddr, String value) {
        int code = isKnxAddrRight(context, knxAddr);
        String selection = DEVICE_KNX_ADDRESS1 + "=?";
        if (code == 0) {
            return 0;
        }
        if (code == 2) {
            selection = DEVICE_KNX_ADDRESS2 + "=?";
        }
        ContentResolver cResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(DEVICE_STATUS, value);
        String[] selectionArgs = {knxAddr};
        return cResolver.update(Uri.parse(VISIT_DB_URL + DEVICE_TABLE),
                cv,
                selection,
                selectionArgs);
    }

    /**
     * addr是否合法
     *
     * @param context
     * @param knxAddr
     * @return
     */
    public static int isKnxAddrRight(Context context, String knxAddr) {
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + DEVICE_TABLE),
                null, null, null, null);
        if (cursor == null) {
            return 0;
        }
        while (cursor.moveToNext()) {
            String addr1 = cursor.getString(cursor.getColumnIndex(DEVICE_KNX_ADDRESS1));
            String addr2 = cursor.getString(cursor.getColumnIndex(DEVICE_KNX_ADDRESS2));
            if (addr1 != null) {
                if (addr1.equals(knxAddr)) {
                    cursor.close();
                    return 1;
                }
            }
            if (addr2 != null) {
                if (addr2.equals(knxAddr)) {
                    cursor.close();
                    return 2;
                }
            }
        }
        cursor.close();
        return 0;
    }

    /**
     * 存储空调状态
     *
     * @param context
     * @param bean
     * @return
     */
    public static boolean addAirStates(Context context, AirStateBean bean, KnxProtocol protocol) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.AIR_DEVICE_ID, protocol.getDeviceid());
        values.put(DbUttil.AIR_PROTOCOL_ID, protocol.getProtocolid());
        values.put(DbUttil.AIR_FUNCTION_NAME, protocol.getFunctionname());
        values.put(DbUttil.AIR_PROTOCOL_ADDR, bean.getProtocolAddr());
        values.put(DbUttil.AIR_DPID, protocol.getDpId());
        values.put(DbUttil.AIR_SPAN, protocol.getSpan());
        values.put(DbUttil.AIR_BEDROOM_ID, protocol.getBedroomid());
        if (protocol.getFunctionname().equals("开关")) {
            values.put(DbUttil.AIR_VALUETYPE, "MODEL");
            values.put(DbUttil.AIR_VALUE, 0);
        } else if (protocol.getFunctionname().equals("湿度")) {
            values.put(DbUttil.AIR_VALUETYPE, "WET");
            values.put(DbUttil.AIR_VALUE, 2);
        } else if (protocol.getFunctionname().equals("风速")) {
            values.put(DbUttil.AIR_VALUETYPE, "WIND_SPEED");
            values.put(DbUttil.AIR_VALUE, 2);
        } else if (protocol.getFunctionname().equals("模式")) {
            values.put(DbUttil.AIR_VALUETYPE, "MODEL");
            values.put(DbUttil.AIR_VALUE, 2);
        } else {
            String address = protocol.getProtocolAddr();
            if (address.contains("_SET_")) {
                values.put(DbUttil.AIR_VALUETYPE, "TEMPERATURE");
                values.put(DbUttil.AIR_VALUE, 200);
            } else if (address.contains("_WET_")) {
                values.put(DbUttil.AIR_VALUETYPE, "WET");
                values.put(DbUttil.AIR_VALUE, 200);
            } else {
                values.put(DbUttil.AIR_VALUETYPE, "TEMPERATURE");
                values.put(DbUttil.AIR_VALUE, 200);
            }
        }
        Uri uri = cResolver.insert(Uri.parse(VISIT_DB_URL + AIR_STATES_TABLE), values);
        if (uri == null) {
            return false;
        }
        return true;
    }

//    /**
//     * 添加空调面板
//     *
//     * @param context
//     * @param protocol
//     * @return
//     */
//    public static boolean addAirPanel(Context context, KnxProtocol protocol) {
//        ContentResolver cResolver = context.getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(DbUttil.AIR_DEVICE_ID, protocol.getDeviceid());
//        values.put(DbUttil.AIR_FUNCTION_NAME, protocol.getFunctionname());
//        values.put(DbUttil.AIR_SPAN, protocol.getSpan());
//        values.put(DbUttil.AIR_DPID, protocol.getDpId());
//        values.put(DbUttil.AIR_PROTOCOL_ADDR, protocol.getProtocolAddr());
//        values.put(DbUttil.AIR_PROTOCOL_ID, protocol.getProtocolid());
//        Uri uri = cResolver.insert(Uri.parse(VISIT_DB_URL + AIR_PANEL_TABLE), values);
//        if (uri == null) {
//            return false;
//        }
//        return true;
//    }

    /**
     * 获取空调的状态
     *
     * @param context
     * @return
     */
    public static Map<String, Integer> getAirState(Context context){
        Map<String, Integer> map = null;
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + AIR_STATES_TABLE),
                null, null, null, null);
        if (cursor != null) {
            map = new HashMap<>();
            while (cursor.moveToNext()) {
                String key = cursor.getString(cursor.getColumnIndex(AIR_PROTOCOL_ADDR));
                int value = cursor.getInt(cursor.getColumnIndex(AIR_VALUE));
                map.put(key, value);
            }
            cursor.close();
        }
        return map;
    }

    /**
     * 更新空调状态
     *
     * @param context
     * @param greenAddr
     * @param value
     * @return
     */
    public static int updataAirStateTable(Context context, String greenAddr, int value) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(AIR_VALUE, value);
        String selection = AIR_PROTOCOL_ADDR + "=?";
        String[] selectionArgs = {greenAddr};
        return cResolver.update(Uri.parse(VISIT_DB_URL + AIR_STATES_TABLE),
                cv,
                selection,
                selectionArgs);
    }

    /**
     * 获取房间name
     */
    public static String getDedRoomName(Context context, String key) {
        String ids = null;
        String name = null;
        ContentResolver cResolver = context.getContentResolver();
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + USER_TABLE),
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ids = cursor.getString(cursor.getColumnIndex(I_BEDROOM_ID));
            }
            cursor.close();
        }
        if (ids != null) {
            try {
                JSONArray array = new JSONArray(ids);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    if (object.has(key)) {
                        name = object.getString(key);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public static void addMessage(Context context, Message message) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LI_TITLE, message.getTitle());
        values.put(LI_CONTENT, message.getContent());
        values.put(LI_TYPE, message.getType());
        values.put(LI_TIME, message.getTime());
        values.put(LI_READ, message.getIsRead());
        values.put(LI_URL, message.getImgUrl());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public static void updateMessage(Context context, String read, String id) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LI_READ, read);
        db.update(TABLE_NAME, values, LI_ID + "=?", new String[]{id});
        db.close();
    }

    public static void deleteMessage(Context context, String id) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        db.delete(TABLE_NAME, LI_ID + "=?", new String[]{id});
        db.close();
    }

    public static List<Message> queryMessage(Context context) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(LI_TITLE));
            String type = cursor.getString(cursor.getColumnIndex(LI_TYPE));
            String content = cursor.getString(cursor.getColumnIndex(LI_CONTENT));
            String time = cursor.getString(cursor.getColumnIndex(LI_TIME));
            String read = cursor.getString(cursor.getColumnIndex(LI_READ));
            int id = cursor.getInt(cursor.getColumnIndex(LI_ID));
            String imgUrl = cursor.getString(cursor.getColumnIndex(LI_URL));
            Message message = new Message(content, type, time, title, read, imgUrl, id);
            messages.add(message);
        }
        cursor.close();
        db.close();
        return messages;
    }


    /**
     * 删除本地数据库
     */
    public static boolean deleteDbTable(String dir){
        File fileDb = new File(dir);
        if (fileDb.isFile() && fileDb.exists()) {
            return fileDb.delete();
        } else {
            return true;
        }
    }

    /**
     *  添加版本数据
     * @param context
     * @param app_name
     * @param version_name
     * @return
     */
    public static boolean addVersionCode(Context context, String app_name, String version_name) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(DbUttil.VERSION_APP_NAME, app_name);
        values.put(DbUttil.VERSION_NAME, version_name);
        Uri uri = cResolver.insert(Uri.parse(VISIT_DB_URL + VERSION_TABLE), values);
        if (uri == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取版本数据
     * @param context
     * @param app_name
     * @return
     */
    public static String getVersionCode(Context context, String app_name) {
        String version = "";
        ContentResolver cResolver = context.getContentResolver();
        String selection = VERSION_APP_NAME + "=?";
        String[] selectionArgs = {app_name};
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + VERSION_TABLE),
                null, selection, selectionArgs, null);
        if (cursor != null){
            while (cursor.moveToNext()) {
                version = cursor.getString(cursor.getColumnIndex(VERSION_NAME));
            }
            cursor.close();
        }
        return version;
    }

    /**
     * 更新版本状态
     * @param context
     * @param appName
     * @param v
     * @return
     */
    public static int updataVersionCode(Context context, String appName, String v) {
        ContentResolver cResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(VERSION_NAME, v);
        String selection = VERSION_APP_NAME + "=?";
        String[] selectionArgs = {appName};
        return cResolver.update(Uri.parse(VISIT_DB_URL + VERSION_TABLE),
                cv,
                selection,
                selectionArgs);
    }

    /**
     * 获取灯设备的地址列表
     */
    public static List<String> getLightAddress(Context context) {
        List<String> list = new ArrayList<>();
        ContentResolver cResolver = context.getContentResolver();
        String selection = DEVICE_KEY_WORDS + "=?";
        String[] selectionArgs = {"灯"};
        Cursor cursor = cResolver.query(Uri.parse(VISIT_DB_URL + DEVICE_TABLE),
                null, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex(DEVICE_KNX_ADDRESS1)));
            }
            cursor.close();
        }
        return list;
    }
}
