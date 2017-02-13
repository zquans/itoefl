package com.iyuce.itoefl.UI.Listening.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.TopListeneringPageAdapter;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.PreferenceUtil;
import com.iyuce.itoefl.Utils.ZipUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class TopListeneringPageActivity extends BaseActivity
        implements View.OnClickListener, TopListeneringPageAdapter.OnPageItemClickListener {

    private RecyclerView mRecyclerView;
    private TopListeneringPageAdapter mAdapter;
    private ArrayList<String> mDataList = new ArrayList<>();

    private TextView mTxtFinish, mTxtTotal;
    private ImageView mImgReward;
    private String mSavePath, mSQLitePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_listenering_page);

        initView();
        initFileDir();
    }

    /**
     * 初始化下载目标文件夹
     */
    private void initFileDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // sd卡根目录
            String sdPath = Environment.getExternalStorageDirectory() + "/";
            mSavePath = sdPath + "ITOEFL";
            //建立目标路径文件
            File dir = new File(mSavePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            /*将路径保存到SharePreferences中*/
            PreferenceUtil.save(this, "SdPath", mSavePath);
        }
    }

    private void initView() {
        findViewById(R.id.txt_header_title_menu).setOnClickListener(this);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);

        mImgReward = (ImageView) findViewById(R.id.img_activity_top_listenering_award);
        mTxtFinish = (TextView) findViewById(R.id.txt_activity_top_listenering_finish);
        mTxtTotal = (TextView) findViewById(R.id.txt_activity_top_listenering_total);
        mTxtTotal.setText("总共 : 12篇");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_top_listenering_page);
        for (int i = 0; i < 7; i++) {
            mDataList.add("Lecture " + i);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TopListeneringPageAdapter(this, mDataList);
        mAdapter.setOnPageItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 给定几个参数，position,Url,mSavePath
     */
    private void doDownLoad(final int pos) {
        OkGo.get("http://img.enhance.cn/toefl/zip/listenaudiozip/1402.zip")
                .execute(new FileCallback(mSavePath, "") {
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        //TODO 全部下载该如何处理,OKGO,并发队列下载,做一个循环，往里传参数轮流执行？
                        LogUtil.i(currentSize + "||" + totalSize + "||" + progress);
                        if (pos != -1) {
                            ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                            imgview.setBackgroundResource(R.mipmap.icon_download_finish);
                            TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(((int) (progress * 100) + "%"));
                        }
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        if (pos != -1) {
                            //下载箭头、文字设为不可见
                            ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                            imgview.setVisibility(View.INVISIBLE);
                            TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                            textView.setVisibility(View.INVISIBLE);
                        }
                        unZipFile(file);
                    }
                });
    }

    /**
     * UnZip解压文件夹
     */
    private void unZipFile(File file) {
        List<File> mList;
        try {
            //获取文件的文件名
            //mList = ZipUtil.GetFileList("/storage/emulated/0/download/1402.zip", true, true);
            mList = ZipUtil.GetFileList(file.getAbsolutePath(), true, true);
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getName().contains("sqlite")) {
                    //拿出数据库文件的路径
                    mSQLitePath = mSavePath + "/" + mList.get(i).getName();
                }
                LogUtil.i("mList = " + mList.get(i).getName());
            }
            //解压zip文件到对应路径
            //ZipUtil.UnZipFolder("/storage/emulated/0/download/1402.zip", "/storage/emulated/0/download/le");
            ZipUtil.UnZipFolder(file.getAbsolutePath(), mSavePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //输出对应的SQLite数据库路径
        LogUtil.i("mSQLitePath = " + mSQLitePath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_header_title_menu:
                doDownLoad(-1);
                break;
            case R.id.imgbtn_header_title:
                finish();
                break;
        }
    }

    //Adaptere中的自定义ItemClick
    @Override
    public void OnPageItemClick(int pos) {
        if (pos == 0) {
            mImgReward.setBackgroundResource(R.mipmap.icon_reward_finish);
            doDownLoad(pos);
            return;
        }
        startActivity(new Intent(this, PageReadyActivity.class));
        LogUtil.i("pos = " + pos);
    }
}