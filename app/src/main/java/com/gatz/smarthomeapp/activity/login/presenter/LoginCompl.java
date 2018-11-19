package com.gatz.smarthomeapp.activity.login.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.citic.zktd.saber.server.entity.json.enums.GreenCommandAddress;
import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.activity.login.model.LoginModel;
import com.gatz.smarthomeapp.activity.login.view.IinputView;
import com.gatz.smarthomeapp.base.BasePresenter;
import com.gatz.smarthomeapp.bean.AirConditioning;
import com.gatz.smarthomeapp.bean.AirStateBean;
import com.gatz.smarthomeapp.bean.Chamber;
import com.gatz.smarthomeapp.bean.HouseInfo;
import com.gatz.smarthomeapp.bean.KnxEquiptment;
import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.bean.Profile;
import com.gatz.smarthomeapp.bean.Result;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.model.netty.AppClient;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.JsonUtil;
import com.gatz.smarthomeapp.utils.LoginInfo;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;
import com.hwangjr.rxbus.RxBus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhouh on 2017/3/6.
 */
public class LoginCompl implements IloginPresenter, BasePresenter {
    private static final String TAG = "LoginCompl-";
    private IinputView iinputView;
    private LoginModel loginModel;
    private Context context;
    private AppClient appClient = AppClient.getInstance();

    @Override
    public void login(Context context) {
        String phoneNum = iinputView.getUserPhone();
        String psw = iinputView.getUserPwd();
        if (Utils.isEmpty(phoneNum)) {
            iinputView.loginNameErr0();
            return;
        }
        if (!Utils.isUserIdLawful(phoneNum)) {
            iinputView.loginNameErr1();
            return;
        }
        if (Utils.isEmpty(psw)) {
            iinputView.loginPwdErr0();
            return;
        }
        if (psw.length() < 6) {
            iinputView.loginPwdErr1();
            return;
        }
        if (psw.length() > 20) {
            iinputView.loginPwdErr2();
            return;
        }
//        if (!Utils.isNetworkConnected(context)) {
//            iinputView.loginIntentFailed();
//            return;
//        }
        iinputView.dialogShow();
        loginModel.login(iinputView.getUserPhone(), iinputView.getUserPwd());
    }

    @Override
    public void startLoginPresent(boolean is) {
        Utils.showLogE(TAG, "startLoginPresent");
        if (DbUttil.isUserExist(context)) {
            UserInfoBean bean = DbUttil.getUser(context);
            if (bean != null) {
                LoginModel.setNettyInfo(bean);
                iinputView.loginAlready();
                //NettyUtils.sendHeart(context);
            }
        } else {
            Utils.showLogE(TAG, "第一次登录");
        }
    }

    @Override
    public void destoryLoginPresent() {
        iinputView = null;
        loginModel = null;
    }

    public LoginCompl(IinputView iinputView, Context ctx) {
        this.iinputView = iinputView;
        this.context = ctx;
        loginModel = new LoginModel(callBack);
    }

    private ObserverCallBack callBack = new ObserverCallBack() {
        @Override
        public void onSuccessHttp(String responseInofo, int resultCode) {
            if (resultCode == UrlUtils.REQUEST_LOGIN_CODE) {
                if (loginSucess(responseInofo)) {
                    iinputView.dialogDisMiss(true);
                } else {
                    iinputView.dialogDisMiss(false);
                }
            } else if (resultCode == UrlUtils.FINDBEDROOM_CODE) {
                getRoomsSucess(responseInofo);
            } else if (resultCode == UrlUtils.FINDEPBID_CODE) {
                getDeviceSucess(responseInofo);
            } else if (resultCode == UrlUtils.GET_CONNECTIP_CODE) {
                getIpSucess(responseInofo);
            }
        }

        @Override
        public void onFailureHttp(IOException e, int resultCode) {
            iinputView.fail(e, e.toString());
            iinputView.dialogDisMiss(false);
        }

        @Override
        public void setData(Object obj) {

        }
    };

    private void getIpSucess(String res) {
        Utils.showLogE(TAG, "getIpSucess::" + res);
        String ip = JsonUtil.getConnectIp(res);
        if (!ip.equals("")) {
            //172.16.7.181  6260 样板间局域网
            //ip = "172.16.7.181";
            DbUttil.updataConnectIp(context, ip);
            RxBus.get().post(Utils.GET_CONNECT_IP, ip);
        }
    }

    private void getRooms() {
        loginModel.getRooms();
    }

    private void getRoomsSucess(String info) {
        Utils.showLogE(TAG, "getRoomsSucess----------" + info);
        List<Object> list = JsonUtil.analyzeFindRoom(info);
        if (null != list) {
            if (list.size() > 0) {
                int code = Integer.parseInt((String) list.get(2));
                String msg = (String) list.get(1);
                if (code == 200) {
                    AirConditioning airConditioning = (AirConditioning) list.get(3);
                    if (airConditioning != null) {
                        List<KnxProtocol> ps = airConditioning.getProtocols();
                        if (ps != null) {
                            for (KnxProtocol protocol : ps) {
                                AirStateBean bean = new AirStateBean();
                                bean.setProtocolAddr(protocol.getProtocolAddr());
                                //TODO 添加value_type value
                                DbUttil.addAirStates(context, bean, protocol);
                            }
                        }
                    }
                    List<Profile> profiles = (List<Profile>) list.get(4);
                    if (profiles != null) {
                        for (Profile profile : profiles) {
                            List<KnxProtocol> protocols = profile.getProtocols();
                            JSONArray jsonArray = new JSONArray();
                            for (int i = 0; i < protocols.size(); i++) {
                                JSONObject o = new JSONObject();
                                KnxProtocol knxProtocol = protocols.get(i);
                                try {
                                    o.put("protocolid", knxProtocol.getProtocolid());
                                    o.put("functionname", knxProtocol.getFunctionname());
                                    o.put("protocolAddr", knxProtocol.getProtocolAddr());
                                    o.put("dpId", knxProtocol.getDpId());
                                    o.put("span", knxProtocol.getSpan());
                                    o.put("cmdvaule", knxProtocol.getCmdvaule());
                                    o.put("protocoltype", knxProtocol.getProtocoltype());
                                    jsonArray.put(i, o);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Utils.showLogE(TAG, "=======intset profiles=======");
                            DbUttil.addScenes(context, profile.getProfilename(), jsonArray.toString());
                        }
                    }
                    List<Chamber> chambers = new ArrayList<>();
                    chambers.clear();
                    chambers.addAll((List<Chamber>) list.get(6));
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < chambers.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(chambers.get(i).getBedroomid(), chambers.get(i).getBedroomname());
                            array.put(i, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Utils.showLogE(TAG, "putinrooms:::::::" + array.toString());
                    DbUttil.updataUser(context, array.toString());
                    getDevices();
                } else {
                    iinputView.fail(null, msg);
                }
            } else {
                iinputView.fail(null, context.getString(R.string.null_rooms));
            }
        }
    }

    private void getDevices() {
        UserInfoBean bean = DbUttil.getUser(context);
        if (bean != null) {
            String bedroomIds = bean.getBedroomId();
            try {
                JSONArray array = new JSONArray(bedroomIds);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Iterator iterator = object.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        loginModel.getDevices(key);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDeviceSucess(String responseInofo) {
        synchronized (this) {
            List<KnxEquiptment> equiptments = new ArrayList<>();
            Utils.showLogE(TAG, "getDevicesSucess----------" + responseInofo);
            List<Object> list = JsonUtil.analyzeEquipments(responseInofo);
            String knxAddr1 = null;
            String knxAddr2 = null;
            int code = Integer.parseInt((String) list.get(2));
            if (200 == code && list.size() > 3) {
                equiptments.clear();
                if (list.get(3) != null) {
                    equiptments.addAll((List<KnxEquiptment>) list.get(3));
                    for (KnxEquiptment equiptment : equiptments) {
                        if (!equiptment.getKeywords().equals("空调")) {
                            JSONArray jsonArray = new JSONArray();
                            List<KnxProtocol> knxProtocols = equiptment.getProtocols();
                            for (int i = 0; i < knxProtocols.size(); i++) {
                                KnxProtocol protocol = knxProtocols.get(i);
                                //灯对应一个地址
                                if (protocol.getFunctionname().equals("反馈")) {
                                    knxAddr1 = protocol.getProtocolAddr();
                                    knxAddr2 = "none";
                                }
                                //窗帘地址映射为deviceId
                                if (equiptment.getKeywords().equals("窗帘")) {
                                    if (protocol.getFunctionname().equals("开关")) {
                                        knxAddr1 = protocol.getProtocolAddr();
                                    } else if (protocol.getFunctionname().equals("暂停")) {
                                        knxAddr2 = protocol.getProtocolAddr();
                                    }
                                }
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("protocolid", protocol.getProtocolid());
                                    object.put("functionname", protocol.getFunctionname());
                                    object.put("protocolAddr", protocol.getProtocolAddr());
                                    object.put("dpId", protocol.getDpId());
                                    object.put("span", protocol.getSpan());
                                    object.put("cmdvaule", protocol.getCmdvaule());
                                    object.put("protocoltype", protocol.getProtocoltype());
                                    jsonArray.put(i, object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            String state = "off";
                            if (equiptment.getState() != null) {
                                state = JsonUtil.getDeviceStatus(equiptment.getState());
                            }
                            DbUttil.addDevice(context, equiptment.getDevicename(),
                                    equiptment.getBedroomname(),
                                    equiptment.getBedroomid(),
                                    equiptment.getDeviceid(),
                                    knxAddr1,
                                    knxAddr2,
                                    jsonArray.toString(),
                                    equiptment.getDevicestype(),
                                    equiptment.getKeywords(),
                                    state);
                        } else {
                            String bedRoomId = null;
                            JSONArray jsonArray = new JSONArray();
                            List<KnxProtocol> knxProtocols = equiptment.getProtocols();
                            for (KnxProtocol protocol : knxProtocols) {
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("protocolid", protocol.getProtocolid());
                                    object.put("functionname", protocol.getFunctionname());
                                    object.put("protocolAddr", protocol.getProtocolAddr());
                                    object.put("dpId", protocol.getDpId());
                                    object.put("span", protocol.getSpan());
                                    object.put("cmdvaule", protocol.getCmdvaule());
                                    object.put("protocoltype", protocol.getProtocoltype());
                                    jsonArray.put(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                bedRoomId = protocol.getBedroomid();
                            }
                            String bedRoomName = null;
                            if (equiptment.getBedroomname() == null) {
                                if (bedRoomId != null) {
                                    bedRoomName = DbUttil.getDedRoomName(context, bedRoomId);
                                }
                            } else {
                                bedRoomName = equiptment.getBedroomname();
                            }
                            DbUttil.addDevice(context, equiptment.getDevicename(),
                                    bedRoomName,
                                    bedRoomId,
                                    equiptment.getDeviceid(),
                                    null, null,
                                    jsonArray.toString(),
                                    equiptment.getDevicestype(),
                                    equiptment.getKeywords(),
                                    null);
                        }
                    }
                }
            }
        }
    }

    private boolean loginSucess(String info) {
        Utils.showLogE(TAG, "loginSucess----------" + info);
        Result<LoginInfo> result = JsonUtil.analyzeLoginInfo(info);
        if (result == null) {
            iinputView.fail(null, context.getString(R.string.login_failed));
            return false;
        }
        String message = result.getMsg();
        LoginInfo loginInfo = result.getT();
        int code = Integer.parseInt(result.getCode());
        if (code == 200) {
            if (null != loginInfo) {
                ArrayList<HouseInfo> houseInfos = loginInfo.getRoom();
                if (null != houseInfos) {
                    if (houseInfos.size() == 1) {
                        //存储到db
                        DbUttil.addUser(context,
                                iinputView.getUserPhone(), iinputView.getUserPwd(),
                                loginInfo.getApikey(),
                                houseInfos.get(0).getRoomID(),
                                houseInfos.get(0).getCommunityID(),
                                houseInfos.get(0).getUnitID(), 0,  "0", "国安府0101室");
                        //TODO 添加假心跳值
                        DbUttil.addPingTime(context, 60, 80);
                        //TODO 第一次登录 分app版本号皆为1.0.0
                        DbUttil.addVersionCode(context, Utils.LSR_AIR, "1.0.0");
                        DbUttil.addVersionCode(context, Utils.LSR_CTRL, "1.0.0");
                        DbUttil.addVersionCode(context, Utils.LSR_ENVI, "1.0.0");
                        DbUttil.addVersionCode(context, Utils.LSR_SECURITY, "1.0.0");
                        DbUttil.addVersionCode(context, Utils.LSR_TALK, "1.0.0");
                        LoginModel.setNettyInfo(DbUttil.getUser(context));
                        loginModel.setBean(DbUttil.getUser(context));
                        analyzeHouseInfo(houseInfos.get(0));
                        getConnectIp();
                        doNettyConnect();
                        getRooms();
                    }
                }
            }
        } else {
            iinputView.fail(null, message);
            return false;
        }
        return true;
    }

    private void getConnectIp() {
        loginModel.getConnectIp();
    }

    private void analyzeHouseInfo(HouseInfo houseInfo) {
        if (null != houseInfo) {
            String communityname = houseInfo.getCommunityname();
            String houseId = houseInfo.getHouseID();
            String buildingId = houseInfo.getBuilname();
            String unitName = houseInfo.getUnitname();
            int i = 0;
            Intent intent = new Intent();
            intent.setAction("com.gatz.smarthomeapp.roominfo");
            if (!TextUtils.isEmpty(houseId)) {
                i= Integer.parseInt(houseId);
                String floor = houseId.substring(0, houseId.length() - 2);
                String family = houseId.substring(houseId.length() - 2);
                intent.putExtra("floor", Integer.valueOf(floor));
                intent.putExtra("family", Integer.valueOf(family));
                Utils.showLogE(TAG, "floor:" + floor + "=======family:" + family);
            }
            if (!TextUtils.isEmpty(buildingId)) {
                intent.putExtra("building", Integer.valueOf(buildingId));
                Utils.showLogE(TAG, "buildingId:" + buildingId);
            }
            if (!TextUtils.isEmpty(unitName)) {
                intent.putExtra("unit", Integer.valueOf(unitName));
                Utils.showLogE(TAG, "unitId:" + unitName);
            }
            context.sendBroadcast(intent);
            String s = communityname + buildingId + "号楼" + unitName + "单元" + i + "室";
            DbUttil.updataUserBuildingName(context, s);
        }
    }

    private void doNettyConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (NettyUtils.socketTag) {
                        Utils.showLogE(TAG, "第一次登录发送的doconnect");
                        appClient.doConnect();
                        break;
                    }
                    if (Utils.isExit) {
                        break;
                    }
                }
            }
        }).start();
    }
}
