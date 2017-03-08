package com.iyuce.itoefl.UI.Main;

import android.Manifest;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;
import com.iyuce.itoefl.Utils.ToastUtil;
import com.iyuce.itoefl.Utils.ZipUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;
import java.util.ArrayList;

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

        //判断是否有权限
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            decideDownload();
        } else {
            //没权限，进行权限请求
            requestPermission(Constants.CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void decideDownload() {
        //查看根文件路径中是否已存在根sql库,否则下载
        String path = SDCardUtil.getExercisePath();
        String filePath = path + File.separator + Constants.SQLITE_TPO;
        File file = new File(filePath);
        LogUtil.i("sql exist = " + file.exists());
        if (!file.exists()) {
            doDownLoad(path);
        } else {
            LogUtil.i("sql exist = yes");
            SQLiteDatabase mDatabase = DbUtil.getHelper(this, filePath).getWritableDatabase();
            String isNone = DbUtil.queryToString(mDatabase, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_PAPER);
            mDatabase.close();
            if (TextUtils.equals(isNone, Constants.NONE)) {
                LogUtil.i("still download the zip");
                doDownLoad(path);
            }
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
                    }
                });
    }

    private void unZipFile(File file, String path) {
        try {
            //解压zip文件到对应路径
            ZipUtil.UnZipFolder(file.getAbsolutePath(), path);
            //删除该文件压缩包.zip
            LogUtil.i("zip delete = " + file.delete());
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
        mTxtPractice.setTextColor(Color.parseColor("#FCFCFC"));
        mTxtLecture.setTextColor(Color.parseColor("#FCFCFC"));
        mTxtMine.setTextColor(Color.parseColor("#FCFCFC"));
        switch (pos) {
            case 0:
                mTxtPractice.setTextColor(Color.parseColor("#FF3370"));
                break;
            case 1:
                mTxtLecture.setTextColor(Color.parseColor("#FF3370"));
                break;
            case 2:
                mTxtMine.setTextColor(Color.parseColor("#FF3370"));
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