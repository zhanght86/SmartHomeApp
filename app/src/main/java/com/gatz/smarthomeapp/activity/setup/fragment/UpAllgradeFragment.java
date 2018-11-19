package com.gatz.smarthomeapp.activity.setup.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.adapter.VersionAdapter;
import com.gatz.smarthomeapp.base.BaseFragment;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.bean.VersionBean;
import com.gatz.smarthomeapp.bean.VersionsInfo;
import com.gatz.smarthomeapp.model.file.UpdataFile;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.HttpUtil;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;

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
import java.util.List;

/**
 * Created by zhouh on 2017/12/26.
 */
public class UpAllgradeFragment extends BaseFragment{
    private ListView versionListView;
    private List<VersionsInfo> infos = new ArrayList<>();
    private VersionAdapter versionAdapter;
    private ProgressDialog pdg;
    private String apkUrl;
    private boolean is_click = false;
    private int progress;
    private String lsr;
    private String n;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private UpgradeFragment.PdgDissListener listener;
    private String mSavePath;
    private String apkName;

    private static final String TAG = "UpAllgradeFragment---";
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_updata_layout;
    }

    @Override
    public void onAttach(Activity activity) {
        listener = (UpgradeFragment.PdgDissListener) activity;
        super.onAttach(activity);
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        init();
    }

    public static UpAllgradeFragment newInstance(Bundle args) {
        UpAllgradeFragment fragment = new UpAllgradeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void init() {
        versionListView  = (ListView) rootView.findViewById(R.id.version_listview);
        versionListView.setEnabled(false);
        UserInfoBean bean = DbUttil.getUser(MyAppliCation.getInstance());
        String res = UpdataFile.readUpdataFile();
        if (!TextUtils.isEmpty(res)) {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaTypeProfile = objectMapper.getTypeFactory().constructParametricType(List.class, VersionBean.class);
            try {
                List<VersionBean> olds = objectMapper.readValue(res, javaTypeProfile);
                for(int i=0; i<olds.size(); i++) {
                    VersionsInfo versionsInfo = new VersionsInfo();
                    versionsInfo.setAppType(olds.get(i).getAppType());
                    versionsInfo.setVersion(olds.get(i).getVersion());
                    infos.add(versionsInfo);
                    versionAdapter = new VersionAdapter(infos, getActivity());
                    versionAdapter.setVersionOnClick(clickListener);
                    versionListView.setAdapter(versionAdapter);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((bean != null) && (infos.size() != 0)) {
            HttpUtil.getVersionPad(bean.getCommunityId(),
                    "user", "lsr", bean.getSessionId(), this,  UrlUtils.VERSION_PAD_CODE);
        }
    }

    @Override
    public void onSuccessHttp(String responseInfo, int resultCode) {
        super.onSuccessHttp(responseInfo, resultCode);
        if (resultCode == UrlUtils.VERSION_PAD_CODE) {
            Utils.showLogE(TAG, responseInfo);
            List<VersionBean> news = UpdataFile.JsonParserFile(responseInfo);
            if (news != null) {
                for(VersionBean n : news) {
                    String app = n.getAppType();
                    for (int i = 0; i < infos.size(); i++) {
                        if(infos.get(i).getAppType().equals(app)){
                            //判断是否需要升级
                            if (Utils.checkVersion(infos.get(i).getVersion(), n.getVersion())) {
                                infos.get(i).setNewVersion(n.getVersion());
                                infos.get(i).setUrl(n.getUrl());
                            }
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public void onFailureHttp(IOException e, int resultCode) {
        super.onFailureHttp(e, resultCode);
        if (resultCode == UrlUtils.VERSION_PAD_CODE) {

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            versionAdapter.notify(infos);
        }
    };

    private VersionAdapter.ClickListener clickListener = new VersionAdapter.ClickListener() {
        @Override
        public void click(int pos) {
            VersionsInfo info = infos.get(pos);
            Log.e(TAG, "升级:::::" + info.getNewVersion() + "====" +
                    info.getAppType() + "=====" + info.getUrl());
            apkUrl = info.getUrl();
            is_click = false;
            progress = 0;
            apkName = getApkName(info.getAppType());
            if ((!TextUtils.isEmpty(apkUrl)) && (!(TextUtils.isEmpty(apkName)))) {
                lsr = info.getAppType();
                n = info.getNewVersion();
                showDownloadDialog();
                new downloadApkThread().start();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "升级失败!", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private String getApkName(String name) {
        String apkname = "";
        if (name.equals(Utils.LSR_HOME)) {
            apkname = Utils.APP_NAME_HOME;
        } else if (name.equals(Utils.LSR_CTRL)) {
            apkname = Utils.APP_NAME_CTRL;
        } else if (name.equals(Utils.LSR_AIR)) {
            apkname = Utils.APP_NAME_AIR;
        } else if (name.equals(Utils.LSR_ENVI)) {
            apkname = Utils.APP_NAME_ENVI;
        } else if (name.equals(Utils.LSR_MSG)) {
            apkname = Utils.APP_NAME_MSG;
        } else if (name.equals(Utils.LSR_TALK)) {
            apkname = Utils.APP_NAME_TALK;
        } else if (name.equals(Utils.LSR_SECURITY)) {
            apkname = Utils.APP_NAME_SECURITY;
        }
        return apkname;
    }

    private void showDownloadDialog() {
        pdg = new ProgressDialog(getActivity());
        pdg.setTitle("版本更新");
        pdg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdg.setProgressNumberFormat("%1d kb/%2d kb");
        pdg.setCanceledOnTouchOutside(false);
        pdg.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                is_click = true;
            }
        });
        pdg.show();
    }


    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
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
                    // 请求成功
                    // 取得请求内容
                    HttpEntity entity = response.getEntity();
                    // 显示内容
                    if (entity != null) {
                        mSavePath = "/dnake/cfg" + "/" + apkName + ".apk";
                        File storeFile = new File(mSavePath);
                        if (storeFile.exists()) {
                            Utils.showLogE("download", "文件已经存在 删除.....");
                            storeFile.delete();
                        }
                        long length = entity.getContentLength();
                        Utils.showLogE("download", "download to" + mSavePath + "---" + length);
                        FileOutputStream output = new FileOutputStream(storeFile);
                        // 得到网络资源并写入文件
                        InputStream input = entity.getContent();
                        byte b[] = new byte[1024];
                        int j = 0;
                        int count = 0;
                        pdg.setMax((int) length / 1024);
                        while ((j = input.read(b)) != -1) {
                            output.write(b, 0, j);
                            count += j;
                            // 计算进度条位置
                            progress = (int) (((float) count / j));
                            // 更新进度
                            mHandler.sendEmptyMessage(DOWNLOAD);
                            if (is_click) {
                                if (storeFile.exists()) {
                                    storeFile.delete();
                                }
                                break;
                            }
                        }
                        mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                        output.flush();
                        output.close();
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

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == DOWNLOAD) {
                pdg.setProgress(progress);
            } else if (what == DOWNLOAD_FINISH) {
                if (pdg.isShowing()) {
                    pdg.dismiss();
                }
                listener.status(true);
                if (!is_click) {
                    //更新版本号
                    UpdataFile.updataVersion(lsr, n);
                    Utils.updataApp(mSavePath, apkName);
                }
                is_click = false;
            }
        }
    };
}
