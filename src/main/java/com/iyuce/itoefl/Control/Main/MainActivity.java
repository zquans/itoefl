package com.iyuce.itoefl.Control.Main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.DecidedDownload;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.HttpUtil;
import com.iyuce.itoefl.Utils.Interface.Http.DownLoadInterface;
import com.iyuce.itoefl.Utils.Interface.Http.RequestInterface;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;
import com.iyuce.itoefl.Utils.ToastUtil;
import com.iyuce.itoefl.Utils.ZipUtil;
import com.iyuce.itoefl.View.NoScrollViewPager;
import com.lzy.okgo.model.HttpHeaders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private NoScrollViewPager mViewPager;
    private MyMainTabAdapter mMyTabAdapter;
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    private TextView mTxtPractice, mTxtLecture, mTxtMine;
    private ImageView mImgPractice, mImgLecture, mImgMine;

    private DecidedDownload mDecidedDownload = new DecidedDownload();

    private boolean isFirst = true;
    private long lastTime;
    private String check_download_time;

    private ProgressDialog mProgressdialog;

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
        mImgPractice = (ImageView) findViewById(R.id.img_activity_main_practice);
        mImgLecture = (ImageView) findViewById(R.id.img_activity_main_lecture);
        mImgMine = (ImageView) findViewById(R.id.img_activity_main_mine);
        LinearLayout mTabPractice = (LinearLayout) findViewById(R.id.linearlayout_activity_main_practice);
        LinearLayout mTabLecture = (LinearLayout) findViewById(R.id.linearlayout_activity_main_lecture);
        LinearLayout mTabMine = (LinearLayout) findViewById(R.id.linearlayout_activity_main_mine);
        mTabPractice.setOnClickListener(this);
        mTabLecture.setOnClickListener(this);
        mTabMine.setOnClickListener(this);

        mViewPager = (NoScrollViewPager) findViewById(R.id.viewpager_activity_main);
        mViewPager.setOnPageChangeListener(this);
        mFragmentList.add(new FragmentExercise());
        mFragmentList.add(new FragmentVocabulary());
        mFragmentList.add(new FragmentMine());
        mMyTabAdapter = new MyMainTabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMyTabAdapter);
        mViewPager.setOffscreenPageLimit(mFragmentList.size() - 1);

        // 通过Http 决定是否 下载,1.是则下载，2.否，则判断是否有文件，无则下载
        check_download_time = PreferenceUtil.getSharePre(this).getString(Constants.REQUEST_TIME_MAIN_DATABASE, "");
        requestIfDown(check_download_time);
    }

    private void requestIfDown(String check_download_time) {
//        LogUtil.e("user-Agent = " + System.getProperty("http.agent"));
        LogUtil.w("check_download_time = " + check_download_time);
        String localVersion = PreferenceUtil.getSharePre(this).getString(Constants.Preference_Version_Local, "1.0");
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.HEAD_KEY_USER_AGENT, System.getProperty("http.agent") + "; toefl/" + localVersion);
        HttpUtil.post(Constants.URL_TPO_MAIN_STATUS + check_download_time, headers, null, new RequestInterface() {
            @Override
            public void doSuccess(String result, Call call, Response response) {
                try {
                    JSONObject obj;
                    obj = new JSONObject(result);
                    mDecidedDownload.code = obj.getString(Constants.CODE_HTTP);
                    mDecidedDownload.data = obj.getString(Constants.DATA_HTTP);
                    mDecidedDownload.message = obj.getString(Constants.MESSAGE_HTTP);
                    mDecidedDownload.request_date = obj.getString("request_date");
                    if (mDecidedDownload.code.equals(Constants.CODE_HTTP_SUCCESS)) {
                        //需要下载，无论存在文件与否，都去下载
                        LogUtil.i("接口说要下载");
                        if (PreferenceUtil.getSharePre(MainActivity.this).getString(Constants.Preference_MAIN_DB_DOWNLOAD, "")
                                .equals(Constants.Preference_MAIN_DB_DOWNLOAD)) {
                            //TODO 不是第一次，提示是否更新
                            downloadAlertDialog();
                        } else {
                            //TODO 如果第一次，自动下载
                            downDatabase(true);
                        }
                    } else {
                        //不需要下载，依然去判断是否存在文件
                        decideDownload(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 给用户友好提示，是否需要下载
     */
    private void downloadAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("题库更新啦")
                .setMessage("去下载最新题库吗?")
                .setPositiveButton("立刻更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downDatabase(true);
                    }
                }).setNegativeButton("以后再说", null)
                .show();
    }

    /**
     * 下载数据库
     */
    private void downDatabase(boolean todownload) {
        //判断是否有权限
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            decideDownload(todownload);
        } else {
            //没权限，进行权限请求
            requestPermission(Constants.CODE_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void decideDownload(boolean todownload) {
        //查看根文件路径中是否已存在根sql库,否则下载
        String path = SDCardUtil.getExercisePath();
        String filePath = path + File.separator + Constants.SQLITE_TPO;
        File file = new File(filePath);
        if (todownload) {
            //接口说要下载
            doDownLoad(path);
        } else {
            //接口说不要下载
            LogUtil.i("接口说不要下载");
            if (!file.exists()) {
                doDownLoad(path);
            }
        }
    }

    private void doDownLoad(final String path) {
        HttpUtil.downLoad(mDecidedDownload.data, path, new DownLoadInterface() {
            @Override
            public void inProgress(long currentSize, long totalSize, float progress, long networkSpeed) {

            }

            @Override
            public void doSuccess(File file, Call call, Response response) {
                unZipFile(file, path);
                //保留本次请求时间到下一次请求的参数中使用
                PreferenceUtil.save(MainActivity.this, Constants.REQUEST_TIME_MAIN_DATABASE, mDecidedDownload.request_date);
                //标记第一次下载完成,从此以后都是询问是否更新
                PreferenceUtil.save(MainActivity.this, Constants.Preference_MAIN_DB_DOWNLOAD, Constants.Preference_MAIN_DB_DOWNLOAD);
            }

            @Override
            public void onBefore() {
                mProgressdialog = new ProgressDialog(MainActivity.this);
                mProgressdialog.setTitle("更新题库数据，请稍候");
                mProgressdialog.setMessage("Loading...");
                mProgressdialog.setCanceledOnTouchOutside(false);
                mProgressdialog.show();
            }

            @Override
            public void onAfter() {
                if (mProgressdialog != null) {
                    mProgressdialog.cancel();
                }
            }
        });
    }

    private void unZipFile(File file, String path) {
        try {
            //解压后删除该文件压缩包
            ZipUtil.UnZipFolder(file.getAbsolutePath(), path);
            if (file.delete()) {
                ToastUtil.showMessage(this, "下载更新成功啦");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
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
     */
    private void resetTab(int pos) {
        mViewPager.setCurrentItem(pos);
        mImgPractice.setBackgroundResource(R.mipmap.icon_exercise_normal);
        mImgLecture.setBackgroundResource(R.mipmap.icon_lesson_normal);
        mImgMine.setBackgroundResource(R.mipmap.icon_mine_normal);
        mTxtPractice.setTextColor(Color.parseColor("#5f738f"));
        mTxtLecture.setTextColor(Color.parseColor("#5f738f"));
        mTxtMine.setTextColor(Color.parseColor("#5f738f"));
        switch (pos) {
            case 0:
                mTxtPractice.setTextColor(Color.parseColor("#46AAFF"));
                mImgPractice.setBackgroundResource(R.mipmap.icon_exercise_select);
                break;
            case 1:
                mTxtLecture.setTextColor(Color.parseColor("#46AAFF"));
                mImgLecture.setBackgroundResource(R.mipmap.icon_lesson_select);
                break;
            case 2:
                mTxtMine.setTextColor(Color.parseColor("#46AAFF"));
                mImgMine.setBackgroundResource(R.mipmap.icon_mine_select);
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