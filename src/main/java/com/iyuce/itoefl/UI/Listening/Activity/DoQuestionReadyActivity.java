package com.iyuce.itoefl.UI.Listening.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.iyuce.itoefl.R;

/**
 * Created by LeBang on 2017/1/24
 */
public class DoQuestionReadyActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mImageButton;
    private boolean isPlay = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question_ready);

        initView();
    }

    private void initView() {
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        mImageButton = (ImageButton) findViewById(R.id.imgbtn_activity_do_question_ready_media);
        mImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                finish();
                break;
            case R.id.imgbtn_activity_do_question_ready_media:
                if (isPlay) {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_pause);
                } else {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_play);
                }
                isPlay = !isPlay;
                break;
        }
    }
}
