package com.gatz.smarthomeapp.activity.setup.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dnake.v700.dmsg;
import com.dnake.v700.dxml;
import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.model.elevator.v700.sound;
import com.gatz.smarthomeapp.model.elevator.v700.utils;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkActivity extends Activity {
    private Button btn_network;
    private RelativeLayout layout_network;
    private EditText e, e2, e3, e4;
    private CheckBox c;
    private Button setup_network_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        init();
    }

    private void init() {
        layout_network = (RelativeLayout) findViewById(R.id.setup_layout_network);
        c = (CheckBox) findViewById(R.id.setup_network_dhcp);
        setup_network_ok = (Button) findViewById(R.id.setup_network_ok);
        e = (EditText) findViewById(R.id.setup_network_ip);
        e2 = (EditText) findViewById(R.id.setup_network_mask);
        e3 = (EditText) findViewById(R.id.setup_network_gateway);
        e4 = (EditText) findViewById(R.id.setup_network_dns);
        c = (CheckBox) findViewById(R.id.setup_network_dhcp);
        RelativeLayout networkLayout = (RelativeLayout) findViewById(R.id.rl_network);
        ImageView backIv = (ImageView) findViewById(R.id.network_backBtn);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkActivity.this.finish();
            }
        });
        networkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        initListener();
        loadNetwork();

    }

    private void initListener() {
        c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                    NetworkActivity.this.sendBroadcast(intent);
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

        EditText e = (EditText) findViewById(R.id.setup_network_ip);
        e.setText(p.getText("/params/ip"));
        e.setInputType(InputType.TYPE_CLASS_TEXT);
        EditText e2 = (EditText) findViewById(R.id.setup_network_mask);
        e2.setText(p.getText("/params/mask"));
        e2.setInputType(InputType.TYPE_CLASS_TEXT);
        EditText e3 = (EditText) findViewById(R.id.setup_network_gateway);
        e3.setText(p.getText("/params/gateway"));
        e3.setInputType(InputType.TYPE_CLASS_TEXT);
        EditText e4 = (EditText) findViewById(R.id.setup_network_dns);
        e4.setText(p.getText("/params/dns"));
        e4.setInputType(InputType.TYPE_CLASS_TEXT);

        TextView tv = (TextView) findViewById(R.id.setup_network_real);
        String ip = utils.getLocalIp();
        if (ip != null)
            tv.setText(ip);
        else
            tv.setText("");

        tv = (TextView) findViewById(R.id.setup_network_mac);
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
