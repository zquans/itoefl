package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.iyuce.itoefl.Utils.NetUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;
import com.iyuce.itoefl.Utils.ToastUtil;
import com.iyuce.itoefl.Utils.ZipUtil;

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
    private ArrayList<String> mUrlList = new ArrayList<>();
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

        //初始化列表数据,从主表中获取到的local_section读取PAPER_RULE表中的RuleName字段
        root_path = SDCardUtil.getExercisePath() + File.separator + Constants.SQLITE_TPO;
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, root_path, Constants.DATABASE_VERSION).getWritableDatabase();
        mModuleList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_RULE, Constants.RuleName, Constants.PaperCode + " =? ", local_section);
        mUrlList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER_RULE, Constants.DownUrl, Constants.PaperCode + " =? ", local_section);
        mDatabase.close();
        LogUtil.i("Url = " + mUrlList.toString());
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

        mTxtFinish.setText("已练习 ：１篇");
        mTxtTotal.setText("总共 :  " + mModuleList.size() + " 篇");

        //网络已连接
        if (NetUtil.isConnected(this)) {
            //但不是WIFI
            if (!NetUtil.isWifi(this)) {
                ToastUtil.showMessage(this, "当前不在WIFI环境，下载将消耗较多流量");
            }
        } else {
            ToastUtil.showMessage(this, "未连接网络");
        }
    }

    /**
     * 打开或者新建一个我创建的本地数据库，查询是否有表,无表则新建一张DownLoad清单表
     */
    private void CreateOrOpenDbTable() {
        //打开或者创建我的本地数据库DOWNLOAD
        downloaded_sql_path = SDCardUtil.getExercisePath() + File.separator + Constants.SQLITE_DOWNLOAD;
        SQLiteDatabase mDatabase = DbUtil.getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
        String create = "create table if not exists " + Constants.TABLE_ALREADY_DOWNLOAD + "("
                + Constants.ID + " integer primary key autoincrement,"
                + SECTION + " text,"
                + MODULE + " text,"
                + LOADING + " text,"
                + DOWNLOAD + " text)";
        mDatabase.execSQL(create);
        String query;
        for (int i = 0; i < mModuleList.size(); i++) {
            query = "select " + DOWNLOAD + " from " + Constants.TABLE_ALREADY_DOWNLOAD + " where " + SECTION + " = ? and " + MODULE + " = ?";
            mDownloadList.add(DbUtil.cursorToNotNullString(mDatabase.rawQuery(query, new String[]{local_section, mModuleList.get(i)})));
            query = "select " + LOADING + " from " + Constants.TABLE_ALREADY_DOWNLOAD + " where " + SECTION + " = ? and " + MODULE + " = ?";
            mLoadingList.add(DbUtil.cursorToNotNullString(mDatabase.rawQuery(query, new String[]{local_section, mModuleList.get(i)})));
//            mDownloadList.add(DbUtil.queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD
//                    , new String[]{DOWNLOAD}, SECTION + " =? and " + MODULE + " =?", new String[]{local_section, mModuleList.get(i)}));
//            mLoadingList.add(DbUtil.queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD
//                    , new String[]{LOADING}, SECTION + " =? and " + MODULE + " =?", new String[]{local_section, mModuleList.get(i)}));
//            mDownloadList.add(DbUtil.queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, DOWNLOAD, MODULE, mModuleList.get(i)));
//            mLoadingList.add(DbUtil.queryToString(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, LOADING, MODULE, mModuleList.get(i)));
        }
        mDatabase.close();
        LogUtil.i(mDownloadList.toString() + "////" + mLoadingList.toString());
    }

    /**
     * 给定几个参数，position,Url,path
     */
    private void doDownLoad(final int pos, String url, final String path) {
        HttpUtil.downLoad(url, path, new HttpInterface() {
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

                SQLiteDatabase mDatabase = DbUtil.getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
                ContentValues mValues = new ContentValues();
                mValues.put(SECTION, local_section);
                mValues.put(MODULE, mModuleList.get(pos));
                mValues.put(DOWNLOAD, "true");
                mValues.put(LOADING, "false");
                DbUtil.insert(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, mValues);
                //用户操作表中，SECTION = local_section 作为筛选条件
                String sql_query = "select " + MODULE + " from " + Constants.TABLE_ALREADY_DOWNLOAD + " where " + SECTION + " = ?";
                LogUtil.i(DbUtil.cursorToArrayList(mDatabase.rawQuery(sql_query, new String[]{local_section})).toString());
                mDatabase.close();

                //解压文件夹
                unZipFile(file, path);

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

    /**
     * UnZip解压文件夹
     */
    private void unZipFile(File file, String path) {
        try {
            //解压zip文件到对应路径
            ZipUtil.UnZipFolder(file.getAbsolutePath(), path);
            //删除文件夹,有bool值返回，可以根据做操作
            LogUtil.i("zip delete = " + file.delete());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_header_title_menu:
                String url;
                String path;
                for (int i = 0; i < mModuleList.size(); i++) {
                    //查对象属性,loading或者downloaded，则不下载
                    if (mUserOprateList.get(i).loading.equals("true") || mUserOprateList.get(i).download.equals("true")) {
                        continue;
                    }
                    path = SDCardUtil.getExercisePath() + File.separator + local_section + File.separator + mModuleList.get(i);
                    url = "http://xm.iyuce.com/app/" + local_section + "_" + mModuleList.get(i) + ".zip";
                    doDownLoad(i, url, path);
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
        String local_path = SDCardUtil.getExercisePath() + File.separator + local_section + File.separator + mModuleList.get(pos);

        //查询download库中的下载表,判断是否下载到本地了，是则进入，否则下载
        SQLiteDatabase mDatabase = DbUtil.getHelper(TopListeneringPageActivity.this, downloaded_sql_path, Constants.DATABASE_VERSION).getWritableDatabase();
        String sql_query = "select " + Constants.ID + " from " + Constants.TABLE_ALREADY_DOWNLOAD
                + " where " + SECTION + " = ? and " + MODULE + " = ? ";
        String isExist = DbUtil.cursorToString(mDatabase.rawQuery(sql_query, new String[]{local_section, mModuleList.get(pos)}));
        LogUtil.i(local_section + "_" + mModuleList.get(pos) + " isExist = " + isExist);
        if (isExist.equals(Constants.NONE)) {
            mDatabase.close();
            String url = "http://xm.iyuce.com/app/" + local_section + "_" + mModuleList.get(pos) + ".zip";
            doDownLoad(pos, url, local_path);
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