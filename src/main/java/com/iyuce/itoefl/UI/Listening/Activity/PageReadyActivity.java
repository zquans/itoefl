package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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

    private TextView mTxtEnglish, mTxtChinese, mTxtCategory, mTxtLevel;
    private String local_path, local_sqlite_path;

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
        intent.putExtra("local_path", local_path);
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

        if (!TextUtils.isEmpty(local_sqlite_path)) {
            SQLiteDatabase mDatabase = DbUtil.getHelper(this, local_sqlite_path, Constants.DATABASE_VERSION).getWritableDatabase();
            LogUtil.i(DbUtil.queryToArrayList(mDatabase, "lyric", null, 0).toString());
            mDatabase.close();
        }
    }
}