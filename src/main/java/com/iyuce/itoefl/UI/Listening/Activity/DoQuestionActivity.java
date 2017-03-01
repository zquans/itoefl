package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.BottomDoQuestionAdapter;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoQuestion;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DoQuestionActivity extends BaseActivity implements
        View.OnClickListener, BottomDoQuestionAdapter.OnButtonItemClickListener {

    private TextView mTxtTimer;
    private TextView mTxtReview;
    private TextView mTxtNext;
    private TextView mTxtCurrent;
    private TextView mTxtTotal;

    private BottomSheetDialog mBottomDialog;

    private RecyclerView mRecyclerView;
    private ArrayList<String> mDataBottomList = new ArrayList<>();
    private BottomDoQuestionAdapter mAdapter;

    private String TOTAL_QUESTION_COUNT;
    private ArrayList<String> mSortList;
    private ArrayList<String> mMusicQuestionList;
    private ArrayList<String> mMusicAnswerList;
    private ArrayList<String> mQuestionIdList;

    //变量，当前题,预设为1
    private int mCurrentQuestion = 1;

    //保存所选答案的题号和内容
    private ArrayList<Integer> mSelectedQuestionList = new ArrayList<>();
    private ArrayList<String> mSelectedAnswerList = new ArrayList<>();
    //表中正确答案数组
    private ArrayList<String> mOptionAnswerList = new ArrayList<>();

    //路径
    private String local_paper_code, local_path, local_music_question;

    private FragmentDoQuestion mFrgment;

    //计时器
    private Timer mTimer = new Timer();
    private int TimeCount = 0;
    private int lastTimeCount = 0;
    private ArrayList<String> mTimeCountList = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTxtTimer.setText("答题用时 " + msg.arg1 + " 秒");
        }
    };

    @Override
    public void onBackPressed() {
        doBackPageReady();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimer.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question);

        initView();
    }

    private void initView() {
        local_paper_code = getIntent().getStringExtra(Constants.PaperCode);
        local_path = getIntent().getStringExtra("local_path");
        local_music_question = getIntent().getStringExtra(Constants.MusicQuestion);

        mTxtTimer = (TextView) findViewById(R.id.txt_header_title_menu);
        TextView mTxtHeadTitle = (TextView) findViewById(R.id.txt_header_title_item);
        mTxtHeadTitle.setText(local_paper_code);
        mTxtTimer.setText("答题用时 0 秒");
        ImageButton mImgClose = (ImageButton) findViewById(R.id.imgbtn_header_title);
        mImgClose.setBackgroundResource(R.mipmap.icon_close);
        mImgClose.setOnClickListener(this);

        mTxtNext = (TextView) findViewById(R.id.txt_activity_do_question_next);
        mTxtReview = (TextView) findViewById(R.id.txt_activity_do_question_review);
        mTxtCurrent = (TextView) findViewById(R.id.txt_activity_do_question_current);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_do_question_total);
        mTxtReview.setOnClickListener(this);
        mTxtNext.setOnClickListener(this);

        //初始化底部选题
        initBottomSheet();

        //数据源
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, local_path + "/" + local_paper_code + ".sqlite",
                Constants.DATABASE_VERSION).getWritableDatabase();
        //传递给Fragment的参数
        mSortList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_QUESTION, null, Constants.Sort);
        mMusicQuestionList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_QUESTION, null, Constants.MusicQuestion);
        mQuestionIdList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_QUESTION, null, Constants.QuestionId);
        //这个直接传给DoResult就好
        mMusicAnswerList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_QUESTION, null, Constants.MusicAnswer);
        //查另一张表,正确答案的数组
        mOptionAnswerList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.Answer);
        LogUtil.i(" now mOptionAnswerList = " + mOptionAnswerList);
        mDatabase.close();

        TOTAL_QUESTION_COUNT = String.valueOf(mSortList.size());
        mTxtTotal.setText(TOTAL_QUESTION_COUNT);
        for (int i = 0; i < mSortList.size(); i++) {
            mDataBottomList.add(mSortList.get(i));
        }
        //应该传递给Fragment的参数  QuestionId(用于在Fragment中继续查表)、Sort题号、MusicQuestion音频,默认是pos=0的值
        mFrgment = FragmentDoQuestion.newInstance(
                TOTAL_QUESTION_COUNT, mSortList.get(0), mMusicQuestionList.get(0), mQuestionIdList.get(0), local_path, local_paper_code);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();

        //执行计时任务
        toCountTime();
    }

    private void toCountTime() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //判断是否在播放录音，是则暂停累加数字
                if (mFrgment.finishMediaPlayer()) {
                    TimeCount++;
                    Message msg = Message.obtain();
                    msg.arg1 = TimeCount;
                    mHandler.sendMessage(msg);
                }
            }
        }, 500, 1000);
    }

    private void initBottomSheet() {
        mBottomDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.style_bottom_do_question, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_bottom_do_question);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new BottomDoQuestionAdapter(mDataBottomList, this);
        mAdapter.setOnBottomItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mBottomDialog.setContentView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                doBackPageReady();
                break;
            case R.id.txt_activity_do_question_review:
                mBottomDialog.show();
                break;
            case R.id.txt_activity_do_question_next:
                //若未播放完音频，不允许操作下一步
                if (!mFrgment.finishMediaPlayer()) {
                    ToastUtil.showMessage(this, "未播放完录音");
                    return;
                }
                //若未选择答案，也不允许操作下一步
                if (TextUtils.isEmpty(mFrgment.selectAnswer())) {
                    ToastUtil.showMessage(this, "本题未答完");
                    return;
                }

                //保存或替换当前题号和所选答案
                if (mSelectedQuestionList.contains(mCurrentQuestion)) {
                    //替换答案
                    mSelectedAnswerList.set(mCurrentQuestion - 1, mFrgment.selectAnswer());
                } else {
                    //保存答案
                    mSelectedQuestionList.add(mCurrentQuestion);
                    mSelectedAnswerList.add(mFrgment.selectAnswer());
                    //保存答题时间,并标记上一次被存的时间，做减法
                    mTimeCountList.add(String.valueOf(TimeCount - lastTimeCount));
                    lastTimeCount = TimeCount;
                }
                LogUtil.i("all = " + mSelectedQuestionList.toString() + "||" + mSelectedAnswerList.toString());
                //答完,进入下一个页面
                if (mSelectedQuestionList.size() == mSortList.size()) {
                    Intent intent = new Intent(this, DoResultActivity.class);
                    intent.putExtra(Constants.PaperCode, local_paper_code);
                    intent.putExtra("local_path", local_path);
                    //TODO 这个路径不一定传，本意是留给doResult中音频的，但老大的音频解析有分段的
                    intent.putExtra(Constants.MusicQuestion, local_music_question);
                    //TODO 所以这次我传了这个，所有音频答案末位路径的数组
                    intent.putStringArrayListExtra(Constants.MusicAnswer, mMusicAnswerList);
                    //TODO 以及这三个，正确答案的数组，我的选择的数组,答题用时数组
                    intent.putStringArrayListExtra("mOptionAnswerList", mOptionAnswerList);
//                    intent.putStringArrayListExtra(Constants.MusicAnswer, mSelectedQuestionList);
                    intent.putStringArrayListExtra("mSelectedAnswerList", mSelectedAnswerList);
                    intent.putStringArrayListExtra("mTimeCountList", mTimeCountList);
                    LogUtil.i(mSelectedAnswerList + mOptionAnswerList.toString() + mTimeCountList);
                    //留给下一级，省去查表的开销
                    intent.putStringArrayListExtra("mSortList", mSortList);
                    intent.putStringArrayListExtra("mQuestionIdList", mQuestionIdList);
                    startActivity(intent);
                    break;
                }
                //或者未答完，换下一题
                mCurrentQuestion++;
                ToastUtil.showMessage(this, "当前第" + mCurrentQuestion + "题");
                SkipToQuestion(mCurrentQuestion);
                break;
        }
    }

    private void doBackPageReady() {
        new AlertDialog.Builder(this).setTitle("退出练习").setMessage("确定退出练习")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(DoQuestionActivity.this, PageReadyActivity.class));
                    }
                }).setNegativeButton("取消", null).show();
    }

    @Override
    public void onBottomClick(int position) {
        mBottomDialog.dismiss();
        //TODO 保存所作题目和对应答案,暂不存入数据库，当所有题目答完，将ArrayList.toString存入数据库
        if (mSelectedQuestionList.contains(position)) {
            mSelectedAnswerList.set(position - 1, mFrgment.selectAnswer());

            SkipToQuestion(position);

            //切换当前题到BottomSheet所选题
            mCurrentQuestion = position;
        } else {
            //如果是未答过的题，不允许跳转
            if (position > mCurrentQuestion)
                ToastUtil.showMessage(this, "本题未答完，无法查看后面的题");
            else
                SkipToQuestion(position);
        }
        LogUtil.i("all = " + mSelectedQuestionList.toString() + "||" + mSelectedAnswerList.toString());
    }

    /**
     * 跳到某题，传参数给相应控件
     */
    private void SkipToQuestion(int position) {
        mTxtCurrent.setText(mSortList.get(position - 1));
        mFrgment = FragmentDoQuestion.newInstance(
                TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1), mQuestionIdList.get(position - 1),
                local_path, local_paper_code);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
    }
}