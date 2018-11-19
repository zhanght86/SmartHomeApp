package com.gatz.smarthomeapp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gatz.smarthomeapp.model.http.ObserverCallBack;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhouh on 2017/4/17.
 */
public abstract class BaseFragment extends Fragment implements ObserverCallBack{
    protected View rootView;
    protected Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        afterCreate(savedInstanceState);
    }

    @Override
    public void onSuccessHttp(String responseInfo, int resultCode) {

    }

    @Override
    public void onFailureHttp(IOException e, int resultCode) {

    }

    @Override
    public void setData(Object obj) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);

}
