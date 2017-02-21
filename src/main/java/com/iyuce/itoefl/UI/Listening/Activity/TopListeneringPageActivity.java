package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.TopListeneringPageAdapter;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.HttpUtil;
import com.iyuce.itoefl.Utils.Interface.HttpInterface;
import com.iyuce.itoefl.Utils.LogUtil;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

public class TopListeneringPageActivity extends BaseActivity
        implements View.OnClickListener, TopListeneringPageAdapter.OnPageItemClickListener {

    private RecyclerView mRecyclerView;
    private TopListeneringPageAdapter mAdapter;
    private ArrayList<String> mDataList = new ArrayList<>();

    private TextView mTxtFinish, mTxtTotal;
    private ImageView mImgReward;
    private String mSavePath;

    private String local_section, local_paper_rule_id;
    //保存根数据库的路径
    private String root_path;
    private static final String SECTION = "section";
    private static final String MODULE = "module";
    private static final String ISDOWNLOAD = "isdownload";

    //自创的SQL库，路径
    private String downloaded_sql_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering_page);

        initView();
    }

    private void initView() {
        local_section = getIntent().getStringExtra("local_section");
        LogUtil.i("local_section = " + local_section);
        TextView textView = (TextView) findViewById(R.id.txt_header_title_item);
        textView.setText(local_section + "\r听力真题");
        findViewById(R.id.txt_header_title_menu).setOnClickListener(this);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);

        mImgReward = (ImageView) findViewById(R.id.img_activity_top_listenering_award);
        mTxtFinish = (TextView) findViewById(R.id.txt_activity_top_listenering_finish);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_top_listenering_total);
        mTxtTotal.setText("总共 : 12篇");

        //初始化列表数据,从主表中获取到的local_section读取PAPER_RULE表中的RuleName字段
        root_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                Constants.FILE_PATH_ITOEFL_EXERCISE + File.separator + Constants.SQLITE_TPO;
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, root_path, Constants.DATABASE_VERSION).getWritableDatabase();
        mDataList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_RULE, Constants.RuleName, Constants.PaperCode, local_section);
        mDatabase.close();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_top_listenering_page);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TopListeneringPageAdapter(this, mDataList);
        mAdapter.setOnPageItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        //初始化数据库(打开或创建)
        CreateOrOpenDbTable();
    }

    /**
     * 打开或者新建一个我创建的本地数据库，查询是否有表,无表则新建一张DownLoad清单表
     */
    private void CreateOrOpenDbTable() {
        //打开或者创建我的本地数据库DOWNLOAD
        downloaded_sql_path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + Constants.FILE_PATH_ITOEFL_EXERCISE + File.separator + Constants.SQLITE_DOWNLOAD;
        SQLiteDatabase mDatabase = DbUtil
                .getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
        //无表则创建表
        String isNone = DbUtil.queryToString(mDatabase, Constants.TABLE_SQLITE_MASTER, "name", Constants.TABLE_NAME, Constants.TABLE_ALREADY_DOWNLOAD);
        if (TextUtils.equals(isNone, Constants.NONE)) {
            //本地表的字段
            String create = "create table " + Constants.TABLE_ALREADY_DOWNLOAD
                    + "(Id integer primary key autoincrement," + SECTION + " text," + MODULE + " text," + ISDOWNLOAD + " text)";
            mDatabase.execSQL(create);
        }
        mDatabase.close();
    }

    /**
     * 给定几个参数，position,Url,path
     */
    private void doDownLoad(final int pos, final String path) {
        HttpUtil.downLoad(pos, path, new HttpInterface() {
            @Override
            public void inProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                if (pos != -1) {
                    ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                    imgview.setBackgroundResource(R.mipmap.icon_download_finish);
                    TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(((int) (progress * 100) + "%"));
                }
            }

            @Override
            public void doSuccess(File file, Call call, Response response) {
                //下载到最末路径
                SQLiteDatabase mDatabase = DbUtil
                        .getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
                ContentValues mValues = new ContentValues();
                mValues.put(SECTION, local_section);
                mValues.put(MODULE, mDataList.get(pos));
                mValues.put(ISDOWNLOAD, "true");
                DbUtil.insert(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, mValues);
                LogUtil.i(DbUtil.queryToArrayList(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, null, MODULE).toString());
                mDatabase.close();
                if (pos != -1) {
                    //下载箭头、文字设为不可见
                    ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                    imgview.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });

//        OkGo.get("http://img.enhance.cn/toefl/zip/listenaudiozip/1402.zip")
//                .execute(new FileCallback(path, "") {
//                    @Override
//                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
//                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
//                        LogUtil.i(currentSize + "||" + totalSize + "||" + progress);
//                        if (pos != -1) {
//                            ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
//                            imgview.setBackgroundResource(R.mipmap.icon_download_finish);
//                            TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
//                            textView.setVisibility(View.VISIBLE);
//                            textView.setText(((int) (progress * 100) + "%"));
//                        }
//                    }
//
//                    @Override
//                    public void onSuccess(File file, Call call, Response response) {
//                        if (pos != -1) {
//                            //下载箭头、文字设为不可见
//                            ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
//                            imgview.setVisibility(View.INVISIBLE);
//                            TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
//                            textView.setVisibility(View.INVISIBLE);
//                        }
//                    }
//                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_header_title_menu:
                for (int i = 0; i < mDataList.size(); i++) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_PATH_ITOEFL_EXERCISE
                            + File.separator + local_section + File.separator + mDataList.get(i);
                    doDownLoad(i, path);
                }
                break;
            case R.id.imgbtn_header_title:
                finish();
                break;
        }
    }

    @Override
    public void OnPageItemClick(int pos) {
        mImgReward.setBackgroundResource(R.mipmap.icon_reward_finish);
        SQLiteDatabase mDatabase1 = DbUtil.getHelper(this, root_path, Constants.DATABASE_VERSION).getWritableDatabase();
        local_paper_rule_id = DbUtil.queryToString(mDatabase1, Constants.TABLE_PAPER_RULE, Constants.ID, Constants.RuleName, mDataList.get(pos));
        mDatabase1.close();
//        LogUtil.i("local_paper_rule_id = " + local_paper_rule_id);

        //关键是末位路径
        mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_PATH_ITOEFL_EXERCISE
                + File.separator + local_section + File.separator + mDataList.get(pos);

        //从List的属性中，判断是否下载到本地了，是则进入，否则下载//TODO (或者判断路径中是否存在该文件)
        SQLiteDatabase mDatabase = DbUtil
                .getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
        String isExist = DbUtil.queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, Constants.ID, MODULE, mDataList.get(pos));
        LogUtil.i("isExist = " + isExist);
        if (isExist.equals(Constants.NONE)) {
            mDatabase.close();
            doDownLoad(pos, mSavePath);
            return;
        }
        mDatabase.close();
        Intent intent = new Intent(this, PageReadyActivity.class);
        intent.putExtra("local_path", mSavePath);
        intent.putExtra("local_paper_rule_id", local_paper_rule_id);
        //给子数据库拼装末位路径
        intent.putExtra("local_section", local_section);
        intent.putExtra("local_module", mDataList.get(pos));
        startActivity(intent);
    }
}