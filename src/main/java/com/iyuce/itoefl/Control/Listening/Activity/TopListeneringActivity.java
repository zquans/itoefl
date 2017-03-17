package com.iyuce.itoefl.Control.Listening.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Control.Listening.Fragment.FragmentOrder;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

public class TopListeneringActivity extends BaseActivity {

    //导航指示器
    private TabLayout mTab;
    private ArrayList<String> mTabList = new ArrayList<>();

    private ViewPager mViewPager;
    private TabTopListeneringAdapter mAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    private CollapsingToolbarLayout mCollapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering);
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
        mCollapLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        //改变字体颜色
        mCollapLayout.setExpandedTitleColor(Color.parseColor("#FCFCFC"));
        mCollapLayout.setCollapsedTitleTextColor(Color.parseColor("#FF3370"));

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
//        mTab = (TabLayout) findViewById(R.id.tab_activity_top_listenering);
//        mTabList.add("顺序");
//        mTabList.add("分类");
        mFragmentList.add(new FragmentOrder());
//        mFragmentList.add(new FragmentClassify());
        mAdapter = new TabTopListeneringAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
//        mTab.setTabMode(TabLayout.MODE_FIXED);
//        mTab.setupWithViewPager(mViewPager);
    }

    private class TabTopListeneringAdapter extends FragmentPagerAdapter {
        public TabTopListeneringAdapter(FragmentManager fm) {
            super(fm);
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mTabList.get(position);
//        }

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