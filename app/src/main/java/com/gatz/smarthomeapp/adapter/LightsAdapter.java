package com.gatz.smarthomeapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.citic.zktd.saber.server.entity.protocol.enums.KnxCommandType;
import com.citic.zktd.saber.server.entity.protocol.enums.KnxControlType;
import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.bean.KnxEquiptment;
import com.gatz.smarthomeapp.bean.KnxProtocol;
import com.gatz.smarthomeapp.model.netty.manager.SendManager;
import com.gatz.smarthomeapp.utils.DeviceCommond;
import com.gatz.smarthomeapp.utils.Utils;

import java.util.List;

/**
 * Created by zhouh on 2017/5/18.
 */
public class LightsAdapter extends BaseAdapter{

    private Context context;
    private List<KnxEquiptment> list;
    private LayoutInflater mInflater;

    public LightsAdapter(Context context, List<KnxEquiptment> list){
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(vh == null){
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_lights_item, null);
            vh.roomName = (TextView) convertView.findViewById(R.id.light_room_name);
            vh.lightIv = (ImageView) convertView.findViewById(R.id.light_bg);
            vh.lightName = (TextView) convertView.findViewById(R.id.light_name);
            vh.closeBtn = (Button) convertView.findViewById(R.id.light_close_btn);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        final KnxEquiptment equiptment = list.get(position);
        vh.roomName.setText(equiptment.getBedroomname());
        updateImageView(vh.lightIv, equiptment.getDevicename());
        vh.lightName.setText(equiptment.getDevicename());
        vh.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KnxProtocol p = null;
                for(KnxProtocol knxProtocol : equiptment.getProtocols()) {
                    if(knxProtocol.getFunctionname().equals("开关")) {
                        p = knxProtocol;
                        break;
                    }
                }
                if (p == null) {
                    return;
                }
                DeviceCommond.sendKnxCommond(context,
                        p,
                        KnxCommandType.POWER,
                        KnxControlType.WRITE, "off",
                        SendManager.getInstance());
            }
        });
        return convertView;
    }

    private void updateImageView(ImageView iv, String deviceName) {
        if (context.getString(R.string.light_btn1_type).equals(deviceName)) {
            iv.setBackgroundResource(R.drawable.mast_light);
        } else if (deviceName.equals(context.getString(R.string.light_btn2_type))) {
            iv.setBackgroundResource(R.drawable.belt_light);
        } else if (deviceName.equals(context.getString(R.string.light_btn3_type))) {
            iv.setBackgroundResource(R.drawable.down_light);
        } else {
            iv.setBackgroundResource(R.drawable.mast_light);
        }
    }

    class ViewHolder {
        TextView roomName;
        ImageView lightIv;
        TextView lightName;
        Button closeBtn;
    }
}
