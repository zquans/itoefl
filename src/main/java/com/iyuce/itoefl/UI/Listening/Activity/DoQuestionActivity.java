package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iyuce.itoefl.R;

public class DoQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTxtTimer, mTxtNext;
    private ImageButton mImgClose;

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
        mTxtNext = (TextView) findViewById(R.id.txt_activity_do_question_next);
        mImgClose = (ImageButton) findViewById(R.id.imgbtn_header_title);
        mTxtTimer = (TextView) findViewById(R.id.txt_header_title_menu);
        mTxtTimer.setText("用时0:48");
        mImgClose.setBackgroundResource(R.mipmap.icon_close);

        mImgClose.setOnClickListener(this);
        mTxtNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                doBackPageReady();
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
}