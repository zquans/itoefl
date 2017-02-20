package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class DoQuestionActivity extends BaseActivity implements View.OnClickListener,
        FragmentDoQuestion.OnFragmentInteractionListener, BottomDoQuestionAdapter.OnButtonItemClickListener {

    private TextView mTxtTimer, mTxtReview, mTxtNext, mTxtCurrent, mTxtTotal;
    private ImageButton mImgClose;

    private BottomSheetDialog mBottomDialog;
    private RecyclerView mRecyclerView;
    private ArrayList<String> mDataBottomList = new ArrayList<>();
    private BottomDoQuestionAdapter mAdapter;

    //TODO 总题量应该等于mSortList的长度
//    private static final int TOTAL_QUESTION_COUNT = 5;
    private ArrayList<String> mSortList;
    private ArrayList<String> mMusicQuestionList;
    private ArrayList<String> mQuestionIdList;

    //当前题
    private int mCurrentQuestion = 1;

    //保存所选答案的题号和内容
    private ArrayList<Integer> mSelectedQuestionList = new ArrayList<>();
    private ArrayList<String> mSelectedAnswerList = new ArrayList<>();

    //路径
    private String local_paper_code, local_path, local_music_question;

    @Override
    public void onBackPressed() {
        doBackPageReady();
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
        mTxtTimer.setText("用时0:48");
        mImgClose = (ImageButton) findViewById(R.id.imgbtn_header_title);
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
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, local_path + "/TPO18_L1.sqlite", Constants.DATABASE_VERSION).getWritableDatabase();
        mSortList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_QUESTION, null, Constants.Sort);
        mMusicQuestionList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_QUESTION, null, Constants.MusicQuestion);
        mQuestionIdList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_QUESTION, null, Constants.QuestionId);
        mDatabase.close();

        for (int i = 0; i < mSortList.size(); i++) {
            mDataBottomList.add(mSortList.get(i));
        }
        //应该传递给Fragment的参数  QuestionId(用于在Fragment中继续查表)、Sort题号、MusicQuestion音频
        FragmentDoQuestion frgment = FragmentDoQuestion.newInstance(mSortList.get(0), mMusicQuestionList.get(0), mQuestionIdList.get(0), local_path);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, frgment).commit();
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
                //保存或替换当前题号和所选答案
                if (mSelectedQuestionList.contains(mCurrentQuestion)) {
                    mSelectedAnswerList.set(mCurrentQuestion - 1, ((int) (Math.random() * 5)) + "");
                } else {
                    mSelectedQuestionList.add(mCurrentQuestion);
                    mSelectedAnswerList.add(((int) (Math.random() * 5)) + "");
                }
                LogUtil.i("all = " + mSelectedQuestionList.toString() + "||" + mSelectedAnswerList.toString());
                //答完,进入下一个页面
                if (mSelectedQuestionList.size() == mSortList.size()) {
                    Intent intent = new Intent(this, DoResultActivity.class);
                    intent.putExtra(Constants.PaperCode, local_paper_code);
                    intent.putExtra("local_path", local_path);
                    intent.putExtra(Constants.MusicQuestion, local_music_question);
                    startActivity(intent);
                    LogUtil.i("all done " + mSelectedQuestionList.toString() + "||" + mSelectedAnswerList.toString());
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBottomClick(int position) {
        mBottomDialog.dismiss();
        //TODO 保存所作题目和对应答案,暂不存入数据库，当所有题目答完，将ArrayList.toString存入数据库
        if (mSelectedQuestionList.contains(position)) {
            mSelectedAnswerList.set(position - 1, ((int) (Math.random() * 5)) + "");

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
     *
     * @param position
     */
    private void SkipToQuestion(int position) {
        mTxtCurrent.setText(mSortList.get(position - 1));
        FragmentDoQuestion frgment = FragmentDoQuestion
                .newInstance(mSortList.get(position - 1), mMusicQuestionList.get(position - 1), mQuestionIdList.get(position - 1), local_path);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, frgment).commit();
    }
}