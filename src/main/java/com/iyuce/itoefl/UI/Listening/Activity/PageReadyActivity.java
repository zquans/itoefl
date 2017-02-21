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
import com.iyuce.itoefl.Utils.ZipUtil;

import java.io.File;
import java.util.List;

/**
 * Created by LeBang on 2017/1/24
 */
public class PageReadyActivity extends BaseActivity {

    private TextView mTxtHeadTitle, mTxtEnglish, mTxtChinese, mTxtCategory, mTxtLevel;
    private String local_paper_rule_id, local_paper_code, local_path, local_music_question, local_sqlite_path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_ready);

        initView();
    }

    private void initView() {
        local_path = getIntent().getStringExtra("local_path");
        LogUtil.i("local_path = " + local_path);
        local_sqlite_path = unZipFile(new File(local_path + "/TPO18L1.zip"));
        LogUtil.i("local_sqlite_path = " + local_sqlite_path);
        //通过这个根库的PaperRule表中的Id值，查子库的PaperRule表的字段
        local_paper_rule_id = getIntent().getStringExtra("local_paper_rule_id");
        //不从表中查，直接拼装上一级的section和module
        local_paper_code = getIntent().getStringExtra("local_section") + "_" + getIntent().getStringExtra("local_module");

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

        //TODO 动态打开指定数据库，这里应该是翔哥还没同步除L1外的数据造成错误
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, local_path + "/" + local_paper_code + ".sqlite",
                Constants.DATABASE_VERSION).getWritableDatabase();
        local_music_question = DbUtil.queryToString(mDatabase, Constants.TABLE_PAPER_RULE, Constants.MusicQuestion, Constants.ID, local_paper_rule_id);
        mTxtHeadTitle.setText(local_paper_code);
        mDatabase.close();
    }

    /**
     * UnZip解压文件夹
     */
    private String unZipFile(File file) {
        String mSQLitePath = null;
        List<File> mList;
        try {
            mList = ZipUtil.GetFileList(file.getAbsolutePath(), true, true);
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getName().contains("sqlite")) {
                    //拿出数据库文件的路径
                    mSQLitePath = local_path + "/" + mList.get(i).getName();
                }
//                LogUtil.i("mList = " + mList.get(i).getName());
            }
            //解压zip文件到对应路径
            ZipUtil.UnZipFolder(file.getAbsolutePath(), local_path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mSQLitePath;
    }

    public void beginPractice(View view) {
        Intent intent = new Intent(this, DoQuestionReadyActivity.class);
        intent.putExtra(Constants.PaperCode, local_paper_code);
        intent.putExtra("local_path", local_path);
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