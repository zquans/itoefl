package com.iyuce.itoefl.Control;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Control.Main.MainActivity;
import com.iyuce.itoefl.R;

public class WelcomeActivity extends BaseActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_welcome);

        judgePermission();
    }

    private void judgePermission() {
        //判断是否有权限
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //有权限，通过handle处理倒计时跳转
            handleToMain();
        } else {
            //没权限，进行权限请求
            requestPermission(Constants.CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void doStore() {
        super.doStore();
        handleToMain();
    }

    private void handleToMain() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toMain();
            }
        }, 1000);
    }

    private void toMain() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        WelcomeActivity.this.finish();
    }
}