package com.iyuce.itoefl.UI.Main;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;
import com.iyuce.itoefl.Utils.ToastUtil;
import com.iyuce.itoefl.Utils.ZipUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private MyMainTabAdapter mMyTabAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    private TextView mTxtPractice, mTxtLecture, mTxtMine;

    private boolean isFirst = true;
    private long lastTime;

    @Override
    public void onBackPressed() {
        if (isFirst) {
            ToastUtil.showMessage(this, "再按一次退出程序");
            lastTime = System.currentTimeMillis();
            isFirst = false;
        } else {
            if ((System.currentTimeMillis() - lastTime) < 1000) {
                this.finish();
            } else {
                ToastUtil.showMessage(this, "再按一次退出程序");
                lastTime = System.currentTimeMillis();
            }
        }
    }

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


        LogUtil.i("main table is exist ? = " + PreferenceUtil.getSharePre(this).getString(Constants.TABLE_ALREADY_DOWNLOAD, "false"));
        //判断是否已经下载了链表主表
        if (PreferenceUtil.getSharePre(this).getString(Constants.TABLE_ALREADY_DOWNLOAD, "false").equals("false")) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FILE_PATH_ITOEFL_EXERCISE;
            LogUtil.i("path = " + path);
            doDownLoad(path);
            PreferenceUtil.save(this, Constants.TABLE_ALREADY_DOWNLOAD, "true");
        }
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

    private void doDownLoad(final String path) {
        OkGo.get("http://xm.iyuce.com/app/tpo.zip")
                .execute(new FileCallback(path, "") {
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        LogUtil.i(currentSize + "||" + totalSize + "||" + progress);
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        unZipFile(file, path);
                        PreferenceUtil.save(MainActivity.this, Constants.TABLE_TPO_PAPER, "true");
                    }
                });
    }

    private void unZipFile(File file, String path) {
        List<File> mList;
        try {
            mList = ZipUtil.GetFileList(file.getAbsolutePath(), true, true);
            for (int i = 0; i < mList.size(); i++) {
                LogUtil.i("mList = " + mList.get(i).getName());
            }
            //解压zip文件到对应路径
            ZipUtil.UnZipFolder(file.getAbsolutePath(), path);
        } catch (Exception e) {
            e.printStackTrace();
        }
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