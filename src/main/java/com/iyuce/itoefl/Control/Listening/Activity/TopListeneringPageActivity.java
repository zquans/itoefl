package com.iyuce.itoefl.Control.Listening.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Control.Listening.Adapter.TopListeneringPageAdapter;
import com.iyuce.itoefl.Model.UserOprate;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.HttpUtil;
import com.iyuce.itoefl.Utils.Interface.Http.DownLoadInterface;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.NetUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;
import com.iyuce.itoefl.Utils.ToastUtil;
import com.iyuce.itoefl.Utils.ZipUtil;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

public class TopListeneringPageActivity extends BaseActivity implements TopListeneringPageAdapter.OnPageItemClickListener {

    private RecyclerView mRecyclerView;
    private TopListeneringPageAdapter mAdapter;
    private ArrayList<UserOprate> mUserOprateList = new ArrayList<>();

    private TextView mTxtFinish, mTxtTotal;

    //传递来的参数
    private String local_code, local_title, from_where, local_practiced_count;

    //保存根数据库的路径
    private String root_path, local_path;

    //自创的SQL库，路径
    private String downloaded_sql_path;

    @Override
    protected void onRestart() {
        super.onRestart();
        mUserOprateList.clear();
        initData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
        }

        initView();
    }

    private void initView() {
        //网络已连接
        if (NetUtil.isConnected(this)) {
            //但不是WIFI
            if (!NetUtil.isWifi(this)) {
                ToastUtil.showMessage(this, "当前不在WIFI环境，下载将消耗较多流量");
            }
        } else {
            ToastUtil.showMessage(this, "未连接网络");
        }

        local_code = getIntent().getStringExtra("local_code");
        local_title = getIntent().getStringExtra("local_title");
        from_where = getIntent().getStringExtra("from_where");
        TextView textView = (TextView) findViewById(R.id.txt_activity_top_listenering_title);
        textView.setText(local_title);

        mTxtFinish = (TextView) findViewById(R.id.txt_activity_top_listenering_finish);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_top_listenering_total);

        //初始化数据列表
        initData();
        //将数据填充到对应视图
        initUI();
    }

    private void initUI() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_top_listenering_page);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new TopListeneringPageAdapter(this, mUserOprateList);
        mAdapter.setOnPageItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mTxtFinish.setText(getString(R.string.page_practiced, local_practiced_count));
        mTxtTotal.setText(getString(R.string.page_total, mUserOprateList.size()));
    }

    private void initData() {
        //初始化列表数据,从主表中获取到的local_code读取PAPER_RULE表中的RuleName字段
        root_path = SDCardUtil.getExercisePath() + File.separator + Constants.SQLITE_TPO;
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, root_path).getWritableDatabase();
        if (TextUtils.equals(from_where, Constants.MODULE)) {
            Cursor cursor = mDatabase.query(Constants.TABLE_PAPER_RULE, null, Constants.PaperCode + " =? ", new String[]{local_code}, null, null, Constants.PaperCode + "," + Constants.Sort + " ASC");
            if (cursor != null) {
                UserOprate mUserOprate;
                while (cursor.moveToNext()) {
                    mUserOprate = new UserOprate();
                    mUserOprate.section = cursor.getString(cursor.getColumnIndex(Constants.PaperCode));
                    mUserOprate.module = cursor.getString(cursor.getColumnIndex(Constants.RuleName));
                    mUserOprate.downTime = cursor.getString(cursor.getColumnIndex(Constants.DownTime));
                    mUserOprate.downUrl = cursor.getString(cursor.getColumnIndex(Constants.DownUrl));
                    mUserOprate.musicQuestion = cursor.getString(cursor.getColumnIndex(Constants.MusicQuestion));
                    mUserOprateList.add(mUserOprate);
                }
                cursor.close();
            }
        } else {
            Cursor cursor = mDatabase.query(Constants.TABLE_PAPER_RULE, null, Constants.ClassCode + " =? ", new String[]{local_code}, null, null, Constants.PaperCode + "," + Constants.Sort + " ASC");
            if (cursor != null) {
                UserOprate mUserOprate;
                while (cursor.moveToNext()) {
                    mUserOprate = new UserOprate();
                    mUserOprate.section = cursor.getString(cursor.getColumnIndex(Constants.PaperCode));
                    mUserOprate.module = cursor.getString(cursor.getColumnIndex(Constants.RuleName));
                    mUserOprate.downTime = cursor.getString(cursor.getColumnIndex(Constants.DownTime));
                    mUserOprate.downUrl = cursor.getString(cursor.getColumnIndex(Constants.DownUrl));
                    mUserOprate.musicQuestion = cursor.getString(cursor.getColumnIndex(Constants.MusicQuestion));
                    mUserOprateList.add(mUserOprate);
                }
                cursor.close();
            }
        }
        mDatabase.close();
        LogUtil.e("mUserOprateList.get(0) = " + mUserOprateList.get(0).downTime + mUserOprateList.get(0).downUrl);

        //初始化用户操作数据库(打开或创建)
        CreateOrOpenDbTable();
        mTxtFinish.setText("已练习 ：" + local_practiced_count + " 篇");
    }

    /**
     * 打开或者新建一个我创建的本地数据库，查询是否有表,无表则新建一张DownLoad清单表
     */
    private void CreateOrOpenDbTable() {
        //打开或者创建我的本地数据库DOWNLOAD
        downloaded_sql_path = SDCardUtil.getExercisePath() + File.separator + Constants.SQLITE_DOWNLOAD;
        SQLiteDatabase mDatabase = DbUtil.getHelper(TopListeneringPageActivity.this, downloaded_sql_path).getWritableDatabase();
        String create = "create table if not exists " + Constants.TABLE_ALREADY_DOWNLOAD + "("
                + Constants.ID + " integer primary key autoincrement,"
                + Constants.UserId + " text,"
                + Constants.SECTION + " text,"
                + Constants.MODULE + " text,"
                + Constants.LOADING + " text,"
                + Constants.DOWNLOAD + " text,"
                + Constants.DownTime + " text,"
                + Constants.Practiced + " text)";
        mDatabase.execSQL(create);
        String query;
        for (int i = 0; i < mUserOprateList.size(); i++) {
            query = "select " + Constants.DOWNLOAD + " from " + Constants.TABLE_ALREADY_DOWNLOAD + " where " + Constants.SECTION + " = ? and " + Constants.MODULE + " = ?";
            mUserOprateList.get(i).download = DbUtil.cursorToNotNullString(mDatabase.rawQuery(query, new String[]{mUserOprateList.get(i).section, mUserOprateList.get(i).module}));
            query = "select " + Constants.LOADING + " from " + Constants.TABLE_ALREADY_DOWNLOAD + " where " + Constants.SECTION + " = ? and " + Constants.MODULE + " = ?";
            mUserOprateList.get(i).loading = DbUtil.cursorToNotNullString(mDatabase.rawQuery(query, new String[]{mUserOprateList.get(i).section, mUserOprateList.get(i).module}));
            query = "select " + Constants.Practiced + " from " + Constants.TABLE_ALREADY_DOWNLOAD + " where " + Constants.SECTION + " = ? and " + Constants.MODULE + " = ?";
            mUserOprateList.get(i).practiced = DbUtil.cursorToNotNullString(mDatabase.rawQuery(query, new String[]{mUserOprateList.get(i).section, mUserOprateList.get(i).module}));
        }
        //TODO 这里的查询好像有问题,因为local_code参数并无法查出分类时的情况
        String sql_practiced_count = "SELECT COUNT(*) FROM " + Constants.TABLE_ALREADY_DOWNLOAD + " WHERE " + Constants.SECTION + " =? and " + Constants.Practiced + " =?";
        local_practiced_count = DbUtil.cursorToString(mDatabase.rawQuery(sql_practiced_count, new String[]{local_code, Constants.TRUE}));
        mDatabase.close();
    }

    /**
     * 给定几个参数，position,Url,path
     */
    private void doDownLoad(final int pos, String url, final String path) {
        HttpUtil.downLoad(url, path, new DownLoadInterface() {
            @Override
            public void onBefore() {
                mUserOprateList.get(pos).loading = Constants.TRUE;
                mRecyclerView.getChildAt(pos).setClickable(false);

                ImageView imgDownload = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                ImageView imgReady = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download_ready);
                imgDownload.setVisibility(View.VISIBLE);
                imgReady.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAfter() {
                mRecyclerView.getChildAt(pos).setClickable(true);
            }

            @Override
            public void inProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                textView.setVisibility(View.VISIBLE);
                textView.setText(((int) (progress * 100) + "%"));
            }

            @Override
            public void doSuccess(File file, Call call, Response response) {
                mUserOprateList.get(pos).loading = Constants.FALSE;
                mUserOprateList.get(pos).download = Constants.TRUE;
                SQLiteDatabase mDatabase = DbUtil.getHelper(TopListeneringPageActivity.this, downloaded_sql_path).getWritableDatabase();
                //先删除，再添加，避免重复
                String sql_delete = "DELETE FROM " + Constants.TABLE_ALREADY_DOWNLOAD + " WHERE " + Constants.SECTION
                        + " = \"" + mUserOprateList.get(pos).section + "\" AND " + Constants.MODULE + " = \"" + mUserOprateList.get(pos).module + "\";";
                LogUtil.e("sql_delete = " + sql_delete);
                mDatabase.execSQL(sql_delete);
                ContentValues mValues = new ContentValues();
                mValues.put(Constants.UserId, "user_default");
                mValues.put(Constants.SECTION, mUserOprateList.get(pos).section);
                mValues.put(Constants.MODULE, mUserOprateList.get(pos).module);
                mValues.put(Constants.DOWNLOAD, Constants.TRUE);
                mValues.put(Constants.LOADING, Constants.FALSE);
                mValues.put(Constants.DownTime, mUserOprateList.get(pos).downTime);
                DbUtil.insert(mDatabase, Constants.TABLE_ALREADY_DOWNLOAD, mValues);
                mDatabase.close();

                //解压文件夹
                unZipFile(file, path);

                //下载箭头、文字设为不可见
                ImageView imgDownload = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                TextView textDownload = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                TextView textState = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_state);
                textDownload.setVisibility(View.INVISIBLE);
//                textDownload.setText("未练习");
                imgDownload.setVisibility(View.INVISIBLE);
                textState.setVisibility(View.INVISIBLE);
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
    public void OnPageItemClick(final int pos) {
        //这个路径用来存放下载的文件，或者传递给下一级
        local_path = SDCardUtil.getExercisePath() + File.separator + mUserOprateList.get(pos).section + File.separator + mUserOprateList.get(pos).module;

        //查询download库中的下载表,判断是否下载到本地了，是则进入，否则下载
        SQLiteDatabase mDatabase = DbUtil.getHelper(TopListeneringPageActivity.this, downloaded_sql_path).getWritableDatabase();
        String sql_query = "select " + Constants.ID + " from " + Constants.TABLE_ALREADY_DOWNLOAD
                + " where " + Constants.SECTION + " = ? and " + Constants.MODULE + " = ? ";
        String isExist = DbUtil.cursorToString(mDatabase.rawQuery(sql_query, new String[]{mUserOprateList.get(pos).section, mUserOprateList.get(pos).module}));
        mDatabase.close();
        LogUtil.i(mUserOprateList.get(pos).section + "_" + mUserOprateList.get(pos).module + " isExist = " + isExist);
        if (isExist.equals(Constants.NONE)) {
            doDownLoad(pos, mUserOprateList.get(pos).downUrl, local_path);
            return;
        }

        //如果新表修改了，则提示用户是否下载新表
        String path = SDCardUtil.getExercisePath();
        String filePath = path + File.separator + Constants.SQLITE_DOWNLOAD;
        SQLiteDatabase database = DbUtil.getHelper(TopListeneringPageActivity.this, filePath).getWritableDatabase();
        String sql_down_time_last = "select " + Constants.DownTime + " from " + Constants.TABLE_ALREADY_DOWNLOAD
                + " where " + Constants.SECTION + " = ? and " + Constants.MODULE + " = ? ";
        String down_time_last = DbUtil.cursorToString(database.rawQuery(sql_down_time_last, new String[]{mUserOprateList.get(pos).section, mUserOprateList.get(pos).module}));
        database.close();
        LogUtil.e("down_time_last = " + down_time_last);
        if (!down_time_last.equals(mUserOprateList.get(pos).downTime)) {
            new AlertDialog.Builder(this).setMessage("有更新的题库，建议下载新题库")
                    .setPositiveButton("使用原题库", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(TopListeneringPageActivity.this, PageReadyActivity.class);
                            intent.putExtra("local_path", local_path);
                            //留给子数据库拼装末位路径
                            intent.putExtra("local_code", mUserOprateList.get(pos).section);
                            intent.putExtra("local_module", mUserOprateList.get(pos).module);
                            intent.putExtra("local_music_question", mUserOprateList.get(pos).musicQuestion);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("下载新题库", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //下载新题
                            doDownLoad(pos, mUserOprateList.get(pos).downUrl, local_path);
                        }
                    }).show();
        } else {
            Intent intent = new Intent(TopListeneringPageActivity.this, PageReadyActivity.class);
            intent.putExtra("local_path", local_path);
            //留给子数据库拼装末位路径
            intent.putExtra("local_code", mUserOprateList.get(pos).section);
            intent.putExtra("local_module", mUserOprateList.get(pos).module);
            intent.putExtra("local_music_question", mUserOprateList.get(pos).musicQuestion);
            startActivity(intent);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listtening_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.download:
                ToastUtil.showMessage(this, "begin download all");
                String path;
                for (int i = 0; i < mUserOprateList.size(); i++) {
                    //查对象属性,loading或者downloaded，则不下载
                    if (mUserOprateList.get(i).loading.equals(Constants.TRUE) || mUserOprateList.get(i).download.equals(Constants.TRUE)) {
                        continue;
                    }
                    path = SDCardUtil.getExercisePath() + File.separator + mUserOprateList.get(i).section + File.separator + mUserOprateList.get(i).module;
                    doDownLoad(i, mUserOprateList.get(i).downUrl, path);
                }
                break;
            default:
                break;
        }
        return true;
    }
}