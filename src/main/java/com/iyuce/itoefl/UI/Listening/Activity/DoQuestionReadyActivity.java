package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/24
 */
public class DoQuestionReadyActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AdapterView.OnItemSelectedListener {

    private ImageButton mImageButton;
    private TextView mTxtEnglish, mTxtChinese, mTxtCurrent, mTxtTotal, mTxtBegin;

    //媒体播放
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private boolean isPlay = true;
    private boolean isfinish = false;

    //音频播放速度调整
    private Spinner mSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ArrayList<String> mRateList = new ArrayList<>();

    //路径
    private String local_path;

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
        doBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
        mMediaPlayer.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question_ready);

        initView();
    }

    private void initView() {
        local_path = getIntent().getStringExtra("local_path");
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar_activity_do_question_ready);
        mSpinner = (Spinner) findViewById(R.id.spinner_activity_do_question_ready_rate);
        mTxtEnglish = (TextView) findViewById(R.id.txt_activity_do_question_ready_english);
        mTxtChinese = (TextView) findViewById(R.id.txt_activity_do_question_ready_chinese);
        mTxtCurrent = (TextView) findViewById(R.id.txt_activity_do_question_ready_current);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_do_question_ready_total);
        mImageButton = (ImageButton) findViewById(R.id.imgbtn_activity_do_question_ready_media);
        mTxtBegin = (TextView) findViewById(R.id.btn_activity_do_question_ready_begin);
        mImageButton.setOnClickListener(this);
        mTxtBegin.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);

        //音频播放速度选择
        mRateList.add("播放速度 x1.0");
        mRateList.add("播放速度 x1.2");
        mRateList.add("播放速度 x1.4");
        mRateList.add("播放速度 x1.6");
        mRateList.add("播放速度 x1.8");
        mSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_control_audio_rate, mRateList);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);

        String musicPath = local_path + "/16895.mp3";
        LogUtil.i("musicPath = " + musicPath);

        //音频准备
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                doBack();
                break;
            case R.id.btn_activity_do_question_ready_begin:
                Intent intent = new Intent(this, DoQuestionActivity.class);
                intent.putExtra("local_path", local_path);
                startActivity(intent);
                break;
            case R.id.imgbtn_activity_do_question_ready_media:
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

    private void getCurrent() {
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
        int time = mMediaPlayer.getCurrentPosition() / 1000;
        mTxtCurrent.setText(TimeUtil.toTimeShow(time));
    }

    private void getDrution() {
        mSeekBar.setMax(mMediaPlayer.getDuration());
        int time = mMediaPlayer.getDuration() / 1000;
        mTxtTotal.setText(TimeUtil.toTimeShow(time));
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
    public void onCompletion(MediaPlayer mp) {
        mTxtCurrent.setText(R.string.txt_audio_time_begin);
        mSeekBar.setProgress(0);
        isfinish = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        LogUtil.i("what ? = " + what);
        return false;
    }

    //SeekBar
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //不加这层判断将导致音频同步卡顿
        if (fromUser) {
            mMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void changeSpeed(float rate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(rate));
        }
    }

    //Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.i("position = " + position);
        switch (position) {
            case 0:
                changeSpeed(1.0f);
                break;
            case 1:
                changeSpeed(1.2f);
                break;
            case 2:
                changeSpeed(1.4f);
                break;
            case 3:
                changeSpeed(1.6f);
                break;
            case 4:
                changeSpeed(1.8f);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void doBack() {
        new AlertDialog.Builder(this).setTitle("退出练习").setMessage("确定退出练习")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(DoQuestionReadyActivity.this, PageReadyActivity.class));
                    }
                }).setNegativeButton("取消", null).show();
    }
}