package com.iyuce.itoefl.UI.Main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup_activity_main);
        mRadioGroup.setOnCheckedChangeListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_activity_main);
        mViewPager.setOnPageChangeListener(this);
        FragmentExercise mFragmentExercise = new FragmentExercise();
        FragmentLecture mFragmentLecture = new FragmentLecture();
        FragmentMine mFragmentMine = new FragmentMine();
        mFragmentList.add(mFragmentExercise);
        mFragmentList.add(mFragmentLecture);
        mFragmentList.add(mFragmentMine);
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton) findViewById(checkedId);
        radioButton.setBackgroundResource(R.color.Gray);
        radioButton.setTextColor(Color.parseColor("#f7941d"));
        LogUtil.i(radioButton.getText().toString() + checkedId);
        mViewPager.setCurrentItem(checkedId - 1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mRadioGroup.check(position + 1);

        LogUtil.i(position + "");
//        RadioButton mRadioButton = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
//        mRadioButton.setBackgroundResource(R.color.colorPrimary);
//        mRadioButton.setTextColor(Color.parseColor("#f7941d"));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}