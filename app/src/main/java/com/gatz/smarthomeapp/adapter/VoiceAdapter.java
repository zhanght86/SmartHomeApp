package com.gatz.smarthomeapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.bean.VoiceMessage;

import java.util.List;

/**
 * Created by Debby on 2016/11/10.
 */
public class VoiceAdapter extends BaseAdapter {
    private Context context;
    private List<VoiceMessage> messageList;
    public VoiceAdapter(Context context, List<VoiceMessage> messages){
        this.context = context;
        this.messageList = messages;
    }

    public void add(VoiceMessage msg){
        messageList.add(msg);
        notifyDataSetChanged();
    }
    public void addLast(int position, ListView listView){
        int firstVisiablePosi = listView.getFirstVisiblePosition();
        int lastVisiablePosi = listView.getLastVisiblePosition();
        if(position>=firstVisiablePosi && position<=lastVisiablePosi){
            View view = listView.getChildAt(position-lastVisiablePosi);
            ViewHolder holder = (ViewHolder) view.getTag();

        }
    }

    @Override
    public int getCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return messageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        VoiceMessage message = (VoiceMessage) getItem(position);
        if(message == null)
            return  -1;
        //接收的消息
        if(message.getType()==VoiceMessage.IMsgViewType.IMVT_COM_MSG){
            return VoiceMessage.IMsgViewType.IMVT_COM_MSG;
        }else if(message.getType()==VoiceMessage.IMsgViewType.IMVT_TO_MSG){//发送的消息
            return VoiceMessage.IMsgViewType.IMVT_TO_MSG;
        }
        return -1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        VoiceMessage message = (VoiceMessage) getItem(i);
        int messageType = message.getiMsgViewType();
        ViewHolder viewHolder = null;
        int type = message.getType();
        View converView = null;
        if(null==converView){
            if(messageType==VoiceMessage.IMsgViewType.IMVT_COM_MSG){
                if(type ==VoiceMessage.Type.TEXT){
                    converView = View.inflate(context, R.layout.layout_voice_left_item,null);
                }else if(type ==VoiceMessage.Type.URL){
                    converView = View.inflate(context, R.layout.layout_voice_left_view_item,null);
                }
            }else if(messageType==VoiceMessage.IMsgViewType.IMVT_TO_MSG){
                converView = View.inflate(context,R.layout.layout_voice_right_item,null);
            }
            viewHolder = new ViewHolder();
           // viewHolder.time = (TextView) converView.findViewById(R.id.tv_voice_time);
            viewHolder.message = (TextView) converView.findViewById(R.id.voice_textContent);
            viewHolder.webView = (WebView) converView.findViewById(R.id.voice_webview);
           // viewHolder.time = (TextView) converView.findViewById(R.id.tv_voice_time);
            converView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) converView.getTag();
        }
        String textContext = message.getMessage();
      //  Date date = message.getDate();
//        if(null!=date){
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String time = sdf.format(date);
//            if(null!=viewHolder.time){
//                viewHolder.time.setText(time);
//            }
//        }
        if(type == VoiceMessage.Type.TEXT){
            viewHolder.message.setText(textContext);
        }else if(type == VoiceMessage.Type.URL){
            viewHolder.message.setText(textContext);
            WebSettings webSettings =  viewHolder.webView .getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            viewHolder.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);// 取消滚动条
            viewHolder.webView.setHorizontalScrollbarOverlay(false);
            viewHolder.webView.setVerticalScrollbarOverlay(false);
            viewHolder.webView.requestFocus();
            viewHolder.webView.setBackgroundColor(0);
            viewHolder.webView.loadUrl(message.getUrl());
            final ViewHolder finalViewHolder = viewHolder;
            finalViewHolder.webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    finalViewHolder.webView.loadUrl(url);
                    return true;
                }
            });
            viewHolder.webView.addJavascriptInterface(new InJavaScriptLocalObj(), "myObj");
        }
        return converView;
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface //sdk17版本以上加上注解
        public void fun1FromAndroid(String data) {
            Log.d("data", data);
        }
    }

    private class ViewHolder{
        private TextView time;
        private TextView message;
        private WebView webView;
    }


}
