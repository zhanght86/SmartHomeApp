package com.gatz.smarthomeapp.service.aidl;

import android.content.Context;
import android.os.RemoteException;

import com.citic.zktd.saber.server.entity.json.enums.GreenCommandAddress;
import com.citic.zktd.saber.server.entity.json.enums.StatusType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxCommandType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxControlType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxStartStopType;
import com.gatz.smarthomeapp.IDeviceControlApi;
import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.model.netty.utils.NettyUtils;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.DeviceCommond;
import com.gatz.smarthomeapp.utils.Utils;

import java.util.List;

/**
 * Created by zhouh on 2017/3/6.
 */
public class DeviceCommandApi extends IDeviceControlApi.Stub {
    private Context context;
    private SendManager sendManager = SendManager.getInstance();

    public DeviceCommandApi(Context context) {
        this.context = context;
    }

    @Override
    public void sendCommand(String type, String deviceId, String value) throws RemoteException {
        Utils.showLogE("DeviceCommandApi", "sendCommand" + deviceId);
        if (type.equals(Utils.LAMP_TYPE)) {
            List<KnxProtocol> knxProtocol = DbUttil.getKnxProtocol(context, deviceId);
            if (knxProtocol != null) {
                KnxProtocol knxProtocol1 = null;
                for (KnxProtocol p : knxProtocol) {
                    if(p.getFunctionname().equals("开关")) {
                        knxProtocol1 = p;
                        break;
                    }
                }
                if (knxProtocol1 == null) {
                    return;
                }
                DeviceCommond.sendKnxCommond(context, knxProtocol1, KnxCommandType.POWER,
                        KnxControlType.WRITE, value, sendManager);
            }
        } else if (type.equals(Utils.WINDOW_TYPE)) {
            List<KnxProtocol> knxProtocol = DbUttil.getKnxProtocol(context, deviceId);
            if (knxProtocol != null) {
                if (value.equals(KnxStartStopType.STOP.getValue())) {
                    KnxProtocol kp1 = null;
                    for(KnxProtocol k1 : knxProtocol) {
                        if (k1.getFunctionname().equals("暂停")) {
                            kp1 = k1;
                            break;
                        }
                    }
                    if (kp1 == null) {
                        return;
                    }
                    DeviceCommond.sendKnxCommond(context, kp1, KnxCommandType.START_STOP,
                            KnxControlType.WRITE, value, sendManager);
                } else {
                    KnxProtocol kp2 = null;
                    for(KnxProtocol k2 : knxProtocol) {
                        if (k2.getFunctionname().equals("开关")) {
                            kp2 = k2;
                            break;
                        }
                    }
                    if (kp2 == null) {
                        return;
                    }
                    DeviceCommond.sendKnxCommond(context, kp2, KnxCommandType.POWER,
                            KnxControlType.WRITE, value, sendManager);
                }
            }
        }
    }

    @Override
    public void sendAirCommand(String address, int value) throws RemoteException {
        GreenCommandAddress address1 = null;
        for (int i = 0; i < GreenCommandAddress.values().length; i++) {
            if (GreenCommandAddress.values()[i].toString().equals(address)) {
                address1 = GreenCommandAddress.values()[i];
            }
        }
        if (address1 != null) {
            DeviceCommond.sendGreenCommand(context, address1,
                    value,
                    sendManager);
        }
    }

    @Override
    public void sendDeviceStatusRequset(String type) throws RemoteException {
        if (type.equals("air")) {
            NettyUtils.getDeviceStateRequset(StatusType.GREEN_CIRCLE);
        }
    }

    @Override
    public void sendAirTimeCommand(String oh, String om, String ch, String cm, String days) throws RemoteException {
        NettyUtils.sendAirTimeCmd(oh, om, ch, cm, days);
    }

    @Override
    public void sendAirTimeOffCmd() throws RemoteException {
        NettyUtils.sendRemoveAirTimeCmd();
    }
}
