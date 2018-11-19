package com.gatz.smarthomeapp.activity.setup.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.base.BaseFragment;
import com.gatz.smarthomeapp.model.elevator.v700.sound;
import com.gatz.smarthomeapp.model.elevator.v700.utils;


import java.net.InetAddress;
import java.net.UnknownHostException;



public class NetWorkFragment extends BaseFragment {
    private Button btn_network;
    private RelativeLayout layout_network;
    private CheckBox c;
    private Button setup_network_ok;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_net_work;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        init();
    }

    public static NetWorkFragment newInstance(Bundle args) {
        NetWorkFragment fragment = new NetWorkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void init() {
        layout_network = (RelativeLayout) rootView.findViewById(R.id.setup_layout_network);
        c = (CheckBox) rootView.findViewById(R.id.setup_network_dhcp);
        setup_network_ok = (Button) rootView.findViewById(R.id.setup_network_ok);
        initListener();
        loadNetwork();

    }

    private void initListener() {
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText e = (EditText) rootView.findViewById(R.id.setup_network_ip);
                EditText e2 = (EditText) rootView.findViewById(R.id.setup_network_mask);
                EditText e3 = (EditText) rootView.findViewById(R.id.setup_network_gateway);
                EditText e4 = (EditText) rootView.findViewById(R.id.setup_network_dns);
                CheckBox c = (CheckBox) rootView.findViewById(R.id.setup_network_dhcp);
                if (c.isChecked()) {
                    e.setEnabled(false);
                    e2.setEnabled(false);
                    e3.setEnabled(false);
                    e4.setEnabled(false);
                } else {
                    e.setEnabled(true);
                    e2.setEnabled(true);
                    e3.setEnabled(true);
                    e4.setEnabled(true);
                }
            }
        });
        setup_network_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText e = (EditText) rootView.findViewById(R.id.setup_network_ip);
                EditText e2 = (EditText) rootView.findViewById(R.id.setup_network_mask);
                EditText e3 = (EditText) rootView.findViewById(R.id.setup_network_gateway);
                EditText e4 = (EditText) rootView.findViewById(R.id.setup_network_dns);
                CheckBox c = (CheckBox) rootView.findViewById(R.id.setup_network_dhcp);

                String ip = e.getText().toString();
                String mask = e2.getText().toString();
                String gateway = e3.getText().toString();
                String dns = e4.getText().toString();

                if (ipValidate(ip) && ipValidate(mask) && ipValidate(gateway) && ipMatch(ip, mask, gateway)) {
                    Intent intent = new Intent();
                    intent.setAction("com.gatz.broadcast.network");
                    intent.putExtra("ip", ip);
                    intent.putExtra("mask", mask);
                    intent.putExtra("gateway", gateway);
                    intent.putExtra("dns", dns);
                    intent.putExtra("dhcp", c.isChecked());
                    getActivity().sendBroadcast(intent);
                } else
                    sound.play(sound.modify_failed, false);
            }
        });
    }

    private void loadNetwork() {
        dmsg req = new dmsg();
        dxml p = new dxml();
        int dhcp;

        req.to("/settings/lan/query", null);
        p.parse(req.mBody);
        dhcp = p.getInt("/params/dhcp", 0);

        EditText e = (EditText) rootView.findViewById(R.id.setup_network_ip);
        e.setText(p.getText("/params/ip"));
        e.setInputType(InputType.TYPE_CLASS_TEXT);
        EditText e2 = (EditText) rootView.findViewById(R.id.setup_network_mask);
        e2.setText(p.getText("/params/mask"));
        e2.setInputType(InputType.TYPE_CLASS_TEXT);
        EditText e3 = (EditText) rootView.findViewById(R.id.setup_network_gateway);
        e3.setText(p.getText("/params/gateway"));
        e3.setInputType(InputType.TYPE_CLASS_TEXT);
        EditText e4 = (EditText) rootView.findViewById(R.id.setup_network_dns);
        e4.setText(p.getText("/params/dns"));
        e4.setInputType(InputType.TYPE_CLASS_TEXT);

        TextView tv = (TextView) rootView.findViewById(R.id.setup_network_real);
        String ip = utils.getLocalIp();
        if (ip != null)
            tv.setText(ip);
        else
            tv.setText("");

        tv = (TextView) rootView.findViewById(R.id.setup_network_mac);
        String mac = utils.getLocalMac();
        if (mac != null)
            tv.setText(mac);
        else
            tv.setText("");

        if (dhcp == 0) {
            c.setChecked(false);
            e.setEnabled(true);
            e2.setEnabled(true);
            e3.setEnabled(true);
            e4.setEnabled(true);
        } else {
            c.setChecked(true);
            e.setEnabled(false);
            e2.setEnabled(false);
            e3.setEnabled(false);
            e4.setEnabled(false);
        }
        //btn_network.setBackgroundDrawable(getResources().getDrawable(R.drawable.setup_btn_network2));
        // layout_network.setVisibility(RelativeLayout.VISIBLE);
    }

    /***
     * 判断IP是否有效
     *
     * @param addr
     * @return
     */
    private Boolean ipValidate(String addr) {
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        if (!addr.matches(REGX_IP))
            return false;
        return true;
    }

    /**
     * 判断IP地址是否匹配
     *
     * @param ip
     * @param mask
     * @param gateway
     * @return
     */
    private Boolean ipMatch(String ip, String mask, String gateway) {
        try {
            byte[] _ip = InetAddress.getByName(ip).getAddress();
            byte[] _mask = InetAddress.getByName(mask).getAddress();
            byte[] _gateway = InetAddress.getByName(gateway).getAddress();
            for (int i = 0; i < 4; i++) {
                _ip[i] &= _mask[i];
                _gateway[i] &= _mask[i];
                if (_ip[i] != _gateway[i])
                    return false;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return true;
    }
}
