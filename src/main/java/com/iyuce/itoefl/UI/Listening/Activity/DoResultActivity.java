package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;
import com.iyuce.itoefl.Utils.TimeUtil;

import java.io.IOException;

public class DoResultActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton mImgBtnPlay;
    private TextView mTxtCurrent, mTxtTotal;

    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private boolean isPlay = true;

    private String mSavePath;

    @Override
    public void onBackPressed() {
        doBackPageReady();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_result);

        initView();
    }

    private void initView() {
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);

        mSeekBar = (SeekBar) findViewById(R.id.bar_activity_do_result_progress);
        mTxtCurrent = (TextView) findViewById(R.id.txt_activity_do_result_current);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_do_result_total);
        mImgBtnPlay = (ImageButton) findViewById(R.id.imgbtn_activity_do_result_play);
        mImgBtnPlay.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);

        //从sharePreferences获取路径
        String SdPath = PreferenceUtil.getSharePre(this).getString("SdPath", "");
        String musicPath = SdPath + "/16895.mp3";
        LogUtil.i("musicPath = " + musicPath);

        //MediaPlayer
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(musicPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            getDrution();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCurrent() {
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
        int time = mMediaPlayer.getCurrentPosition() / 1000;
        String timer = TimeUtil.toTimeShow(time);
        mTxtCurrent.setText(timer);
    }

    private void getDrution() {
        mSeekBar.setMax(mMediaPlayer.getDuration());
        int time = mMediaPlayer.getDuration() / 1000;
        String timer = TimeUtil.toTimeShow(time);
        mTxtTotal.setText(timer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                doBackPageReady();
                break;
            case R.id.imgbtn_activity_do_result_play:
                if (!isPlay) {
                    mImgBtnPlay.setBackgroundResource(R.mipmap.icon_media_pause);
                    mMediaPlayer.start();
                } else {
                    mImgBtnPlay.setBackgroundResource(R.mipmap.icon_media_play);
                    mMediaPlayer.pause();
                }
                isPlay = !isPlay;
                break;
        }
    }

    private void doBackPageReady() {
        startActivity(new Intent(this, PageReadyActivity.class));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mMediaPlayer.seekTo(progress);
            mTxtCurrent.setText(TimeUtil.toTimeShow(progress / 1000));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}