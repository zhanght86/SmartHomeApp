package com.gatz.smarthomeapp.activity.setup.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.activity.login.view.LoginActivity;
import com.gatz.smarthomeapp.activity.setup.fragment.HomeFragment;
import com.gatz.smarthomeapp.activity.setup.fragment.IdearFragment;
import com.gatz.smarthomeapp.activity.setup.fragment.NetWorkFragment;
import com.gatz.smarthomeapp.activity.setup.fragment.UpAllgradeFragment;
import com.gatz.smarthomeapp.activity.setup.fragment.UpgradeFragment;
import com.gatz.smarthomeapp.base.MyAppliCation;
import com.gatz.smarthomeapp.bean.UserInfoBean;
import com.gatz.smarthomeapp.model.http.ObserverCallBack;
import com.gatz.smarthomeapp.utils.DbUttil;
import com.gatz.smarthomeapp.utils.HttpUtil;
import com.gatz.smarthomeapp.utils.JsonUtil;
import com.gatz.smarthomeapp.utils.ToastUtil;
import com.gatz.smarthomeapp.utils.UrlUtils;
import com.gatz.smarthomeapp.utils.Utils;
import com.gatz.smarthomeapp.view.SetDialog;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetupActivity extends FragmentActivity implements View.OnTouchListener,
        UpgradeFragment.PdgDissListener {

    @BindView(R.id.setup_backBtn)
    ImageView backBtn;
    @BindView(R.id.setup_close_btn)
    Button closeBtn;

    @BindView(R.id.setup_version_iv)
    ImageView versionIv;
    @BindView(R.id.setup_version_tv)
    TextView versionTv;
    @BindView(R.id.setup_version_layout)
    RelativeLayout versionLayout;
    @BindView(R.id.setup_intent_iv)
    ImageView intentIv;
    @BindView(R.id.setup_intent_tv)
    TextView intentTv;
    @BindView(R.id.setup_intent_layout)
    RelativeLayout intentLayout;

    private FragmentManager mFragmentManager;
    private IdearFragment idearFragment;
    private HomeFragment homeFragment;
    private NetWorkFragment networFragment;
    private UpAllgradeFragment upAllgradeFragment;

    private SetDialog setDialog;

    private Bundle updataBundle = new Bundle();
    private int currentPos = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAppliCation.getInstance().addActivity(this);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_setup);
        initView();
        initDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        ButterKnife.bind(this);
        setDialog = new SetDialog(this);
        versionLayout.setOnTouchListener(this);
        intentLayout.setOnTouchListener(this);
        setDialog.setDialogBtnClick(setDialogClick);
    }

    private void initDate() {
        mFragmentManager = getSupportFragmentManager();
        //showHome();
        showIdear();
        if (Utils.roomIdNumber.equals("")) {
            UserInfoBean bean = DbUttil.getUser(getApplicationContext());
            if (bean != null) {
                Utils.roomIdNumber = bean.getRoomId().substring(3);
                if (Utils.roomIdNumber.length() == 13) {
                    Utils.roomIdNumber = Utils.roomIdNumber.substring(0, 3)
                            + Utils.roomIdNumber.substring(10);
                } else {
                    Utils.roomIdNumber = "123456";
                }
            }
        }
    }

    public void setupOnClick(View v) {
        int id = v.getId();
        if (id == R.id.setup_backBtn) {
            this.finish();
        } else if (id == R.id.setup_idea_layout) {
            showIdear();
        } else if (id == R.id.setup_user_layout) {
            setDialog.show();
        }
//        else if (id == R.id.setup_close_btn) {
//            showHome();
//        }
    }

    private View.OnClickListener setDialogClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.set_pj_cancel_btn) {

            } else if (id == R.id.set_pj_sure_btn) {
                String pwd = setDialog.getPwd();
                if (pwd.equals(Utils.roomIdNumber)) {
                    if(DbUttil.deleteDbTable(DbUttil.DB_DIR)){
                        DbUttil.deleteDbTable(DbUttil.DB_DIR_JOURNAL);
                        showWaitingDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                waitingDialog.dismiss();
                                Intent intent = new Intent(MyAppliCation.getInstance().getApplicationContext(), LoginActivity.class);
                                PendingIntent restartIntent = PendingIntent.getActivity(
                                        MyAppliCation.getInstance().getApplicationContext(), 0, intent,
                                        PendingIntent.FLAG_ONE_SHOT);
                                //退出程序重启
                                AlarmManager mgr = (AlarmManager) MyAppliCation.getInstance().
                                        getSystemService(Context.ALARM_SERVICE);
                                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1500,
                                        restartIntent); // 1秒钟后重启应用
                                MyAppliCation.getInstance().exit(true);
                                MyAppliCation.getInstance().finishProcess();
                            }
                        }, 5000);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
            setDialog.disMiss();
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private ProgressDialog waitingDialog;
    private void showWaitingDialog() {
        waitingDialog=
                new ProgressDialog(this);
        waitingDialog.setTitle("提示");
        waitingDialog.setMessage("删除数据,应用重启中....");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.setup_version_layout:
                        versionLayout.setBackgroundResource(R.drawable.homepage_bg_s);
                        versionIv.setBackgroundResource(R.drawable.set_up_icon2_btn_p);
                        versionTv.setTextColor(getResources().getColor(R.color.home_color_golden_3));
                        break;
                    case R.id.setup_intent_layout:
                        intentLayout.setBackgroundResource(R.drawable.homepage_bg_s);
                        intentIv.setBackgroundResource(R.drawable.set_up_icon3_btn_p);
                        intentTv.setTextColor(getResources().getColor(R.color.home_color_golden_3));
                        break;

                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.setup_version_layout:
                        versionLayout.setBackgroundResource(R.drawable.homepage_bg_environment);
                        versionIv.setBackgroundResource(R.drawable.set_up_icon2_btn_n);
                        versionTv.setTextColor(getResources().getColor(R.color.color_black));
                        if (Utils.isNetworkConnected(this)) {
                            if (currentPos == 2) {
                                break;
                            }
                            showUpgrade();
                        } else {
                            ToastUtil.makeLongText(getApplicationContext(), "请检查网络连接！");
                        }
                        break;
                    case R.id.setup_intent_layout:
                        intentLayout.setBackgroundResource(R.drawable.homepage_bg_environment);
                        intentIv.setBackgroundResource(R.drawable.set_up_icon3_btn_n);
                        intentTv.setTextColor(getResources().getColor(R.color.color_black));
                        if (currentPos == 4) {
                            break;
                        }
                        showNetwork();
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

    private void showHome() {
        updataCloseBtn(false);
        if (currentPos == 3) {
            return;
        }
        currentPos = 3;
        homeFragment = (HomeFragment) mFragmentManager.findFragmentByTag("home");
        FragmentTransaction nowTransaction = mFragmentManager.beginTransaction();
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance(null);
        }
        nowTransaction.replace(R.id.setup_layout, homeFragment, "home");
        nowTransaction.commit();
    }

    private void showIdear() {
        //updataCloseBtn(true);
        if (currentPos == 1) {
            return;
        }
        currentPos = 1;
        idearFragment = (IdearFragment) mFragmentManager.findFragmentByTag("idear");
        FragmentTransaction nowTransaction = mFragmentManager.beginTransaction();
        if (idearFragment == null) {
            idearFragment = IdearFragment.newInstance(null);
        }
        nowTransaction.replace(R.id.setup_layout, idearFragment, "idear");
        nowTransaction.commit();
    }

    private void showUpgrade() {
        //updataCloseBtn(true);
        if (currentPos == 2) {
            return;
        }
        currentPos = 2;
        upAllgradeFragment = (UpAllgradeFragment) mFragmentManager.findFragmentByTag("upgradeall");
        FragmentTransaction nowTransaction = mFragmentManager.beginTransaction();
        if (upAllgradeFragment == null) {
            upAllgradeFragment = UpAllgradeFragment.newInstance(updataBundle);
        }
        nowTransaction.replace(R.id.setup_layout, upAllgradeFragment, "upgradeall");
        nowTransaction.commitAllowingStateLoss();
    }

    private void showNetwork(){
        //updataCloseBtn(false);
        if (currentPos == 4) {
            return;
        }
        currentPos = 4;
        networFragment = (NetWorkFragment) mFragmentManager.findFragmentByTag("network");
        FragmentTransaction nowTransaction = mFragmentManager.beginTransaction();
        if (networFragment == null) {
            networFragment = NetWorkFragment.newInstance(null);
        }
        nowTransaction.replace(R.id.setup_layout, networFragment, "network");
        nowTransaction.commit();
    }

    /**
     * 点空白 隐藏键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true; //
            }
        }
        return false;
    }
    
    @Override
    public void status(boolean isDiss) {
        Utils.showLogE("Setup", "apk download finish");
        this.getWindow().getDecorView().
                setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void updataCloseBtn(final boolean isShow) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    if (closeBtn.getVisibility() != View.VISIBLE) {
                        closeBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (closeBtn.getVisibility() != View.GONE) {
                        closeBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
