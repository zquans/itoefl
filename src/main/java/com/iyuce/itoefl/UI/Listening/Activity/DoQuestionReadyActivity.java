package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;

import java.io.File;

/**
 * Created by LeBang on 2017/1/24
 */
public class DoQuestionReadyActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mImageButton;
    private Button mButtonBegin;
    private boolean isPlay = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_question_ready);

        initView();
    }

    private void initView() {
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        mImageButton = (ImageButton) findViewById(R.id.imgbtn_activity_do_question_ready_media);
        mButtonBegin = (Button) findViewById(R.id.btn_activity_do_question_ready_begin);
        mImageButton.setOnClickListener(this);
        mButtonBegin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_header_title:
                finish();
                break;
            case R.id.btn_activity_do_question_ready_begin:
                startActivity(new Intent(this, DoQuestionActivity.class));
                break;
            case R.id.imgbtn_activity_do_question_ready_media:
                if (isPlay) {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_pause);
                } else {
                    mImageButton.setBackgroundResource(R.mipmap.icon_media_play);
                }
                isPlay = !isPlay;
                //TODO 成功打开载入的数据库
                //打开默认在database中的SQl数据库
                //SQLiteDatabase  = openOrCreateDatabase("aipu.db", MODE_PRIVATE, null);
                //打开指定下载的文件，.sqlite结尾的数据库格式
                File file_ = new File("/storage/emulated/0/download/le/1402.sqlite");
                SQLiteDatabase mDatabase = SQLiteDatabase.openOrCreateDatabase(file_, null);
                Cursor mCursor = mDatabase.query("lyric", null, "id>?", new String[]{"0"}, null, null, "id desc");
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        LogUtil.i(mCursor.getString(mCursor.getColumnIndex("id")));
                        LogUtil.i(mCursor.getString(mCursor.getColumnIndex("content")));
                    }
                    mCursor.close();
                }
                mDatabase.close();
                break;
        }
    }
}