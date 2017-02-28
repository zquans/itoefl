package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.iyuce.itoefl.Model.Exercise.ListenResult;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.ResultTitleAdapter;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoResult;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.RecyclerItemClickListener;
import com.iyuce.itoefl.Utils.TimeUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

import java.io.File;
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
    private boolean isFinish = false;

    //答案相关
    private RecyclerView mRecyclerView;
    private ResultTitleAdapter mTitleAdapter;
    private ArrayList<ListenResult> mResultTitleList = new ArrayList<>();
    private ViewPager mViewPager;
    private AnswerAdapter mContentAdapter;
    private ArrayList<Fragment> mResultContentList = new ArrayList<>();

    //传递而来的数组参数,省去查表的开销
    private ArrayList<String> mSortList;
    private ArrayList<String> mQuestionIdList;
    //答案相关的数组
    private ArrayList<String> mMusicAnswerList;
    private ArrayList<String> mOptionAnswerList;
    private ArrayList<String> mSelectedAnswerList;
    private ArrayList<String> mTimeCountList;

    //路径
    private String local_paper_code, local_path, local_music_question;

    private static final int BEGIN = 0;

    private Handler mMediaProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.FLAG_AUDIO_PAUSE:
                    mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
                    break;
                case Constants.FLAG_AUDIO_PLAY:
                    if (isFinish) {
                        mMediaProgressHandler.removeMessages(Constants.FLAG_AUDIO_PLAY);
                        mImageButton.setBackgroundResource(R.mipmap.icon_media_play);
                        mTxtCurrent.setText(R.string.txt_audio_time_begin);
                        mSeekBar.setProgress(BEGIN);
                        isFinish = false;
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
        local_paper_code = getIntent().getStringExtra(Constants.PaperCode);
        local_path = getIntent().getStringExtra("local_path");
        local_music_question = getIntent().getStringExtra(Constants.MusicQuestion);
        mSortList = getIntent().getStringArrayListExtra("mSortList");
        mQuestionIdList = getIntent().getStringArrayListExtra("mQuestionIdList");
        //答案相关数组
        mMusicAnswerList = getIntent().getStringArrayListExtra(Constants.MusicAnswer);
        mOptionAnswerList = getIntent().getStringArrayListExtra("mOptionAnswerList");
        mSelectedAnswerList = getIntent().getStringArrayListExtra("mSelectedAnswerList");
        mTimeCountList = getIntent().getStringArrayListExtra("mTimeCountList");

        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);
        TextView mTxtHeadTitle = (TextView) findViewById(R.id.txt_header_title_item);
        mTxtHeadTitle.setText("练习结果\r" + local_paper_code);

        mSeekBar = (SeekBar) findViewById(R.id.bar_activity_do_result_progress);
        mTxtCurrent = (TextView) findViewById(R.id.txt_activity_do_result_current);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_do_result_total);
        mImageButton = (ImageButton) findViewById(R.id.imgbtn_activity_do_result_play);
        mImageButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);

        //数据源
        for (int i = 0; i < mSortList.size(); i++) {
            SQLiteDatabase mDatabase = DbUtil.getHelper(this, local_path + "/" + local_paper_code + ".sqlite", Constants.DATABASE_VERSION).getWritableDatabase();
            //查表Question
            String mQuestionType = DbUtil.queryToString(mDatabase, Constants.TABLE_QUESTION, Constants.QuestionType, Constants.ID, mQuestionIdList.get(i));
            String mContent = DbUtil.queryToString(mDatabase, Constants.TABLE_QUESTION, Constants.Content, Constants.ID, mQuestionIdList.get(i));
            String mAnswer = DbUtil.queryToString(mDatabase, Constants.TABLE_QUESTION, Constants.Answer, Constants.ID, mQuestionIdList.get(i));
            //查表Option
            ArrayList<String> mOptionContentList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_OPTION, Constants.Content, Constants.QuestionId, mQuestionIdList.get(i));
            ArrayList<String> mOptionCodeList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_OPTION, Constants.Code, Constants.QuestionId, mQuestionIdList.get(i));
            mDatabase.close();
            ListenResult result = new ListenResult();
            result.question_name = mSortList.get(i);
            result.choice_right = mOptionAnswerList.get(i);
            result.choice_user = mSelectedAnswerList.get(i);
            if (result.choice_right.equals(result.choice_user)) {
                result.question_state = true;
            }
            //如果i=0，默认选中
            result.question_is_select = i == 0;
            //传递给Fragment数据,可以增加参数
            FragmentDoResult mFragmentDoResult = FragmentDoResult.newInstance(result.question_name,
                    mSortList.size() + "", mContent, mOptionContentList, mOptionCodeList, mQuestionType,
                    mSelectedAnswerList.get(i), mOptionAnswerList.get(i), mTimeCountList.get(i));
            mResultTitleList.add(result);
            mResultContentList.add(mFragmentDoResult);
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
        mViewPager.setOffscreenPageLimit(mSortList.size() - 1);
        mViewPager.setOnPageChangeListener(this);

        //MediaPlayer
        String musicPath = local_path + File.separator + local_music_question;
        LogUtil.i("doResult musicPath = " + musicPath);
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
        //切换音频
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(local_path + File.separator + mMusicAnswerList.get(position));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        mSeekBar.setProgress(BEGIN);
        isFinish = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
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