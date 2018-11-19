package com.gatz.smarthomeapp.activity.setup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by zhouh on 2017/5/3.
 */
public class HomeFragment extends BaseFragment {

    public static HomeFragment newInstance(Bundle args) {
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_layout;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {

    }
}
