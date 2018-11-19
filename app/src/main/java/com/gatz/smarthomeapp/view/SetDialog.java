package com.gatz.smarthomeapp.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gatz.smarthomeapp.R;

/**
 * Created by zhouh on 2017/5/24.
 */
public class SetDialog extends Dialog {

    private Dialog mDialog;
    private EditText pwdEditText;
    private Button sureBtn;
    private Button cancelBtn;

    public SetDialog(Context context) {
        super(context);
        mDialog = new Dialog(getContext(), R.style.mDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.set_dialog_layout, null);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(view);
        pwdEditText = (EditText) view.findViewById(R.id.set_pj_pwd_edit);
        pwdEditText.clearFocus();
        sureBtn = (Button) view.findViewById(R.id.set_pj_sure_btn);
        cancelBtn = (Button) view.findViewById(R.id.set_pj_cancel_btn);
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void disMiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void setDialogBtnClick(View.OnClickListener click) {
        sureBtn.setOnClickListener(click);
        cancelBtn.setOnClickListener(click);
    }

    public String getPwd() {
        if (pwdEditText != null) {
            return pwdEditText.getText().toString();
        }
        return null;
    }

}
