package com.iyuce.itoefl.Control.Listening.Activity;

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
import com.iyuce.itoefl.Utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

public class TopListeneringActivity extends BaseActivity {

    //导航指示器
    private TabLayout mTab;
    private ArrayList<String> mTabList = new ArrayList<>();
    private ArrayList<ListenModule> mModuleeList = new ArrayList<>();

    private ViewPager mViewPager;
    private TabTopListeneringAdapter mAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

//    private CollapsingToolbarLayout mCollapLayout;

    @Override
    protected void onRestart() {
        super.onRestart();
        mModuleeList.clear();
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
        ArrayList<String> nameList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER, Constants.PaperCode + " ASC", Constants.PaperName);
        mDatabase.close();

        //提供module 和 progress
        ListenModule mListenModule;
        String practiced_path;
        for (int i = 0; i < nameList.size(); i++) {
            mListenModule = new ListenModule();
            mListenModule.name = nameList.get(i);

            practiced_path = path + File.separator + Constants.SQLITE_DOWNLOAD;
            SQLiteDatabase mDatabaseDownload = DbUtil.getHelper(this, practiced_path).getWritableDatabase();
            String isNone_Download = DbUtil.queryToString(mDatabaseDownload, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_ALREADY_DOWNLOAD);
            if (!TextUtils.equals(isNone_Download, Constants.NONE)) {
                String practiced_count_sql = "SELECT COUNT(*) FROM " + Constants.TABLE_ALREADY_DOWNLOAD + " WHERE " + Constants.SECTION + " =? and " + Constants.Practiced + " =?";
                String practiced_count = DbUtil.cursorToString(mDatabaseDownload.rawQuery(practiced_count_sql, new String[]{nameList.get(i), "true"}));
                mListenModule.practiced_count = practiced_count;//拿练习过的数据数量
            }
            mDatabaseDownload.close();

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
            mModuleeList.add(mListenModule);
        }

        //ViewPage 的Data
        Bundle bundle = new Bundle();
        bundle.putSerializable("mModuleeList", mModuleeList);
        FragmentOrder mFragmentOrder = new FragmentOrder();
        mFragmentOrder.setArguments(bundle);
        mFragmentList.add(mFragmentOrder);
        FragmentClassify mFragmentClssify = new FragmentClassify();
        mFragmentClssify.setArguments(bundle);
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