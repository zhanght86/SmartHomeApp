package com.gatz.smarthomeapp.activity.setup.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.base.BaseFragment;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouh on 2017/4/17.
 */
public class UpgradeFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.upgrade_btn)
    Button updataBtn;
    @BindView(R.id.upgrade_tv)
    TextView upgradeTv;
    private ProgressDialog pdg;

    private String apkUrl;
    private boolean is_click = false;
    private int progress;
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private PdgDissListener listener;
    private String mSavePath;

    private static final String HOME_APKNAME = "SmartHomeApp";

    @Override
    public void onAttach(Activity activity) {
        listener = (PdgDissListener) activity;
        super.onAttach(activity);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_version_layout;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        updataBtn.setOnClickListener(this);
        initData();
    }

    public static UpgradeFragment newInstance(Bundle args) {
        UpgradeFragment fragment = new UpgradeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initData() {
        Bundle bundle = getArguments();
        String version = bundle.getString(UrlUtils.VERSION);
        if (!Utils.isEmpty(version)) {
            apkUrl = bundle.getString(UrlUtils.URL);
            upgradeTv.setText(String.format(getString(R.string.setup_updata_text2), version));
            updataBtn.setEnabled(true);
        } else {
            upgradeTv.setText(bundle.getString("version_msg"));
            updataBtn.setEnabled(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSuccessHttp(String responseInfo, int resultCode) {
        super.onSuccessHttp(responseInfo, resultCode);
    }

    @Override
    public void onFailureHttp(IOException e, int resultCode) {
        super.onFailureHttp(e, resultCode);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.upgrade_btn) {
            showDownloadDialog();
            new downloadApkThread().start();
        }
    }

    /**
     * 显示软件下载对话框
     */
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
                        String sdpath = "/dnake/cfg";
                        mSavePath = sdpath + "/SmartHomeApp.apk";
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
                                is_click = false;
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
                    //RxBus.get().post("dismiss", true);
                }
                listener.status(true);
                Utils.updataApp(mSavePath, HOME_APKNAME);
            }
        }
    };

    public interface PdgDissListener {
        void status(boolean isDiss);
    }
}
