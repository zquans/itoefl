package com.iyuce.itoefl.UI.Listening.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.iyuce.itoefl.R;

public class DoResultActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mImgBtnPlay;

    private boolean isPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_result);

        initView();
    }

    private void initView() {
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);

        mImgBtnPlay = (ImageButton) findViewById(R.id.imgbtn_activity_do_result_play);
        mImgBtnPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                finish();
                break;
            case R.id.imgbtn_activity_do_result_play:
                if (!isPlay)
                    mImgBtnPlay.setBackgroundResource(R.mipmap.icon_media_pause);
                else
                    mImgBtnPlay.setBackgroundResource(R.mipmap.icon_media_play);
                isPlay = !isPlay;
                break;
        }
    }
}