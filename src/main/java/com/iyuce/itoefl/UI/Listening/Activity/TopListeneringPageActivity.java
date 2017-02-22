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
import com.iyuce.itoefl.Model.UserOprate;
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
    //列表对象，包含两个库中查询的数据
    private ArrayList<UserOprate> mUserOprateList = new ArrayList<>();
    private ArrayList<String> mModuleList = new ArrayList<>();
    private ArrayList<String> mLoadingList = new ArrayList<>();
    private ArrayList<String> mDownloadList = new ArrayList<>();

    private TextView mTxtFinish, mTxtTotal;
    private ImageView mImgReward;

    private String local_section;

    //保存根数据库的路径
    private String root_path;

    //自创的SQL库，路径
    private String downloaded_sql_path;

    //自建的表中的column字段
    private static final String SECTION = "Section";
    private static final String MODULE = "Module";
    private static final String DOWNLOAD = "Download";
    private static final String LOADING = "Loading";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering_page);

        initView();
    }

    private void initView() {
        local_section = getIntent().getStringExtra("local_section");
        TextView textView = (TextView) findViewById(R.id.txt_header_title_item);
        textView.setText(local_section + "\r听力真题");
        findViewById(R.id.txt_header_title_menu).setOnClickListener(this);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);

        mImgReward = (ImageView) findViewById(R.id.img_activity_top_listenering_award);
        mTxtFinish = (TextView) findViewById(R.id.txt_activity_top_listenering_finish);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_top_listenering_total);
        mTxtFinish.setText("已练习 ：１篇");
        mTxtTotal.setText("总共 :  6 篇");

        //初始化列表数据,从主表中获取到的local_section读取PAPER_RULE表中的RuleName字段
        root_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                Constants.FILE_PATH_ITOEFL_EXERCISE + File.separator + Constants.SQLITE_TPO;
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, root_path, Constants.DATABASE_VERSION).getWritableDatabase();
        mModuleList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_RULE, Constants.RuleName, Constants.PaperCode, local_section);
        mDatabase.close();
        //初始化用户操作数据库(打开或创建)
        CreateOrOpenDbTable();

        //从两个数据库中查询完成数据拼装，Adapter装载数据
        UserOprate mUserOprate;
        for (int i = 0; i < mModuleList.size(); i++) {
            mUserOprate = new UserOprate();
            mUserOprate.module = mModuleList.get(i);
            mUserOprate.download = mDownloadList.get(i);
            mUserOprate.loading = mLoadingList.get(i);
            mUserOprateList.add(mUserOprate);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_top_listenering_page);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TopListeneringPageAdapter(this, mUserOprateList);
        mAdapter.setOnPageItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
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
        //判断是否有表
        String isNone = DbUtil.queryToString(mDatabase, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_ALREADY_DOWNLOAD);
        if (TextUtils.equals(isNone, Constants.NONE)) {
            //无表则创建本地用户操作表,表中字段用常量
            String create = "create table " + Constants.TABLE_ALREADY_DOWNLOAD + "(Id integer primary key autoincrement,"
                    + SECTION + " text,"
                    + MODULE + " text,"
                    + LOADING + " text,"
                    + DOWNLOAD + " text)";
            mDatabase.execSQL(create);
        }
        for (int i = 0; i < mModuleList.size(); i++) {
            mDownloadList.add(DbUtil.
                    queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, DOWNLOAD, MODULE, mModuleList.get(i)));
            mLoadingList.add(DbUtil.
                    queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, LOADING, MODULE, mModuleList.get(i)));
        }
        mDatabase.close();
        LogUtil.i(mDownloadList.toString() + "////" + mLoadingList.toString());
    }

    /**
     * 给定几个参数，position,Url,path
     */
    private void doDownLoad(final int pos, final String path) {
        HttpUtil.downLoad(pos, path, new HttpInterface() {
            @Override
            public void onBefore() {
                mRecyclerView.getChildAt(pos).setClickable(false);
            }

            @Override
            public void onAfter() {
                mRecyclerView.getChildAt(pos).setClickable(true);
            }

            @Override
            public void inProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                if (pos != -1) {
                    mUserOprateList.get(pos).loading = "true";

                    ImageView imgDownload = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                    ImageView imgReady = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download_ready);
                    imgDownload.setVisibility(View.VISIBLE);
                    imgReady.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(((int) (progress * 100) + "%"));
                }
            }

            @Override
            public void doSuccess(File file, Call call, Response response) {
                mUserOprateList.get(pos).loading = "false";
                mUserOprateList.get(pos).download = "true";

                SQLiteDatabase mDatabase = DbUtil
                        .getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
                ContentValues mValues = new ContentValues();
                mValues.put(SECTION, local_section);
                mValues.put(MODULE, mModuleList.get(pos));
                mValues.put(DOWNLOAD, "true");
                mValues.put(LOADING, "false");
                DbUtil.insert(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, mValues);
                //TODO 用户操作表中，光查MODULE是不够的，还要加查SECTION = "TPO18" 作为筛选条件
                LogUtil.i(DbUtil.queryToArrayList(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, null, MODULE).toString());
                mDatabase.close();
                if (pos != -1) {
                    //下载箭头、文字设为不可见
                    ImageView imgDownload = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                    ImageView imgProgress = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_progress);
                    if (pos == 0) {
                        imgProgress.setBackgroundResource(R.mipmap.icon_progress_finish_first);
                    } else if (pos == mDownloadList.size() - 1) {
                        imgProgress.setBackgroundResource(R.mipmap.icon_progress_finish_last);
                    } else {
                        imgProgress.setBackgroundResource(R.mipmap.icon_progress_finish_center);
                    }
                    imgDownload.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_header_title_menu:
                for (int i = 0; i < mModuleList.size(); i++) {
                    //查对象属性,loading或者downloaded，则不下载
                    if (mUserOprateList.get(i).loading.equals("true") || mUserOprateList.get(i).download.equals("true")) {
                        continue;
                    }
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_PATH_ITOEFL_EXERCISE
                            + File.separator + local_section + File.separator + mModuleList.get(i);
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
        //奖杯图标，当全部题目完成后设为亮
        mImgReward.setBackgroundResource(R.mipmap.icon_reward_finish);

        //这个路径用来存放下载的文件，或者传递给下一级
        String local_path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_PATH_ITOEFL_EXERCISE
                + File.separator + local_section + File.separator + mModuleList.get(pos);
        //查询download库中的下载表,判断是否下载到本地了，是则进入，否则下载
        SQLiteDatabase mDatabase = DbUtil
                .getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
        //TODO 用户操作表中，光加MODULE作为条件是不够的，还要加查SECTION作为筛选条件
        String isExist = DbUtil.queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, Constants.ID, MODULE, mModuleList.get(pos));
        LogUtil.i(mModuleList.get(pos) + " isExist = " + isExist);
        if (isExist.equals(Constants.NONE)) {
            mDatabase.close();
            doDownLoad(pos, local_path);
            return;
        }
        mDatabase.close();

        //给intent的参数,根据用户所选项，获得PaperRuleName对应的PaperRuleId，以便查找下一张表用
        SQLiteDatabase mDatabase1 = DbUtil.getHelper(this, root_path, Constants.DATABASE_VERSION).getWritableDatabase();
        String local_paper_rule_id = DbUtil.queryToString(mDatabase1, Constants.TABLE_PAPER_RULE, Constants.ID, Constants.RuleName, mModuleList.get(pos));
        mDatabase1.close();

        Intent intent = new Intent(this, PageReadyActivity.class);
        intent.putExtra("local_path", local_path);
        intent.putExtra("local_paper_rule_id", local_paper_rule_id);
        //留给子数据库拼装末位路径
        intent.putExtra("local_section", local_section);
        intent.putExtra("local_module", mModuleList.get(pos));
        startActivity(intent);
    }
}