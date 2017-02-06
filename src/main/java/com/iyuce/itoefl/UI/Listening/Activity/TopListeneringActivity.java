package com.iyuce.itoefl.UI.Listening.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentClassify;
import com.iyuce.itoefl.UI.Listening.Fragment.FragmentOrder;

import java.util.ArrayList;

public class TopListeneringActivity extends AppCompatActivity {

    //导航指示器
    private TabLayout mTab;
    private ArrayList<String> mTabList = new ArrayList<>();

    private ViewPager mViewPager;
    private TabTopListeneringAdapter mAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering);

        initView();
    }

    private void initView() {
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
        mFragmentList.add(new FragmentOrder());
        mFragmentList.add(new FragmentClassify());
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
}