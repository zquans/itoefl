package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;

/**
 * Created by LeBang on 2017/1/24
 */
public class PageReadyActivity extends BaseActivity {

    private TextView mTxtEnglish, mTxtChinese, mTxtCategory, mTxtLevel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_ready);

        initView();
    }

    private void initView() {
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTxtEnglish = (TextView) findViewById(R.id.txt_activity_page_ready_title_english);
        mTxtChinese = (TextView) findViewById(R.id.txt_activity_page_ready_title_chinese);
        mTxtCategory = (TextView) findViewById(R.id.txt_activity_page_ready_title_category);
        mTxtLevel = (TextView) findViewById(R.id.txt_activity_page_ready_title_level);
    }

    public void beginPractice(View view) {
        startActivity(new Intent(this, DoQuestionReadyActivity.class));
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

        /**
         * 已解压过该操作
         */
        //从sharePreferences获取路径
        String SdPath = PreferenceUtil.getSharePre(this).getString("SdPath", "");
        String sqlitePath = SdPath + "/1402.sqlite";
        LogUtil.i("sqlitePath = " + sqlitePath);
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, sqlitePath, Constants.DATABASE_VERSION).getWritableDatabase();
        LogUtil.i(DbUtil.queryToArrayList(mDatabase, "lyric", null, 0).toString());
        mDatabase.close();
    }
}