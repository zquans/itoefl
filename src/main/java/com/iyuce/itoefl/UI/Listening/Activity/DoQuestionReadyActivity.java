package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;

import java.io.IOException;

/**
 * Created by LeBang on 2017/1/24
 */
public class DoQuestionReadyActivity extends AppCompatActivity
        implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener
        , MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private ImageButton mImageButton;
    private TextView mTxtEnglish, mTxtChinese, mTxtCurrent, mTxtTotal, mTxtBegin;
    private boolean isPlay = true;

    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private static int FLAG_PLAY = 0;
    private static int FLAG_PAUSE = 1;
    private boolean isfinish = false;

    private Handler mMediaProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    LogUtil.i("do pause");
                    mMediaProgressHandler.removeMessages(0);
                    break;
                case 0:
                    if (msg.arg1 < mMediaPlayer.getDuration()) {
                        Message message = Message.obtain();
                        message.arg1 = mMediaPlayer.getCurrentPosition();
                        message.what = FLAG_PLAY;
                        mMediaProgressHandler.sendMessageDelayed(message, 1000);
                        LogUtil.i("current = " + mMediaPlayer.getCurrentPosition() + ",,, duration = " + mMediaPlayer.getDuration());

                        mTxtCurrent.setText(message.arg1 + "");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.release();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question_ready);

        initView();
    }

    private void initView() {
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar_activity_do_question_ready);
        mTxtEnglish = (TextView) findViewById(R.id.txt_activity_do_question_ready_english);
        mTxtChinese = (TextView) findViewById(R.id.txt_activity_do_question_ready_chinese);
        mTxtCurrent = (TextView) findViewById(R.id.txt_activity_do_question_ready_current);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_do_question_ready_total);
        mImageButton = (ImageButton) findViewById(R.id.imgbtn_activity_do_question_ready_media);
        mTxtBegin = (TextView) findViewById(R.id.btn_activity_do_question_ready_begin);
        mImageButton.setOnClickListener(this);
        mTxtBegin.setOnClickListener(this);

        String SdPath = PreferenceUtil.getSharePre(this).getString("SdPath", "");
        String musicPath = SdPath + "/16895.mp3";
        LogUtil.i("musicPath = " + musicPath);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(musicPath);
            mMediaPlayer.prepare();

            mTxtTotal.setText(mMediaPlayer.getDuration() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                finish();
                break;
            case R.id.btn_activity_do_question_ready_begin:
                startActivity(new Intent(this, DoQuestionActivity.class));
                break;
            case R.id.imgbtn_activity_do_question_ready_media:
                if (isPlay) {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_pause);
                    mMediaPlayer.start();
                    Message msg = Message.obtain();
                    msg.what = FLAG_PLAY;
                    msg.arg1 = mMediaPlayer.getCurrentPosition();
                    mMediaProgressHandler.sendMessage(msg);
                } else {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_play);
                    mMediaPlayer.pause();
                    Message msg = Message.obtain();
                    msg.what = FLAG_PAUSE;
                    msg.arg1 = mMediaPlayer.getCurrentPosition();
                    mMediaProgressHandler.sendMessage(msg);
                }
                isPlay = !isPlay;
                LogUtil.i("current = " + mMediaPlayer.getCurrentPosition() + ",,, duration = " + mMediaPlayer.getDuration());
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        getDrution();
    }

    private void getCurrent() {
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
        int time = mMediaPlayer.getCurrentPosition() / 1000;
        int h, m, s;
        h = time / 60 / 60;
        m = (time - 60 * (h * 60)) / 60;
        s = time % 60;
        String timer = h + ":" + m + ":" + s;
        mTxtCurrent.setText(timer);
    }

    private void getDrution() {
        mSeekBar.setMax(mMediaPlayer.getDuration());
        int time = mMediaPlayer.getDuration() / 1000;
        int h, m, s;
        h = time / 60 / 60;
        m = (time - 60 * (h * 60)) / 60;
        s = time % 60;
        String timer = h + ":" + m + ":" + s;
        mTxtTotal.setText(timer);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.e("complete", "I am complete");
        mTxtCurrent.setText("00:00:00");
        mSeekBar.setProgress(0);
        isfinish = true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        getCurrent();
        LogUtil.i("percent = " + percent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        LogUtil.i("what = " + what + ",extra = " + extra);
        return false;
    }
}