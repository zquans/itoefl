package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;

/**
 * Created by LeBang on 2017/1/24
 */
public class PageReadyActivity extends BaseActivity {

    private TextView mTxtHeadTitle, mTxtEnglish, mTxtChinese, mTxtCategory, mTxtLevel;
    private String local_paper_code, local_path, local_music_question;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_ready);

        initView();
    }

    private void initView() {
        local_path = getIntent().getStringExtra("local_path");
        //不从表中查，直接拼装上一级的section和module
        local_paper_code = getIntent().getStringExtra("local_section") + "_" + getIntent().getStringExtra("local_module");
        //主音频名称
        local_music_question = getIntent().getStringExtra("local_music_question");

        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTxtHeadTitle = (TextView) findViewById(R.id.txt_header_title_item);
        mTxtEnglish = (TextView) findViewById(R.id.txt_activity_page_ready_title_english);
        mTxtChinese = (TextView) findViewById(R.id.txt_activity_page_ready_title_chinese);
        mTxtCategory = (TextView) findViewById(R.id.txt_activity_page_ready_title_category);
        mTxtLevel = (TextView) findViewById(R.id.txt_activity_page_ready_title_level);

        mTxtHeadTitle.setText(local_paper_code);
    }

    public void beginPractice(View view) {
        Intent intent = new Intent(this, DoQuestionReadyActivity.class);
        intent.putExtra("local_path", local_path);
        intent.putExtra(Constants.PaperCode, local_paper_code);
        intent.putExtra(Constants.MusicQuestion, local_music_question);
        startActivity(intent);
    }

    public void beginListen(View view) {
//        DbHelper mHelper = DbUtil.getHelper(this, "/storage/emulated/0/ITOEFL_JSON/user.db", Constants.DATABASE_VERSION);
//        SQLiteDatabase mDatabase1 = mHelper.getWritableDatabase();
//        LogUtil.i("long = insert " + DbUtil.insert(mDatabase1, Constants.TABLE_USER, "dream", "free"));
//        LogUtil.i("int update = " + DbUtil.update(mDatabase1, Constants.TABLE_USER, "dream", "eating", "_id >5"));
//        LogUtil.i("int delete = " + DbUtil.delete(mDatabase1, Constants.TABLE_USER, "dream = ", new String[]{"eating"}));
//        DbUtil.createTable(mDatabase1, Constants.TABLE_USER, "people", "dream");
//        LogUtil.i("list = column_1" + DbUtil.queryToArrayList(mDatabase1, Constants.TABLE_USER, null, 0).toString());
//        LogUtil.i("list = dream" + DbUtil.queryToArrayList(mDatabase1, Constants.TABLE_USER, null, "dream").toString());
//        mDatabase1.close();
    }
}