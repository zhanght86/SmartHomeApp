package com.gatz.smarthomeapp.activity.setup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gatz.smarthomeapp.R;
import com.gatz.smarthomeapp.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouh on 2017/4/17.
 */
public class IdearFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.submit_btn)
    Button submitBtn;
    @BindView(R.id.idear_edit)
    EditText feedEdit;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_idea_layout;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        submitBtn.setOnClickListener(this);
    }

    public static IdearFragment newInstance(Bundle args) {
        IdearFragment fragment = new IdearFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.submit_btn) {

        }
    }
}
