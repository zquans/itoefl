package com.iyuce.itoefl.Control.Mine;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;

/**
 * Created by Administrator on 2016/9/23
 */
public class AboutUsActivity extends BaseActivity {

    private TextView mTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mTxtTitle = (TextView) findViewById(R.id.txt_header_title_item);
        mTxtTitle.setText("关于我们");
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        initEvent();
    }

    /* 两种方式判断最大允许运行内存 */
    private void initEvent() {
        ActivityManager activityManager = (ActivityManager) AboutUsActivity.this
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryClass();
        activityManager.getLargeMemoryClass();
        LogUtil.e("ActivityManager方式获取", "memory = " + activityManager.getMemoryClass() + ",largememory = "
                + activityManager.getLargeMemoryClass());

        Runtime.getRuntime().maxMemory();
        LogUtil.e("Runtime获取", "runtimememory = " + Runtime.getRuntime().maxMemory() + ", 即"
                + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "M");
    }

}