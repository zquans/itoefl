package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.ListenResult;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.ResultTitleAdapter;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoResult;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.RecyclerItemClickListener;
import com.iyuce.itoefl.Utils.TimeUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;

public class DoResultActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        ViewPager.OnPageChangeListener, FragmentDoResult.OnFragmentInteractionListener {

    private ImageButton mImageButton;
    private TextView mTxtCurrent, mTxtTotal;

    //音频播放相关
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private boolean isPlay = true;
    private boolean isfinish = false;

    //答案相关
    private RecyclerView mRecyclerView;
    private ResultTitleAdapter mTitleAdapter;
    private ArrayList<ListenResult> mResultTitleList = new ArrayList<>();
    private ViewPager mViewPager;
    private AnswerAdapter mContentAdapter;
    private ArrayList<Fragment> mResultContentList = new ArrayList<>();

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
        doBackPageReady();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
        mMediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
        mMediaPlayer.pause();
        mImageButton.setBackgroundResource(R.mipmap.icon_media_play);
        isPlay = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_result);

        initView();
    }

    private void initView() {
        local_path = getIntent().getStringExtra("local_path");
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);

        mSeekBar = (SeekBar) findViewById(R.id.bar_activity_do_result_progress);
        mTxtCurrent = (TextView) findViewById(R.id.txt_activity_do_result_current);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_do_result_total);
        mImageButton = (ImageButton) findViewById(R.id.imgbtn_activity_do_result_play);
        mImageButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);

        //TODO 模拟数据换真实数据
        for (int i = 1; i < 6; i++) {
            ListenResult result = new ListenResult();
            result.question_name = i + "";
            result.question_state = i % 2 == 0;
            result.question_is_select = i == 1;
            //传递给Fragment数据,可以增加参数
            FragmentDoResult mFrgDoResult = FragmentDoResult.newInstance(result.question_name);
            mResultTitleList.add(result);
            mResultContentList.add(mFrgDoResult);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_do_result_question);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTitleAdapter = new ResultTitleAdapter(mResultTitleList, this);
        mRecyclerView.setAdapter(mTitleAdapter);
        //放在Activity中做点击事件
        doRecyclerItemClick();

        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_do_result_question);
        mContentAdapter = new AnswerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mContentAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setOnPageChangeListener(this);

        //MediaPlayer
        String musicPath = local_path + "/16895.mp3";
        LogUtil.i("musicPath = " + musicPath);
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

    /**
     * 放在Activity中做点击事件
     */
    private void doRecyclerItemClick() {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mViewPager.setCurrentItem(position);
                        //切换题目时候的效果
                        changeQuestion(position);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));
    }

    /**
     * 切换题目时候的动画效果
     */
    private void changeQuestion(int position) {
        //修改选中项的数据
        for (int i = 0; i < mResultTitleList.size(); i++) {
            mResultTitleList.get(i).question_is_select = false;
        }
        mResultTitleList.get(position).question_is_select = true;
        /*必须要刷新列表中的数据，否则没有动画效果*/
        mTitleAdapter.notifyDataSetChanged();

        //执行动画
        for (int i = 0; i < mResultTitleList.size(); i++) {
            View itemChangeImg = mRecyclerView.getChildAt(position).findViewById(R.id.img_item_do_result);
            TextView itemChangeTxt = (TextView) mRecyclerView.getChildAt(position).findViewById(R.id.txt_item_do_result);
            if (!mResultTitleList.get(position).question_is_select) {
                //未选中的
                itemChangeTxt.setTextColor(Color.parseColor("#000000"));
                if (mResultTitleList.get(position).question_state) {
                    //正确的
                    itemChangeImg.setBackgroundResource(R.mipmap.icon_answer_cycle_right_stroke);
                } else {
                    //错误的
                    itemChangeImg.setBackgroundResource(R.mipmap.icon_answer_cycle_wrong_stroke);
                }
            } else {
                //选中的
                itemChangeTxt.setTextColor(Color.parseColor("#ffffff"));
                if (mResultTitleList.get(position).question_state) {
                    //正确的
                    itemChangeImg.setBackgroundResource(R.mipmap.icon_answer_talk_right_full);
                } else {
                    //错误的
                    itemChangeImg.setBackgroundResource(R.mipmap.icon_answer_talk_wrong_full);
                }
            }
        }
    }

    //音频进度
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

    //seekBar
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    //mediaPlayer
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

    //TODO 预留Fragment中的反馈接口
    @Override
    public void onFragmentInteraction(String string) {
        ToastUtil.showMessage(this, string);
    }

    //ViewPager
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        changeQuestion(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //ViewPager的Adapter内部类
    private class AnswerAdapter extends FragmentPagerAdapter {
        public AnswerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mResultContentList.get(position);
        }

        @Override
        public int getCount() {
            return mResultContentList.size();
        }
    }
}