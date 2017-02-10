package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.BottomDoQuestionAdapter;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentDoQuestion;
import com.iyuce.itoefl.Utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class DoQuestionActivity extends AppCompatActivity implements View.OnClickListener,
        FragmentDoQuestion.OnFragmentInteractionListener, BottomDoQuestionAdapter.OnButtonItemClickListener {

    private TextView mTxtTimer, mTxtReview, mTxtNext, mTxtCurrent, mTxtTotal;
    private ImageButton mImgClose;

    private BottomSheetDialog mBottomDialog;
    private RecyclerView mRecyclerView;
    private ArrayList<String> mDataBottomList = new ArrayList<>();
    private BottomDoQuestionAdapter mAdapter;

    private ArrayList<HashMap> mSelectedAnswerList = new ArrayList<>();

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
        mImgClose = (ImageButton) findViewById(R.id.imgbtn_header_title);
        mTxtTimer = (TextView) findViewById(R.id.txt_header_title_menu);
        mTxtTimer.setText("用时0:48");
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

        //TODO BottomSheet数据源
        for (int i = 1; i < 6; i++) {
            mDataBottomList.add(i + "");
        }
        FragmentDoQuestion frgment = FragmentDoQuestion.newInstance("1");
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
                startActivity(new Intent(this, DoResultActivity.class));
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
        mTxtCurrent.setText(position + "");
        FragmentDoQuestion frgment = FragmentDoQuestion.newInstance(position + "");
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_do_question, frgment).commit();

        //TODO 保存所作题目和对应答案,暂不存入数据库，当所有题目答完，将ArrayList.toString存入数据库
        HashMap map = new HashMap();
        map.put(position, (int) (Math.random() * 5));
        mSelectedAnswerList.add(map);
        LogUtil.i("all = " + mSelectedAnswerList.toString());

        //TODO 取最后答题结果值的方法
        for (int i = 0; i < mSelectedAnswerList.size(); i++) {
            String key = mSelectedAnswerList.get(i).keySet().toString();
            String value = mSelectedAnswerList.get(i).values().toString();
//            LogUtil.i(key + " = " + value);
        }
    }
}