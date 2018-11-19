package com.gatz.smarthomeapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.bean.VersionsInfo;
import com.gatz.smarthomeapp.utils.Utils;

import java.util.List;

/**
 * Created by zhouh on 2017/12/26.
 */
public class VersionAdapter extends BaseAdapter{

    private Context context;
    private List<VersionsInfo> list;
    private LayoutInflater mInflater;
    private ClickListener clickListener;

    public VersionAdapter(List<VersionsInfo> mlist, Context ctx) {
        list = mlist;
        context = ctx;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(vh == null){
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_version_item, null);
            vh.appName = (TextView) convertView.findViewById(R.id.app_name);
            vh.appDes = (TextView) convertView.findViewById(R.id.app_des);
            vh.btn = (Button) convertView.findViewById(R.id.app_btn);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        VersionsInfo info = list.get(position);
        String lsr = info.getAppType();
        String o = info.getVersion();
        String url = info.getUrl();
        String n = info.getNewVersion();
        if ((TextUtils.isEmpty(url)) || (TextUtils.isEmpty(n))) {
            vh.appDes.setText("当前版本" + o + "为最新版本!");
            vh.btn.setVisibility(View.GONE);
        } else {
            vh.appDes.setText("当前版本为" + o + " 升级版本为" + n);
            vh.btn.setVisibility(View.VISIBLE);
        }
        if (lsr.equals(Utils.LSR_HOME)) {
            vh.appName.setText("首页模块");
        } else if (lsr.equals(Utils.LSR_CTRL)) {
            vh.appName.setText("智能模块");
        } else if (lsr.equals(Utils.LSR_AIR)) {
            vh.appName.setText("空调模块");
        } else if (lsr.equals(Utils.LSR_ENVI)) {
            vh.appName.setText("环境模块");
        } else if (lsr.equals(Utils.LSR_MSG)) {
            vh.appName.setText("消息模块");
        } else if (lsr.equals(Utils.LSR_TALK)) {
            vh.appName.setText("门禁模块");
        } else if (lsr.equals(Utils.LSR_SECURITY)) {
            vh.appName.setText("安防模块");
        }
        vh.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.click(position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView appName;
        TextView appDes;
        Button btn;
    }

    public void notify(List<VersionsInfo> infoList) {
        this.list = infoList;
        notifyDataSetChanged();
    }

    public void setVersionOnClick(ClickListener c) {
        clickListener = c;
    }

    public interface ClickListener {
        void click(int pos);
    }
}
