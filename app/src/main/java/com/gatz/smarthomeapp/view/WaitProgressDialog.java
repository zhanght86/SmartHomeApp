package com.gatz.smarthomeapp.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;

import com.gatz.smarthomeapp.R;


/**
 * Created by zhouh on 2017/8/17.
 */
public class WaitProgressDialog extends Dialog {
    private static WaitProgressDialog dialog;

    public WaitProgressDialog(Context context, int theme) {
        super(context, theme);
    }


    public static void init(Context context, boolean cancelable) {
        if(dialog == null){
            dialog = new WaitProgressDialog(context, R.style.Custom_Progress);
        }
        dialog.setTitle("");
        dialog.setContentView(R.layout.login_progress_dialog_layout);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
    }

    public static void vshow() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 关闭dialog
     */
    public static void dimiss() {
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
