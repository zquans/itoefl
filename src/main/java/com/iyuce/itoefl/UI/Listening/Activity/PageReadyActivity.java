package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;

import java.io.File;

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
        //打开默认在database中的SQl数据库
        //SQLiteDatabase  = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
        //打开指定下载的文件，.sqlite结尾的数据库格式

        /**
         * 已解压过该操作
         */
        //从sharePreferences获取路径
        String SdPath = PreferenceUtil.getSharePre(this).getString("SdPath", "");
        String sqlitePath = SdPath + "/1402.sqlite";
        LogUtil.i("sqlitePath = " + sqlitePath);

        File file = new File(sqlitePath);
        SQLiteDatabase mDatabase = SQLiteDatabase.openOrCreateDatabase(file, null);
        Cursor mCursor = mDatabase.query("lyric", null, "id>?", new String[]{"0"}, null, null, "id desc");
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                LogUtil.i(mCursor.getString(mCursor.getColumnIndex("id")));
                LogUtil.i(mCursor.getString(mCursor.getColumnIndex("content")));
            }
            mCursor.close();
        }
        mDatabase.close();
    }
}