package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;
import com.iyuce.itoefl.Utils.TimeUtil;

import java.io.IOException;

public class DoResultActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private ImageButton mImageButton;
    private TextView mTxtCurrent, mTxtTotal;

    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private boolean isPlay = true;
    private boolean isfinish = false;

    private Handler mMediaProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.FLAG_AUDIO_PAUSE:
                    mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
                    break;
                case Constants.FLAG_AUDIO_PLAY:
                    if (isfinish) {
                        mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
                        mImageButton.setBackgroundResource(R.mipmap.icon_media_play);
                        mTxtCurrent.setText(R.string.txt_audio_time_begin);
                        mSeekBar.setProgress(0);
                        isfinish = false;
                        isPlay = false;
                        break;
                    }
                    Message message = Message.obtain();
                    message.what = Constants.FLAG_AUDIO_PLAY;
                    mMediaProgressHandler.sendMessageDelayed(message, 1000);
                    getCurrent();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        doBackPageReady();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
        mMediaPlayer.release();
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
        mImageButton = (ImageButton) findViewById(R.id.imgbtn_activity_do_result_play);
        mImageButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);

        //从sharePreferences获取路径
        String SdPath = PreferenceUtil.getSharePre(this).getString("SdPath", "");
        String musicPath = SdPath + "/16895.mp3";
        LogUtil.i("musicPath = " + musicPath);

        //MediaPlayer
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        try {
            mMediaPlayer.setDataSource(musicPath);
            mMediaPlayer.prepare();
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
                Message msg = Message.obtain();
                if (!isPlay) {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_pause);
                    mMediaPlayer.start();
                    msg.what = Constants.FLAG_AUDIO_PLAY;
                } else {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_play);
                    mMediaPlayer.pause();
                    msg.what = Constants.FLAG_AUDIO_PAUSE;
                }
                mMediaProgressHandler.sendMessage(msg);
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

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        Message msg = Message.obtain();
        msg.what = Constants.FLAG_AUDIO_PLAY;
        mMediaProgressHandler.sendMessage(msg);
        getDrution();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer.reset();
        LogUtil.i("what ? = " + what);
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.reset();
        mTxtCurrent.setText(R.string.txt_audio_time_begin);
    }
}