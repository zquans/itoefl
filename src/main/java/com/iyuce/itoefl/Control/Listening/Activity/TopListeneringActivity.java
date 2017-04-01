package com.iyuce.itoefl.Control.Listening.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Control.Listening.Fragment.FragmentClassify;
import com.iyuce.itoefl.Control.Listening.Fragment.FragmentOrder;
import com.iyuce.itoefl.Model.Exercise.ListenModule;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

public class TopListeneringActivity extends BaseActivity {

    //导航指示器
    private TabLayout mTab;
    private ArrayList<String> mTabList = new ArrayList<>();
    private ArrayList<ListenModule> mModuleList = new ArrayList<>();

    private ViewPager mViewPager;
    private TabTopListeneringAdapter mAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

//    private CollapsingToolbarLayout mCollapLayout;

    @Override
    protected void onRestart() {
        super.onRestart();
        mModuleList.clear();
        mFragmentList.clear();
        initData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.mipmap.icon_back);
//        }
        initData();
        initView();
    }

    private void initData() {
        //路径
        String path = SDCardUtil.getExercisePath();
        String tpo_path = path + File.separator + Constants.SQLITE_TPO;
        //先拿出所有的Module名
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, tpo_path).getWritableDatabase();
        /**Classify部分*/
        String sql_query = "SELECT " + Constants.CodeName + " FROM " + Constants.TABLE_CLASS + " WHERE " + Constants.Parent + " = ? ";
        ArrayList<String> mClassifyNameList = DbUtil.cursorToArrayList(mDatabase.rawQuery(sql_query, new String[]{"0"}));
        sql_query = "SELECT " + Constants.Code + " FROM " + Constants.TABLE_CLASS + " WHERE " + Constants.Parent + " = ? ";
        ArrayList<String> mClassifyCodeList = DbUtil.cursorToArrayList(mDatabase.rawQuery(sql_query, new String[]{"0"}));
        LogUtil.e("mClassifyNameList = " + mClassifyNameList);

        //拿出数组实体属性
        ArrayList<ListenModule> mClassifyList = new ArrayList<>();
        ListenModule mClassifyModule;
        String sql_query_all = "SELECT * FROM " + Constants.TABLE_CLASS;
        Cursor cursor = mDatabase.rawQuery(sql_query_all, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mClassifyModule = new ListenModule();
                mClassifyModule.code = cursor.getString(cursor.getColumnIndex(Constants.Code));
                mClassifyModule.name = cursor.getString(cursor.getColumnIndex(Constants.CodeName));
                mClassifyModule.parent = cursor.getString(cursor.getColumnIndex(Constants.Parent));
                mClassifyList.add(mClassifyModule);
            }
            cursor.close();
        }

        /**Module部分*/
        ArrayList<String> mModuleNameList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER, Constants.PaperCode + " ASC", Constants.PaperName);
        mDatabase.close();

        String practiced_path = path + File.separator + Constants.SQLITE_DOWNLOAD;
        SQLiteDatabase databaseDownload = DbUtil.getHelper(this, practiced_path).getWritableDatabase();
        String isNone_Download = DbUtil.queryToString(databaseDownload, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_ALREADY_DOWNLOAD);
        databaseDownload.close();

        //提供module 和 progress
        ListenModule mListenModule;
        for (int i = 0; i < mModuleNameList.size(); i++) {
            mListenModule = new ListenModule();
            mListenModule.name = mModuleNameList.get(i);

            if (!TextUtils.equals(isNone_Download, Constants.NONE)) {
                SQLiteDatabase mDatabaseDownload = DbUtil.getHelper(this, practiced_path).getWritableDatabase();
                String practiced_count_sql = "SELECT COUNT(*) FROM " + Constants.TABLE_ALREADY_DOWNLOAD + " WHERE " + Constants.SECTION + " =? and " + Constants.Practiced + " =?";
                String practiced_count = DbUtil.cursorToString(mDatabaseDownload.rawQuery(practiced_count_sql, new String[]{mModuleNameList.get(i), "true"}));
                mListenModule.practiced_count = practiced_count;//拿练习过的数据数量
                mDatabaseDownload.close();
            }

//            SQLiteDatabase mDatabaseTpo = DbUtil.getHelper(this, tpo_path).getWritableDatabase();
////            String isNone_Tpo = DbUtil.queryToString(mDatabaseTpo, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_PAPER_RULE);
////            if (!TextUtils.equals(isNone_Tpo, Constants.NONE)) {
//            String total_count_sql = "SELECT COUNT(*) FROM " + Constants.TABLE_PAPER_RULE + " WHERE " + Constants.PaperCode + " =? ";
//            String total_count = DbUtil.cursorToString(mDatabaseTpo.rawQuery(total_count_sql, new String[]{nameList.get(i)}));
//            mListenModule.total_count = total_count;//拿所有的module的数据数量
////                LogUtil.i("total_count = " + total_count);
////            }
//            mDatabaseTpo.close();
            mListenModule.total_count = "6";////分母,总的数量
            mModuleList.add(mListenModule);
        }

        //ViewPage 的Data
        FragmentOrder mFragmentOrder = new FragmentOrder();
        Bundle bundle_Module = new Bundle();
        bundle_Module.putSerializable("mModuleList", mModuleList);
        mFragmentOrder.setArguments(bundle_Module);
        mFragmentList.add(mFragmentOrder);
        FragmentClassify mFragmentClssify = new FragmentClassify();
        Bundle bundle_Classify = new Bundle();
        bundle_Classify.putSerializable("mClassifyNameList", mClassifyNameList);
        bundle_Classify.putSerializable("mClassifyCodeList", mClassifyCodeList);
        bundle_Classify.putSerializable("mClassifyList", mClassifyList);
        mFragmentClssify.setArguments(bundle_Classify);
        mFragmentList.add(mFragmentClssify);
    }

    private void initView() {
//        //改变字体颜色
//        mCollapLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
//        mCollapLayout.setExpandedTitleColor(Color.parseColor("#FCFCFC"));
//        mCollapLayout.setCollapsedTitleTextColor(Color.parseColor("#FF3370"));
        TextView textView = (TextView) findViewById(R.id.txt_header_title_item);
        textView.setText("TPO \r听力真题");
        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_top_listenering);
        mTab = (TabLayout) findViewById(R.id.tab_activity_top_listenering);
        mTabList.add("顺序");
        mTabList.add("分类");

        mAdapter = new TabTopListeneringAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTab.setTabMode(TabLayout.MODE_FIXED);
        mTab.setupWithViewPager(mViewPager);
    }

    private class TabTopListeneringAdapter extends FragmentPagerAdapter {
        public TabTopListeneringAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}