package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.ContentValues;
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
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoQuestionDefault;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoQuestionJudge;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoQuestionMulti;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoQuestionSingle;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoQuestionSort;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

import java.io.File;
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

    private ArrayList<String> mQuestionIdList;
    private ArrayList<String> mMasterIdList;
    private ArrayList<String> mQuestionTypeList;
    private ArrayList<String> mQuestionContentList;
    private ArrayList<String> mSortList;
    private ArrayList<String> mMusicQuestionList;
    private ArrayList<String> mMusicAnswerList;

    //总题数
    private String TOTAL_QUESTION_COUNT;
    //当前题,预设为1
    private int mCurrentQuestion = 1;

    //保存所选答案的题号，本类中切换题用
    private ArrayList<Integer> mSelectedQuestionList = new ArrayList<>();
    //保存所选的答案，传入下一级
    private ArrayList<String> mSelectedAnswerList = new ArrayList<>();
    private ArrayList<String> mAnswerList = new ArrayList<>();

    //路径
    private String local_paper_code, local_path, local_music_question;

    private FragmentDoQuestionDefault mFrgment;

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
        backPageReady();
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
        local_path = getIntent().getStringExtra("local_path");
        local_paper_code = getIntent().getStringExtra(Constants.PaperCode);
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

        //数据源 //TODO 这些其实也可以封装在DbUtil中返回一个对象
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, local_path + "/" + local_paper_code + ".sqlite").getWritableDatabase();
        //传递给Fragment的参数
        mQuestionIdList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.ID);
        mMasterIdList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.MasterId);
        mQuestionTypeList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.QuestionType);
        mQuestionContentList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.Content);
        mSortList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.Sort);
        mMusicQuestionList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.MusicQuestion);
        //这个直接传给DoResult就好
        mMusicAnswerList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_QUESTION, null, Constants.MusicAnswer);
        mDatabase.close();

        TOTAL_QUESTION_COUNT = String.valueOf(mSortList.size());
        mTxtTotal.setText(TOTAL_QUESTION_COUNT);
        for (int i = 0; i < mSortList.size(); i++) {
            mDataBottomList.add(mSortList.get(i));
        }
        //根据当前题型进行不同Fragment的数据装载,初始为1是因为要获取mQuestionTypeList数组的第一项
        toSwitch(1);
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
                backPageReady();
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
                    mAnswerList.set(mCurrentQuestion - 1, mFrgment.realAnswer());
                } else {
                    //保存答案
                    mSelectedQuestionList.add(mCurrentQuestion);
                    mSelectedAnswerList.add(mFrgment.selectAnswer());
                    mAnswerList.add(mFrgment.realAnswer());
                    //保存答题时间,并标记上一次被存的时间，做减法
                    mTimeCountList.add(String.valueOf(TimeCount - lastTimeCount));
                    lastTimeCount = TimeCount;
                }
                LogUtil.i("when click = " + mSelectedQuestionList.toString() + mSelectedAnswerList.toString() + mAnswerList);

                //未答完，换下一题
                if (mSelectedQuestionList.size() != mSortList.size()) {
                    mCurrentQuestion++;
                    SkipToQuestion(mCurrentQuestion);
                    LogUtil.i("当前第" + mCurrentQuestion + "题");
                    break;
                }
                //TODO 比较两个答案是否相同，直接传递是否正确的结果给下一级
                ArrayList<String> mBingoList = new ArrayList();
                for (int i = 0; i < mSelectedAnswerList.size(); i++) {
                    if (TextUtils.equals(mSelectedAnswerList.get(i), mAnswerList.get(i))) {
                        mBingoList.add("true");
                    } else {
                        mBingoList.add("false");
                    }
                }
                //或者答完,建用户练习表，保存用户的做题记录,进入下一个页面
                String downloaded_sql_path = SDCardUtil.getExercisePath() + File.separator + Constants.SQLITE_DOWNLOAD;
                SQLiteDatabase mDatabase = DbUtil.getHelper(this, downloaded_sql_path).getWritableDatabase();
                String create = "create table if not exists " + Constants.TABLE_ALREADY_PRACTICED + "("
                        + Constants.Sort + " integer primary key,"
                        + Constants.PaperCode + " text,"
                        + Constants.RuleName + " text,"
                        + Constants.QuestionType + " text,"
                        + Constants.QuestionId + " text,"
                        + Constants.Content + " text,"
                        + Constants.UserSelect + " text,"
                        + Constants.Answer + " text,"
                        + Constants.Bingo + " text,"
                        + Constants.TimeCount + " text)";
                mDatabase.execSQL(create);
                mDatabase.beginTransaction();
                for (int i = 0; i < mSortList.size(); i++) {
                    //判断是否存在，不存在则插入，存在则更新
                    String sql_replace = "replace into " + Constants.TABLE_ALREADY_PRACTICED + " ("
                            + Constants.Sort + ","
                            + Constants.PaperCode + ","
                            + Constants.RuleName + ","
                            + Constants.QuestionType + ","
                            + Constants.QuestionId + ","
                            + Constants.Content + ","
                            + Constants.UserSelect + ","
                            + Constants.Answer + ","
                            + Constants.Bingo + ","
                            + Constants.TimeCount + ")values(\""
                            + mSortList.get(i) + "\",\""
                            + local_paper_code + "\",\""
                            + local_paper_code + "\",\""
                            + mQuestionTypeList.get(i) + "\",\""
                            + mQuestionIdList.get(i) + "\",\""
                            + mQuestionContentList.get(i) + "\",\""
                            + mSelectedAnswerList.get(i) + "\",\""
                            + mAnswerList.get(i) + "\",\""
                            + mBingoList.get(i) + "\",\""
                            + mTimeCountList.get(i) + "\" )";
                    mDatabase.execSQL(sql_replace);
                }
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();

                //TODO 往下载表中存入字段,标明已经练习过
                ContentValues mValues = new ContentValues();
                mValues.put(Constants.Practiced, "true");
                mDatabase.update(Constants.TABLE_ALREADY_DOWNLOAD, mValues, Constants.SECTION + " =? and " + Constants.MODULE + " =? ",
                        new String[]{local_paper_code.substring(0, 5), local_paper_code.substring(6)});
                mValues.clear();
                mDatabase.close();
                LogUtil.i("subString = " + local_paper_code.substring(0, 5) + ",," + local_paper_code.substring(6));

                Intent intent = new Intent(this, DoResultActivity.class);
                intent.putExtra("local_path", local_path);
                intent.putExtra(Constants.PaperCode, local_paper_code);
                intent.putExtra(Constants.MusicQuestion, local_music_question);
                //分段录音
                intent.putStringArrayListExtra(Constants.MusicAnswer, mMusicAnswerList);
                intent.putStringArrayListExtra("mBingoList", mBingoList);
                intent.putStringArrayListExtra("mAnswerList", mAnswerList);
                intent.putStringArrayListExtra("mSelectedAnswerList", mSelectedAnswerList);
                intent.putStringArrayListExtra("mTimeCountList", mTimeCountList);
                LogUtil.i(mSelectedAnswerList + mAnswerList.toString() + mTimeCountList);
                //留给下一级，省去查表的开销
                intent.putStringArrayListExtra("mSortList", mSortList);
                intent.putStringArrayListExtra("mQuestionIdList", mQuestionIdList);
                intent.putStringArrayListExtra("mQuestionTypeList", mQuestionTypeList);
                intent.putStringArrayListExtra("mQuestionContentList", mQuestionContentList);
                startActivity(intent);
                break;
        }
    }

    private void backPageReady() {
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
        // 保存所作题目和对应答案,暂不存入数据库，当所有题目答完，将ArrayList.toString存入数据库
        if (mSelectedQuestionList.contains(position)) {
            mSelectedAnswerList.set(position - 1, mFrgment.selectAnswer());
            mAnswerList.set(position - 1, mFrgment.realAnswer());

            //切换当前Fragment
            SkipToQuestion(position);
            //切换当前题到BottomSheet所选题
            mCurrentQuestion = position;
        } else {
            //如果是未答过的题，不允许跳转
            if (position > mCurrentQuestion) {
                ToastUtil.showMessage(this, "本题未答完，无法查看后面的题");
            } else {
                //TODO ????当前题？ 如果是，则不应该跳转
//                SkipToQuestion(position);
            }
        }
        LogUtil.i("all = " + mSelectedQuestionList.toString() + "||" + mSelectedAnswerList.toString());
    }

    /**
     * 跳到某题，传参数给相应控件
     */
    private void SkipToQuestion(int position) {
        mTxtCurrent.setText(mSortList.get(position - 1));

        //根据当前题型进行不同Fragment的数据装载
        toSwitch(position);
    }

    /**
     * 根据当前题型进行不同Fragment的数据装载
     */
    private void toSwitch(int position) {
        if (position == 6) {
            mFrgment = FragmentDoQuestionSort.newInstance(
                    TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
                    mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
                    local_path, local_paper_code);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
            return;
        }
        if (position == 3) {
            mFrgment = FragmentDoQuestionMulti.newInstance(
                    TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
                    mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
                    local_path, local_paper_code);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
            return;
        }
//        if (position == 2) {
//            mFrgment = FragmentDoQuestionJudge.newInstance(
//                    TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
//                    mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
//                    local_path, local_paper_code);
//            getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
//            return;
//        }
        switch (mQuestionTypeList.get(position - 1)) {
            case Constants.QUESTION_TYPE_MULTI:
                mFrgment = FragmentDoQuestionMulti.newInstance(
                        TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
                        mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
                        local_path, local_paper_code);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
                break;
            case Constants.QUESTION_TYPE_JUDGE:
                mFrgment = FragmentDoQuestionJudge.newInstance(
                        TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
                        mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
                        local_path, local_paper_code);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
                break;
            case Constants.QUESTION_TYPE_SINGEL:
                mFrgment = FragmentDoQuestionSingle.newInstance(
                        TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
                        mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
                        local_path, local_paper_code);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
                break;
            case Constants.QUESTION_TYPE_SORT:
                mFrgment = FragmentDoQuestionSort.newInstance(
                        TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
                        mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
                        local_path, local_paper_code);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
                break;
            default:
                mFrgment = FragmentDoQuestionSingle.newInstance(
                        TOTAL_QUESTION_COUNT, mSortList.get(position - 1), mMusicQuestionList.get(position - 1),
                        mQuestionIdList.get(position - 1), mQuestionContentList.get(position - 1),
                        local_path, local_paper_code);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, mFrgment).commit();
                break;
        }
    }
}