package com.gatz.smarthomeapp.activity.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citic.zktd.saber.server.entity.json.GetStatusResponse;
import com.citic.zktd.saber.server.entity.json.GreenCircleResponse;
import com.citic.zktd.saber.server.entity.json.GreenValue;
import com.citic.zktd.saber.server.entity.json.KnxResponse;
import com.citic.zktd.saber.server.entity.json.announce.GreenCircleQuartzAnnounceMessage;
import com.citic.zktd.saber.server.entity.json.enums.GreenCommandAddress;
import com.citic.zktd.saber.server.entity.json.enums.StatusType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxCommandType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxControlType;
import com.citic.zktd.saber.server.entity.quartz.JobDetailResponse;
import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.activity.elevator.ElevatorActivity;
import com.gatz.smarthomeapp.activity.login.model.LoginModel;
import com.gatz.smarthomeapp.activity.setup.view.SetupActivity;
import com.gatz.smarthomeapp.adapter.LightsAdapter;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.bean.KnxEquiptment;
import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.bean.Result;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.bean.VersionBean;
import com.gatz.smarthomeapp.bean.VersionsInfo;
import com.gatz.smarthomeapp.model.file.UpdataFile;
import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.model.netty.AppClient;
import com.gatz.smarthomeapp.model.netty.global.AppGlobal;
import com.gatz.smarthomeapp.model.netty.global.ConnectResultEvent;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.session.AppStandardSessionManager;
import com.gatz.smarthomeapp.model.netty.session.RequestSessionListener;
import com.gatz.smarthomeapp.model.netty.task.SendHeartTask;
import com.gatz.smarthomeapp.model.netty.utils.MsgUtils;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.DeviceCommond;
import com.gatz.smarthomeapp.utils.HttpUtil;
import com.gatz.smarthomeapp.utils.JsonUtil;
import com.gatz.smarthomeapp.utils.LoginInfo;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;
import com.gatz.smarthomeapp.utils.VoiceUtils;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "HomeActivity-";
    private RelativeLayout ctrlLayout;
    private RelativeLayout airLayout;
    private RelativeLayout safeLayout;
    private RelativeLayout environmentLayout;
    private RelativeLayout settingsLayout;
    private RelativeLayout messageLayout;
    private RelativeLayout secureLayout;
    private RelativeLayout diantiLayout;

    private ImageView environmentIv;
    private TextView environmentTx;
    private ImageView messageIv;
    private TextView messageTx;
    private ImageView connectIv;

    private RelativeLayout recordingLayout;

    @BindView(R.id.settings_imageview)
    ImageView settingsIv;
    @BindView(R.id.settings_text1)
    TextView settingTx;

    //scene mode
    @BindView(R.id.scene_gohome_iv)
    ImageView gohomeBtn;
    @BindView(R.id.scene_leavehome_iv)
    ImageView leavehomeBtn;
    @BindView(R.id.scene_sleep_iv)
    ImageView sleepBtn;
    @BindView(R.id.scene_eat_iv)
    ImageView eatBtn;
    @BindView(R.id.scene_film_iv)
    ImageView filmBtn;
    @BindView(R.id.scene_greet_iv)
    ImageView greetBtn;

    @BindView(R.id.home_light)
    ImageView lightBtn;
    @BindView(R.id.light_layout)
    RelativeLayout lightLayout;
    @BindView(R.id.light_listview)
    ListView lightListView;
    @BindView(R.id.light_all_close_btn)
    Button lightCloseBtn;
    @BindView(R.id.light_ui_close_btn)
    Button lightUiCloseBtn;
    @BindView(R.id.home_connect_text)
    TextView connectText;
    @BindView(R.id.home_name_text)
    TextView homeNameTv;

    private final int CONNECT_SUCCESS = 100;
    private final int SESSION_INVALID = 101;
    private final int GATEWAY_UNEXIST = 102;
    private final int CONNECT_FAILURE = 103;
    private final int DEVICE_UNIQUE_ID_NOT_EMPTY = 104;
    private static final int HIDE_RECORD = 5;
    private static final int SHOW_RECORD = 6;
    private final int REQUEST_SESSION = 107;
    private int currentNettyStatus = CONNECT_SUCCESS;
    private static final String REACTION_ADDR = "0/0/5";
    private static final String ON = "on";


    private static int[] errAddrCode = {64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75};
    private LightsAdapter lightsAdapter;
    private List<KnxEquiptment> onLights = new ArrayList<>();
    private List<String> lightAddresss = new ArrayList<>();

    private final int light_enable = 11;
    private final int light_disable = 12;
    private Map<String, Integer> airStates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.showLogE(TAG, "version:::::1.0.5");
        MyAppliCation.getInstance().addActivity(this);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initData();
        initView();
        RxBus.get().register(this);
        //重启状态查询
        if (NettyUtils.connectTag) {
            NettyUtils.pingRequest();
            //取一次状态 如果已经连接成功 那么直接同步状态
            NettyUtils.getDeviceStateRequset(StatusType.GREEN_CIRCLE);
            NettyUtils.getDeviceStateRequset(StatusType.KNX);
        }
        UpdataFile.initUpdataFile();
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.showLogE(TAG, "----onStrat----");
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if ((null == NettyUtils.sendHeartTask)) {
            long oT = DbUttil.getPingOt(getApplicationContext());
            long vT = DbUttil.getPingVt(getApplicationContext());
            //连接成功发送心跳
            AppGlobal.getInstance().setIntervalTime(vT);
            AppGlobal.getInstance().setTimeOut(oT);
            NettyUtils.sendHeartTask = new SendHeartTask();
            NettyUtils.sendHeartTask.init();
        }
        setModeEnable(true);
        //jiguang
        UserInfoBean bean = DbUttil.getUser(this);
        Intent intent = new Intent();
        intent.setAction("com.gatz.smarthomeapp.jiguang");
        intent.putExtra("username", bean.getUserName());
        intent.putExtra("password", bean.getPsw());
        this.sendBroadcast(intent);
        //
        AppStandardSessionManager appStandardSessionManager = AppStandardSessionManager.getInstance();
        appStandardSessionManager.register(RequestSessionListener.getInstance());
    }

    private void initView() {
        ctrlLayout = (RelativeLayout) findViewById(R.id.home_ctrl_layout);
        airLayout = (RelativeLayout) findViewById(R.id.home_air_layout);
        safeLayout = (RelativeLayout) findViewById(R.id.home_safe_layout);
        environmentLayout = (RelativeLayout) findViewById(R.id.home_environment_layout);
        settingsLayout = (RelativeLayout) findViewById(R.id.home_setting_layout);
        messageLayout = (RelativeLayout) findViewById(R.id.home_message_layout);
        secureLayout = (RelativeLayout) findViewById(R.id.home_secure_layout);
        diantiLayout = (RelativeLayout) findViewById(R.id.home_elevator_layout);
        messageIv = (ImageView) findViewById(R.id.message_imageview);
        messageTx = (TextView) findViewById(R.id.message_text1);
        connectIv = (ImageView) findViewById(R.id.home_connect_iv);

        recordingLayout = (RelativeLayout) findViewById(R.id.rl_home_recording);

        //voiceLayout.setOnClickListener(this);
        //settingsLayout.setOnClickListener(this);
        safeLayout.setOnClickListener(this);
        ctrlLayout.setOnClickListener(this);
        airLayout.setOnClickListener(this);
        secureLayout.setOnClickListener(this);
        environmentLayout.setOnClickListener(this);
        diantiLayout.setOnClickListener(this);
        lightBtn.setOnClickListener(lightClick);
        lightCloseBtn.setOnClickListener(lightClick);
        lightUiCloseBtn.setOnClickListener(lightClick);

        //environmentLayout.setOnTouchListener(this);
        messageLayout.setOnTouchListener(this);
        settingsLayout.setOnTouchListener(this);
    }

    private void initData() {
        Utils.showLogE(TAG, "initData.................");
        MsgUtils.addEventListener(ConnectResultEvent.KNX_RESPONSE, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.FAILURE, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.SESSION_INVALID, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.CONNECT_SESSION_INVALID, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.DEVICE_UNIQUE_ID_NOT_EMPTY, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.PING_SUCCESS, knxListener);
        // connect
        MsgUtils.addEventListener(ConnectResultEvent.GATEWAY_UNEXIST, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.CONNECT_FAILURE, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.CONNECT_SUCCESS, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.REQUEST_SESSION, knxListener);

        //air
        MsgUtils.addEventListener(ConnectResultEvent.GREENCIRCLE_RESPONSE, airListener);
        MsgUtils.addEventListener(ConnectResultEvent.KNX_STATUS_RESPONSE, knxListener);
        MsgUtils.addEventListener(ConnectResultEvent.GREENCIRCLE_TIME_RESPONSE, airTimeListener);

        //voice
        MsgUtils.addEventListener(VoiceUtils.HIDE_RECORD, recordListener);
        MsgUtils.addEventListener(VoiceUtils.SHOW_RECORD, recordListener);
        getAirInfos();
        getLights(true);
        lightAddresss = DbUttil.getLightAddress(getApplicationContext());
        if (onLights.size() == 0) {
            lightBtn.setEnabled(false);
        } else {
            lightBtn.setEnabled(true);
            lightBtn.setBackgroundResource(R.drawable.homepage_light_liang);
        }
        lightsAdapter = new LightsAdapter(getApplicationContext(), onLights);
        lightListView.setAdapter(lightsAdapter);
        homeNameTv.setText(DbUttil.getUserBuildingName(getApplicationContext()));
    }

    private void getAirInfos() {
        airStates = DbUttil.getAirState(getApplicationContext());
    }

    private void getLights(boolean isGet) {
        onLights.clear();
        List<KnxEquiptment> lights = DbUttil.getDevices(getApplicationContext(), "灯");
        if (lights != null) {
            for (KnxEquiptment light : lights) {
                if (light.getState().equals("on")) {
                    onLights.add(light);
                }
            }
            if (isGet) {
                return;
            }
            if (onLights.size() > 0) {
                mUiHandler.sendEmptyMessage(light_enable);
            } else {
                mUiHandler.sendEmptyMessage(light_disable);
            }
        } else {
            mUiHandler.sendEmptyMessage(light_disable);
        }
    }

    private MsgUtils.HandleEventListener airTimeListener = new MsgUtils.HandleEventListener() {
        @Override
        public void onHandle(String eventName, Object... objs) {
            if (objs[0] instanceof JobDetailResponse) {
                Intent intent = new Intent();
                intent.setAction(Utils.ACTION_AIR_TIME);
                intent.putExtra("AirTimeSetCode", "sucess");
                sendBroadcast(intent);
            } else if (objs[0] instanceof GreenCircleQuartzAnnounceMessage) {
                GreenCircleQuartzAnnounceMessage message = (GreenCircleQuartzAnnounceMessage) objs[0];
                Map<String, Object> map = message.getExtInfo();
                if (map != null) {
                    if (map.containsKey(NettyUtils.TIME_TYPE_TAG)) {
                        String cmd = (String) map.get(NettyUtils.TIME_TYPE_TAG);
                        if (cmd.equals(NettyUtils.TIME_AIR_OPEN)) {
                            Intent intent = new Intent();
                            intent.setAction(Utils.ACTION_AIR_TIME);
                            intent.putExtra("AirTimeSetCode", "open");
                            sendBroadcast(intent);
                            DeviceCommond.sendGreenCommand(getApplication(), GreenCommandAddress.MODEL,
                                    10, SendManager.getInstance());
                        } else if (cmd.equals(NettyUtils.TIME_AIR_CLOSE)) {
                            Intent intent = new Intent();
                            intent.setAction(Utils.ACTION_AIR_TIME);
                            intent.putExtra("AirTimeSetCode", "close");
                            sendBroadcast(intent);
                            DeviceCommond.sendGreenCommand(getApplication(), GreenCommandAddress.MODEL,
                                    0, SendManager.getInstance());
                        }
                    }
                }
            }
        }
    };

    private MsgUtils.HandleEventListener airListener = new MsgUtils.HandleEventListener() {
        @Override
        public void onHandle(String eventName, Object... objs) {
            if (eventName.equals(ConnectResultEvent.GREENCIRCLE_RESPONSE)) {
                for (int i = 0; i < objs.length; i++) {
                    if (objs[i] instanceof GreenCircleResponse) {
                        GreenCircleResponse response = (GreenCircleResponse) objs[i];
                        synchronized (this) {
                            updataGreenTable(response);
                        }
                    } else if (objs[i] instanceof GetStatusResponse) {
                        GetStatusResponse gresponse = (GetStatusResponse) objs[i];
                        synchronized (this) {
                            updataGreenStatusTable(gresponse);
                        }
                    }
                }
            }
        }
    };

    private MsgUtils.HandleEventListener knxListener = new MsgUtils.HandleEventListener() {
        @Override
        public void onHandle(String eventName, Object... objs) {
            Utils.showLogE(TAG, "knxListener::::" + eventName);
            if (eventName.equals(ConnectResultEvent.CONNECT_SUCCESS)) {
                mUiHandler.sendEmptyMessage(CONNECT_SUCCESS);
                DbUttil.updataGatewayStatus(getApplicationContext(), 1);
                NettyUtils.getDeviceStateRequset(StatusType.GREEN_CIRCLE);
                NettyUtils.getDeviceStateRequset(StatusType.KNX);
                return;
            } else if (eventName.equals(ConnectResultEvent.PING_SUCCESS)) {
                mUiHandler.sendEmptyMessage(CONNECT_SUCCESS);
                if (currentNettyStatus != CONNECT_SUCCESS) {
                    DbUttil.updataGatewayStatus(getApplicationContext(), 0);
                } else {
                    if (DbUttil.getGatewayStatus(getApplicationContext()) != 1) {
                        DbUttil.updataGatewayStatus(getApplicationContext(), 1);
                        //重连状态成功 在取一次状态
                        NettyUtils.getDeviceStateRequset(StatusType.GREEN_CIRCLE);
                        NettyUtils.getDeviceStateRequset(StatusType.KNX);
                    }
                }
                return;
            } else if ((eventName.equals(ConnectResultEvent.SESSION_INVALID) ||
                    (eventName.equals(ConnectResultEvent.CONNECT_SESSION_INVALID)))) {
                mUiHandler.sendEmptyMessage(SESSION_INVALID);
                UserInfoBean userInfo = DbUttil.getUser(getApplicationContext());
                if (userInfo != null) {
                    HttpUtil.doLogin(Utils.getMacAddr(), userInfo.getUserName(), userInfo.getPsw(),
                            UrlUtils.TERMINAL, httpCallBack, UrlUtils.REQUEST_LOGIN_CODE);
                }
                DbUttil.updataGatewayStatus(getApplicationContext(), 0);
                return;
            } else if (eventName.equals(ConnectResultEvent.DEVICE_UNIQUE_ID_NOT_EMPTY)) {
                mUiHandler.sendEmptyMessage(DEVICE_UNIQUE_ID_NOT_EMPTY);
                DbUttil.updataGatewayStatus(getApplicationContext(), 0);
                return;
            } else if (eventName.equals(ConnectResultEvent.GATEWAY_UNEXIST)) {
                mUiHandler.sendEmptyMessage(GATEWAY_UNEXIST);
                if (DbUttil.getGatewayStatus(getApplication()) != 0) {
                    DbUttil.updataGatewayStatus(getApplicationContext(), 0);
                }
                return;
            } else if (eventName.equals(ConnectResultEvent.CONNECT_FAILURE)) {
                mUiHandler.sendEmptyMessage(CONNECT_FAILURE);
                if (DbUttil.getGatewayStatus(getApplication()) != 0) {
                    DbUttil.updataGatewayStatus(getApplicationContext(), 0);
                }
                return;
            } else if (eventName.equals(ConnectResultEvent.REQUEST_SESSION)) {
                mUiHandler.sendEmptyMessage(REQUEST_SESSION);
                DbUttil.updataGatewayStatus(getApplicationContext(), 0);
                return;
            } else if (eventName.equals(ConnectResultEvent.KNX_STATUS_RESPONSE)) {
                GetStatusResponse statusResponse = (GetStatusResponse) objs[0];
                updataKnxStatusTable(statusResponse);
                return;
            }
            for (int i = 0; i < objs.length; i++) {
                KnxResponse response = (KnxResponse) objs[i];
                if (eventName.equals(ConnectResultEvent.KNX_RESPONSE)) {
                    updataKnxTable(response);
                }
            }
        }
    };

    private MsgUtils.HandleEventListener recordListener = new MsgUtils.HandleEventListener() {
        @Override
        public void onHandle(String eventName, Object... objs) {
            if (VoiceUtils.SHOW_RECORD.equals(eventName)) {
                mUiHandler.sendEmptyMessage(SHOW_RECORD);
                Utils.showLogE(TAG, "显示录音");
            } else if (VoiceUtils.HIDE_RECORD.endsWith(eventName)) {
                mUiHandler.sendEmptyMessage(HIDE_RECORD);
                Utils.showLogE(TAG, "隐藏录音");
            }
        }
    };

    private void updataKnxStatusTable(GetStatusResponse response) {
        Utils.showLogE(TAG, "==主动获取灯的状态==" + response.getStatus());
        String[] a = response.getStatus().split(":");
        for (int i = 0; i < a.length; i++) {
            String address = a[i].substring(0, a[i].indexOf("-"));
            String value = a[i].substring(a[i].indexOf("-") + 1);
            knxDevcieStatus(address, value);
        }
    }

    private void updataKnxTable(KnxResponse response) {
        if (response.getSeq() == -1) {
            String address = response.getKnxAddress();
            String value = response.getValue();
            knxDevcieStatus(address, value);
        }
    }

    private void knxDevcieStatus(String address, String value) {
        DbUttil.updataDeviceStatus(getApplicationContext(), address, value);
        if (lightAddresss.size() == 0) {
            lightAddresss = DbUttil.getLightAddress(getApplicationContext());
        }
        if (lightAddresss.size() == 0) {
            return;
        }
        if (REACTION_ADDR.equals(address) && ON.equals(value)) {
            Utils.showLogE(TAG, "唤醒屏幕");
            Utils.lightUpScreen(HomeActivity.this);
        }
        //是否有灯反馈了
        if (lightAddresss.contains(address)) {
            Utils.showLogE(TAG, "灯反馈了>>" + address + "==" + value);
            getLights(false);
        }
    }

    private void updataGreenTable(GreenCircleResponse response) {
        Utils.showLogE(TAG, "上报获取空调的状态::::::" + response.toString());
        if (response.getCommandMap().size() > 0) {
            Map<GreenCommandAddress, GreenValue> map = response.getCommandMap();
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                GreenCommandAddress address = (GreenCommandAddress) entry.getKey();
                GreenValue value = (GreenValue) entry.getValue();
                if (isAirFault(address)) {
                    if (value.getValue() == 1) {
                        Utils.showLogE(TAG, "空调出故障了::::::" + address.toString());
                        Intent intent = new Intent();
                        intent.setAction(Utils.ACTION_AIR_ERR);
                        intent.putExtra("AirErrCode", address.getValue());
                        sendBroadcast(intent);
                    }
                } else {
                    //判断是否需要更新本地数据库
                    if (!haveAddressInGd(address.toString())) {
                        Utils.showLogE(TAG, "空调add数据库:::" + address.toString() + "=====" + value.getValue());
                        DbUttil.updataAirStateTable(getApplicationContext(), address.toString(), value.getValue());
                        airStates.put(address.toString(), value.getValue());
                    } else {
                        int value_info = airStates.get(address.toString());
                        Utils.showLogE(TAG, "=====" + address.toString() + ":::::内存中的数据::::" + value_info
                                + ":::::上报的数据:::::" + value.getValue());
                        if (value_info != value.getValue()) {
                            Utils.showLogE(TAG, "空调updata数据库:::" + address.toString() + "=====" + value.getValue());
                            DbUttil.updataAirStateTable(getApplicationContext(), address.toString(), value.getValue());
                            airStates.put(address.toString(), value.getValue());
                        } else {
                            if (address.toString().equals("WIND_SPEED") || (address.toString().equals("WET"))) {
                                Utils.showLogE(TAG, "模式选择........");
                                DbUttil.updataAirStateTable(getApplicationContext(), address.toString(), value.getValue());
                                airStates.put(address.toString(), value.getValue());
                            }
                        }
                    }
                }
            }
        } else {
            //空调返回为null 关机显示
            Utils.showLogE(TAG, "空调的状态为空.......");
            airStates.put("MODEL", 0);
            DbUttil.updataAirStateTable(getApplicationContext(),
                    GreenCommandAddress.MODEL.toString(), 0);
        }
    }

    private void updataGreenStatusTable(GetStatusResponse response) {
        Utils.showLogE(TAG, "主动获取空调的状态::::::" + response.getStatus());
        if (response.getCommandMap().size() > 0) {
            Map<GreenCommandAddress, GreenValue> map = response.getCommandMap();
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                GreenCommandAddress address = (GreenCommandAddress) entry.getKey();
                GreenValue value = (GreenValue) entry.getValue();
                if (isAirFault(address)) {
                    if (value.getValue() == 1) {
                        Intent intent = new Intent();
                        intent.setAction(Utils.ACTION_AIR_ERR);
                        intent.putExtra("AirErrCode", address.getValue());
                        sendBroadcast(intent);
                    }
                } else {
                    //判断是否需要更新本地数据库
                    if (!haveAddressInGd(address.toString())) {
                        Utils.showLogE(TAG, "空调add数据库:::" + address.toString() + "=====" + value.getValue());
                        DbUttil.updataAirStateTable(getApplicationContext(), address.toString(), value.getValue());
                        airStates.put(address.toString(), value.getValue());
                    } else {
                        int value_info = airStates.get(address.toString());
                        Utils.showLogE(TAG, "=====" + address.toString() + ":::::内存中的数据::::" + value_info
                                + ":::::上报的数据:::::" + value.getValue());
                        if (value_info != value.getValue()) {
                            Utils.showLogE(TAG, "空调updata数据库:::" + address.toString() + "=====" + value.getValue());
                            DbUttil.updataAirStateTable(getApplicationContext(), address.toString(), value.getValue());
                            airStates.put(address.toString(), value.getValue());
                        } else {
                            if (address.toString().equals("WIND_SPEED") || (address.toString().equals("WET"))) {
                                Utils.showLogE(TAG, "模式选择........");
                                DbUttil.updataAirStateTable(getApplicationContext(), address.toString(), value.getValue());
                                airStates.put(address.toString(), value.getValue());
                            }
                        }
                    }
                }
            }
        } else {
            //空调返回为null 关机显示
            Utils.showLogE(TAG, "空调的状态为空.......");
            airStates.put("MODEL", 0);
            DbUttil.updataAirStateTable(getApplicationContext(),
                    GreenCommandAddress.MODEL.toString(), 0);
        }
    }

    private boolean haveAddressInGd(String address) {
        if (airStates.containsKey(address)) {
            return true;
        }
        return false;
    }


    private boolean isAirFault(GreenCommandAddress address) {
        int code = address.getValue();
        for (int i = 0; i < errAddrCode.length; i++) {
            if (errAddrCode[i] == code) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        clickRespond(id);
    }


    private void clickRespond(int id) {
        Intent intent = null;
        if (id == R.id.home_ctrl_layout) {
            if (currentNettyStatus != CONNECT_SUCCESS) {
                Toast.makeText(getApplicationContext(),
                        "服务器连接失败或者网关不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            NettyUtils.getDeviceStateRequset(StatusType.KNX);
            intent = getPackageManager().getLaunchIntentForPackage("com.gatz.smartctrlapp");
            DbUttil.updataGatewayStatus(getApplicationContext(), 1);
        } else if (id == R.id.home_safe_layout) {
            intent = new Intent();
            ComponentName comp = new ComponentName("com.dnake.talk",
                    "com.dnake.talk.MonitorActivity");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
        } else if (id == R.id.home_air_layout) {
            if (airStates == null) {
                Toast.makeText(getApplicationContext(),
                        "无空调数据", Toast.LENGTH_SHORT).show();
                return;
            }
            if ((currentNettyStatus != CONNECT_SUCCESS)) {
                Toast.makeText(getApplicationContext(),
                        "服务器连接失败或者网关不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            NettyUtils.getDeviceStateRequset(StatusType.GREEN_CIRCLE);
            intent = getPackageManager().getLaunchIntentForPackage("com.gatz.smartairctrlapp");
            DbUttil.updataGatewayStatus(getApplicationContext(), 1);
        } else if (id == R.id.home_secure_layout) {
            intent = new Intent();
            ComponentName comp = new ComponentName("com.dnake.security",
                    "com.dnake.security.SecurityActivity");
            intent.setComponent(comp);
            intent.setAction("android.intent.action.VIEW");
        } else if (id == R.id.home_environment_layout) {
            intent = getPackageManager().getLaunchIntentForPackage("com.gatz.smartenvironmentapp");
        } else if (id == R.id.home_elevator_layout) {
            intent = new Intent(HomeActivity.this, ElevatorActivity.class);
        }
        if (intent != null) {
            this.startActivity(intent);
        }
    }

    public void sceneClick(View v) {
        setSceneMode(v.getId());
    }

    private void setSceneMode(int id) {
        //TODO check gatewat status
        int status = DbUttil.getGatewayStatus(getApplicationContext());
        List<KnxProtocol> protocols = null;
        Handler handler = new Handler();
        switch (id) {
            case R.id.scene_gohome_iv:
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_s);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_n);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_n);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_n);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_n);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_n);
                if (status == 1) {
                    protocols = DbUttil.getSceneProtocols(getApplicationContext(),
                            getResources().getString(R.string.scene_gohome));
                }
                break;
            case R.id.scene_leavehome_iv:
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_n);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_s);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_n);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_n);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_n);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_n);
                if (status == 1) {
                    protocols = DbUttil.getSceneProtocols(getApplicationContext(),
                            getResources().getString(R.string.scene_leavehome));
                }
                break;
            case R.id.scene_sleep_iv:
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_n);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_n);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_s);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_n);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_n);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_n);
                if (status == 1) {
                    protocols = DbUttil.getSceneProtocols(getApplicationContext(),
                            getResources().getString(R.string.scene_sleep));
                }
                break;
            case R.id.scene_film_iv:
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_n);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_n);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_n);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_s);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_n);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_n);
                if (status == 1) {
                    protocols = DbUttil.getSceneProtocols(getApplicationContext(),
                            getResources().getString(R.string.scene_film));
                }
                break;
            case R.id.scene_eat_iv:
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_n);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_n);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_n);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_n);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_s);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_n);
                if (status == 1) {
                    protocols = DbUttil.getSceneProtocols(getApplicationContext(),
                            getResources().getString(R.string.scene_eat));
                }
                break;
            case R.id.scene_greet_iv:
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_n);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_n);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_n);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_n);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_n);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_s);
                if (status == 1) {
                    protocols = DbUttil.getSceneProtocols(getApplicationContext(),
                            getResources().getString(R.string.scene_greet));
                }
                break;
            default:
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_n);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_n);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_n);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_n);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_n);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_n);
                break;
        }
        if (protocols != null) {
            DeviceCommond.sendProfileCommond(getApplicationContext(), protocols, SendManager.getInstance());
        }
        setModeEnable(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gohomeBtn.setBackgroundResource(R.drawable.homepage_scene_gohome_n);
                leavehomeBtn.setBackgroundResource(R.drawable.homepage_scene_leavehome_n);
                sleepBtn.setBackgroundResource(R.drawable.homepage_scene_sleep_n);
                filmBtn.setBackgroundResource(R.drawable.homepage_scene_game_n);
                eatBtn.setBackgroundResource(R.drawable.homepage_scene_eat_n);
                greetBtn.setBackgroundResource(R.drawable.homepage_scene_greet_n);
                setModeEnable(true);
            }
        }, 2 * 1000);
    }

    private void setModeEnable(boolean isEnable) {
        gohomeBtn.setEnabled(isEnable);
        leavehomeBtn.setEnabled(isEnable);
        sleepBtn.setEnabled(isEnable);
        filmBtn.setEnabled(isEnable);
        eatBtn.setEnabled(isEnable);
        greetBtn.setEnabled(isEnable);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
//                    case R.id.home_environment_layout:
//                        environmentLayout.setBackgroundResource(R.drawable.homepage_bg_s);
//                        environmentIv.setBackgroundResource(R.drawable.homepage_environment_icon_s);
//                        environmentTx.setTextColor(getResources().getColor(R.color.home_color_golden_3));
//                        break;
                    case R.id.home_message_layout:
                        messageLayout.setBackgroundResource(R.drawable.homepage_bg_s);
                        messageIv.setBackgroundResource(R.drawable.homepage_message_icon_s);
                        messageTx.setTextColor(getResources().getColor(R.color.home_color_golden_2));
                        break;
                    case R.id.home_setting_layout:
                        settingsLayout.setBackgroundResource(R.drawable.homepage_bg_s);
                        settingsIv.setBackgroundResource(R.drawable.homepage_settings_icon_s);
                        settingTx.setTextColor(getResources().getColor(R.color.home_color_golden_2));
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
//                    case R.id.home_environment_layout:
//                        environmentLayout.setBackgroundResource(R.drawable.homepage_bg_environment);
//                        environmentIv.setBackgroundResource(R.drawable.homepage_environment_icon_n);
//                        environmentTx.setTextColor(getResources().getColor(R.color.home_scene_color));
//                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.gatz.smartenvironmentapp");
//                        HomeActivity.this.startActivity(intent);
//                        break;
                    case R.id.home_message_layout:
                        messageLayout.setBackgroundResource(R.drawable.homepage_bg_message);
                        messageIv.setBackgroundResource(R.drawable.homepage_messgae_icon_n);
                        messageTx.setTextColor(getResources().getColor(R.color.home_scene_color));
                        Intent newsIntent = getPackageManager().getLaunchIntentForPackage("com.gatz.smartctrlphoneapp");
                        startActivity(newsIntent);
                        break;
                    case R.id.home_setting_layout:
                        settingsLayout.setBackgroundResource(R.drawable.homepage_bg_message);
                        settingsIv.setBackgroundResource(R.drawable.homepage_settings_icon_n);
                        settingTx.setTextColor(getResources().getColor(R.color.home_scene_color));
                        Intent settingIntent = new Intent(HomeActivity.this, SetupActivity.class);
                        startActivity(settingIntent);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private Handler mUiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CONNECT_SUCCESS) {
                connectText.setVisibility(View.GONE);
                connectIv.setBackgroundResource(R.drawable.common_point_golden);
                currentNettyStatus = CONNECT_SUCCESS;
            } else if (msg.what == SESSION_INVALID) {
                connectText.setVisibility(View.VISIBLE);
                connectText.setText(String.valueOf(SESSION_INVALID));
                connectIv.setBackgroundResource(R.drawable.common_point_red);
                currentNettyStatus = SESSION_INVALID;
            } else if (msg.what == GATEWAY_UNEXIST) {
                connectText.setVisibility(View.VISIBLE);
                connectText.setText(String.valueOf(GATEWAY_UNEXIST));
                connectIv.setBackgroundResource(R.drawable.common_point_red);
                currentNettyStatus = GATEWAY_UNEXIST;
            } else if (msg.what == CONNECT_FAILURE) {
                connectText.setVisibility(View.VISIBLE);
                connectText.setText(String.valueOf(CONNECT_FAILURE));
                connectIv.setBackgroundResource(R.drawable.common_point_red);
                currentNettyStatus = CONNECT_FAILURE;
            } else if (msg.what == DEVICE_UNIQUE_ID_NOT_EMPTY) {
                connectText.setVisibility(View.VISIBLE);
                connectText.setText(String.valueOf(DEVICE_UNIQUE_ID_NOT_EMPTY));
                connectIv.setBackgroundResource(R.drawable.common_point_red);
                currentNettyStatus = DEVICE_UNIQUE_ID_NOT_EMPTY;
            } else if (msg.what == REQUEST_SESSION) {
                connectText.setVisibility(View.VISIBLE);
                connectText.setText(String.valueOf(REQUEST_SESSION));
                connectIv.setBackgroundResource(R.drawable.common_point_red);
                currentNettyStatus = REQUEST_SESSION;
            } else if (msg.what == light_enable) {
                lightBtn.setBackgroundResource(R.drawable.homepage_light_liang);
                lightBtn.setEnabled(true);
                lightsAdapter.notifyDataSetChanged();
            } else if (msg.what == light_disable) {
                if (lightLayout.getVisibility() != View.GONE) {
                    lightLayout.setVisibility(View.GONE);
                    enableBtn(true);
                }
                lightBtn.setBackgroundResource(R.drawable.homepage_light);
                lightBtn.setEnabled(false);
            } else if (msg.what == HIDE_RECORD) {
                recordingLayout.setVisibility(View.INVISIBLE);
            } else if (msg.what == SHOW_RECORD) {
                recordingLayout.setVisibility(View.VISIBLE);
            }
        }
    };


    private View.OnClickListener lightClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.home_light) {
                if (lightLayout.getVisibility() != View.VISIBLE) {
                    lightLayout.setVisibility(View.VISIBLE);
                    enableBtn(false);
                }
            } else if (id == R.id.light_ui_close_btn) {
                if (lightLayout.getVisibility() != View.GONE) {
                    lightLayout.setVisibility(View.GONE);
                    enableBtn(true);
                }
            } else if (id == R.id.light_all_close_btn) {
                getLights(false);
                if (onLights.size() == 0) {
                    if (lightLayout.getVisibility() != View.GONE) {
                        lightLayout.setVisibility(View.GONE);
                        enableBtn(true);
                    }
                    return;
                }
                for (int i = 0; i < onLights.size(); i++) {
                    KnxProtocol knxProtocol = null;
                    for (KnxProtocol p : onLights.get(i).getProtocols()) {
                        if (p.getFunctionname().equals("开关")) {
                            knxProtocol = p;
                            break;
                        }
                    }
                    if (knxProtocol == null) {
                        return;
                    }
                    DeviceCommond.sendKnxCommond(getApplicationContext(),
                            knxProtocol,
                            KnxCommandType.POWER,
                            KnxControlType.WRITE, "off",
                            SendManager.getInstance());
                }
                if (lightLayout.getVisibility() != View.GONE) {
                    lightLayout.setVisibility(View.GONE);
                    enableBtn(true);
                }
            }
        }
    };

    private void enableBtn(boolean isBoolen) {
        //voiceLayout.setEnabled(isBoolen);
        settingsLayout.setEnabled(isBoolen);
        safeLayout.setEnabled(isBoolen);
        ctrlLayout.setEnabled(isBoolen);
        airLayout.setEnabled(isBoolen);
        environmentLayout.setEnabled(isBoolen);
        messageLayout.setEnabled(isBoolen);
        //elevatorLayout.setEnabled(isBoolen);
    }

    private ObserverCallBack httpCallBack = new ObserverCallBack() {
        @Override
        public void onSuccessHttp(String responseInfo, int resultCode) {
            Utils.showLogE(TAG, responseInfo);
            if (resultCode == UrlUtils.REQUEST_LOGIN_CODE) {
                Utils.showLogE(TAG, "SessionId invalid loginSucess----------" + responseInfo);
                Result<LoginInfo> result = JsonUtil.analyzeLoginInfo(responseInfo);
                if (result == null) {
                    return;
                }
                LoginInfo loginInfo = result.getT();
                int code = Integer.parseInt(result.getCode());
                if (code == 200) {
                    if (null != loginInfo) {
                        String sId = loginInfo.getApikey();
                        DbUttil.updataUserSessionId(getApplicationContext(), sId);
                        LoginModel.setNettyInfo(DbUttil.getUser(getApplicationContext()));
                        AppClient.getInstance().doConnect();
                    }
                }
            }
        }

        @Override
        public void onFailureHttp(IOException e, int resultCode) {
            Utils.showLogE(TAG, e.toString());
            if (resultCode == UrlUtils.REQUEST_LOGIN_CODE) {
                UserInfoBean bean = DbUttil.getUser(getApplicationContext());
                LoginModel.setNettyInfo(bean);
                AppClient.getInstance().doConnect();
            }
        }

        @Override
        public void setData(Object obj) {

        }
    };

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(Utils.GET_VERSION_LSR)})
    public void getResponseInfo(String r) {
        Utils.showLogE(TAG, r);
        int c = r.indexOf("@@@");
        String info = r.substring(c + 3);
        int code = Integer.valueOf(r.substring(0, c));
        Bundle bundle = JsonUtil.parseGetVersion(info);
        int status = Integer.parseInt(bundle.getString(UrlUtils.STATUS));
        if (status != 200) {
            return;
        }
        String version = bundle.getString(UrlUtils.VERSION);
        String apkUrl = bundle.getString(UrlUtils.URL);

        //文件中的版本
        String res = UpdataFile.readUpdataFile();
        List<VersionsInfo> vInfos = new ArrayList<>();
        if (!TextUtils.isEmpty(res)) {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaTypeProfile = objectMapper.getTypeFactory().constructParametricType(List.class, VersionBean.class);
            try {
                List<VersionBean> olds = objectMapper.readValue(res, javaTypeProfile);
                for (int i = 0; i < olds.size(); i++) {
                    VersionsInfo versionsInfo = new VersionsInfo();
                    versionsInfo.setAppType(olds.get(i).getAppType());
                    versionsInfo.setVersion(olds.get(i).getVersion());
                    vInfos.add(versionsInfo);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (code == UrlUtils.LSR_AIR_CODE) {
            String cName = "1.0.0";
            for (VersionsInfo versionsInfo : vInfos) {
                if (versionsInfo.getAppType().equals(Utils.LSR_AIR)) {
                    cName = versionsInfo.getVersion();
                    break;
                }
            }
            Utils.showLogE(TAG, "AirApp服务器版本===" + version + "===文件版本===" + cName);
            if (Utils.checkVersion(cName, version)) {
                new downloadApkThread(apkUrl, Utils.APP_NAME_AIR, Utils.LSR_AIR, version).start();
            }
        } else if (code == UrlUtils.LSR_CTRL_CODE) {
            String cName = "1.0.0";
            for (VersionsInfo versionsInfo : vInfos) {
                if (versionsInfo.getAppType().equals(Utils.LSR_CTRL)) {
                    cName = versionsInfo.getVersion();
                    break;
                }
            }
            Utils.showLogE(TAG, "CtrlApp服务器版本===" + version + "===文件版本===" + cName);
            if (Utils.checkVersion(cName, version)) {
                new downloadApkThread(apkUrl, Utils.APP_NAME_CTRL, Utils.LSR_CTRL, version).start();
            }
        } else if (code == UrlUtils.LSR_ENVI_CODE) {
            String cName = "1.0.0";
            for (VersionsInfo versionsInfo : vInfos) {
                if (versionsInfo.getAppType().equals(Utils.LSR_ENVI)) {
                    cName = versionsInfo.getVersion();
                    break;
                }
            }
            Utils.showLogE(TAG, "EnviApp服务器版本===" + version + "===文件版本===" + cName);
            if (Utils.checkVersion(cName, version)) {
                new downloadApkThread(apkUrl, Utils.APP_NAME_ENVI, Utils.LSR_ENVI, version).start();
            }
        } else if (code == UrlUtils.LSR_MSG_CODE) {
            String cName = "1.0.0";
            for (VersionsInfo versionsInfo : vInfos) {
                if (versionsInfo.getAppType().equals(Utils.LSR_MSG)) {
                    cName = versionsInfo.getVersion();
                    break;
                }
            }
            Utils.showLogE(TAG, "MSG服务器版本===" + version + "===文件版本===" + cName);
            if (Utils.checkVersion(cName, version)) {
                new downloadApkThread(apkUrl, Utils.APP_NAME_MSG, Utils.LSR_MSG, version).start();
            }
        } else if (code == UrlUtils.LSR_SECURITY_CODE) {
            String cName = "1.0.0";
            for (VersionsInfo versionsInfo : vInfos) {
                if (versionsInfo.getAppType().equals(Utils.LSR_SECURITY)) {
                    cName = versionsInfo.getVersion();
                    break;
                }
            }
            Utils.showLogE(TAG, "SECURITY服务器版本===" + version + "===文件版本===" + cName);
            if (Utils.checkVersion(cName, version)) {
                new downloadApkThread(apkUrl, Utils.APP_NAME_SECURITY, Utils.LSR_SECURITY, version).start();
            }
        } else if (code == UrlUtils.LSR_TALK_CODE) {
            String cName = "1.0.0";
            for (VersionsInfo versionsInfo : vInfos) {
                if (versionsInfo.getAppType().equals(Utils.LSR_TALK)) {
                    cName = versionsInfo.getVersion();
                    break;
                }
            }
            Utils.showLogE(TAG, "TALK服务器版本===" + version + "===文件版本===" + cName);
            if (Utils.checkVersion(cName, version)) {
                new downloadApkThread(apkUrl, Utils.APP_NAME_TALK, Utils.LSR_TALK, version).start();
            }
        } else if (code == UrlUtils.LSR_HOME_CODE) {
            String cName = "1.0.0";
            for (VersionsInfo versionsInfo : vInfos) {
                if (versionsInfo.getAppType().equals(Utils.LSR_HOME)) {
                    cName = versionsInfo.getVersion();
                    break;
                }
            }
            Utils.showLogE(TAG, "HOME服务器版本===" + version + "===文件版本===" + cName);
            if (Utils.checkVersion(cName, version)) {
                new downloadApkThread(apkUrl, Utils.APP_NAME_HOME, Utils.LSR_HOME, version).start();
            }
        }
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        private String apkUrl;
        private String apkName;
        private String vName;
        private String vCode;

        protected downloadApkThread(String apkUrl, String apkName, String versionName, String vCode) {
            this.apkUrl = apkUrl;
            this.apkName = apkName;
            this.vName = versionName;
            this.vCode = vCode;
        }

        @Override
        public void run() {
            try {
                // 实例化一个HttpClient
                final DefaultHttpClient httpClient = (DefaultHttpClient) Utils.getNewHttpClient();
                // 设置需要下载的文件
                URI uri = new URI(UrlUtils.UPLOAD_APK_URL + apkUrl);
                Utils.showLogE("downloadApkThread", "uri:::" + uri.toString());
                //HttpPost request = new HttpPost(uri);
                HttpGet request = new HttpGet(uri);
                HttpResponse response = httpClient.execute(request);
                int code = response.getStatusLine()
                        .getStatusCode();
                Utils.showLogE("downloadApkThread", "code:::" + code);
                if (HttpStatus.SC_OK == code) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String mSavePath = "/dnake/cfg" + "/" + apkName + ".apk";
                        File storeFile = new File(mSavePath);
                        if (storeFile.exists()) {
                            Utils.showLogE("download", "文件" + apkName + "已经存在 删除.....");
                            storeFile.delete();
                        }
                        long length = entity.getContentLength();
                        Utils.showLogE("download", "download to" + mSavePath + "---" + length);
                        FileOutputStream output = new FileOutputStream(storeFile);
                        // 得到网络资源并写入文件
                        InputStream input = entity.getContent();
                        byte b[] = new byte[1024];
                        int j = 0;
                        while ((j = input.read(b)) != -1) {
                            output.write(b, 0, j);
                        }
                        output.flush();
                        output.close();
                        //更新数据库中相应的app版本号;
                        Utils.showLogE(TAG, "下载完毕:::::");
                        UpdataFile.updataVersion(vName, vCode);
                        Utils.updataApp(mSavePath, apkName);
                    } else {
                        Utils.showLogE("download", "null");
                    }
                    if (entity != null) {
                        entity.consumeContent();
                    }
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}
