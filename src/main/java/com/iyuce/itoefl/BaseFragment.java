package com.iyuce.itoefl;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by LeBang on 2017/1/22
 */
public class BaseFragment extends Fragment {

    private ProgressDialog progressdialog;

    public void progressdialogshow(Context context) {
        if (progressdialog == null) {
            progressdialog = new ProgressDialog(context);
        }
        progressdialog.setTitle("加载中，请稍候");
        progressdialog.setMessage("Loading...");
        progressdialog.setCanceledOnTouchOutside(false);
//         progressdialog.setCancelable(false);
        progressdialog.show();
    }

    public void progressdialogcancel() {
        if (progressdialog != null) {
            progressdialog.cancel();
        }
    }
}