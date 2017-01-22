package com.iyuce.itoefl.UI.Main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_main);
        FragmentExercise mFragmentExercise = new FragmentExercise();
        FragmentLecture mFragmentLecture = new FragmentLecture();
        FragmentMine mFragmentMine = new FragmentMine();
        mFragmentList.add(mFragmentExercise);
        mFragmentList.add(mFragmentLecture);
        mFragmentList.add(mFragmentMine);
        LogUtil.i("FragmentExercise");
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
    }
}