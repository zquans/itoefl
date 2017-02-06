package com.iyuce.itoefl.UI.Main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuce.itoefl.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private MyMainTabAdapter mMyTabAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    private TextView mTxtPractice, mTxtLecture, mTxtMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mTxtPractice = (TextView) findViewById(R.id.txt_activity_main_practice);
        mTxtLecture = (TextView) findViewById(R.id.txt_activity_main_lecture);
        mTxtMine = (TextView) findViewById(R.id.txt_activity_main_mine);
        LinearLayout mTabPractice = (LinearLayout) findViewById(R.id.linearlayout_activity_main_practice);
        LinearLayout mTabLecture = (LinearLayout) findViewById(R.id.linearlayout_activity_main_lecture);
        LinearLayout mTabMine = (LinearLayout) findViewById(R.id.linearlayout_activity_main_mine);
        mTabPractice.setOnClickListener(this);
        mTabLecture.setOnClickListener(this);
        mTabMine.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_main);
        mViewPager.setOnPageChangeListener(this);
        FragmentExercise mFragmentExercise = new FragmentExercise();
        FragmentLecture mFragmentLecture = new FragmentLecture();
        FragmentMine mFragmentMine = new FragmentMine();
        mFragmentList.add(mFragmentExercise);
        mFragmentList.add(mFragmentLecture);
        mFragmentList.add(mFragmentMine);
        mMyTabAdapter = new MyMainTabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMyTabAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //重选Tab
        resetTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearlayout_activity_main_practice:
                resetTab(0);
                break;
            case R.id.linearlayout_activity_main_lecture:
                resetTab(1);
                break;
            case R.id.linearlayout_activity_main_mine:
                resetTab(2);
                break;
        }
    }

    /**
     * 重选对应Tab
     *
     * @param pos
     */
    private void resetTab(int pos) {
        mViewPager.setCurrentItem(pos);
        mTxtPractice.setTextColor(Color.parseColor("#cccccc"));
        mTxtLecture.setTextColor(Color.parseColor("#cccccc"));
        mTxtMine.setTextColor(Color.parseColor("#cccccc"));
        switch (pos) {
            case 0:
                mTxtPractice.setTextColor(Color.parseColor("#f7941d"));
                break;
            case 1:
                mTxtLecture.setTextColor(Color.parseColor("#f7941d"));
                break;
            case 2:
                mTxtMine.setTextColor(Color.parseColor("#f7941d"));
                break;
        }
    }

    private class MyMainTabAdapter extends FragmentPagerAdapter {
        public MyMainTabAdapter(FragmentManager fm) {
            super(fm);
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