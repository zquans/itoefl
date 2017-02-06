package com.iyuce.itoefl.UI.Listening.Activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;

import java.io.File;
import java.io.IOException;

public class DoResultActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mImgBtnPlay;

    private MediaPlayer mMediaPlayer;
    private SoundPool mSoundPool;
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

        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mMediaPlayer = new MediaPlayer();
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

                //MediaPlayer
                try {
                    mMediaPlayer.setDataSource("/storage/emulated/0/download/le/16895.mp3");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(2.0f));
                    }
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //SoundPool
//                mSoundPool.load("/storage/emulated/0/download/le/16895.mp3", 1);
//                mSoundPool.play(1, 1, 1, 0, 0, 1.5f);
                break;
        }
    }
}