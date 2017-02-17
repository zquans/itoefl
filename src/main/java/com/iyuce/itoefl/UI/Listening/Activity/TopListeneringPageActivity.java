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
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.TopListeneringPageAdapter;
import com.iyuce.itoefl.Utils.HttpUtil;
import com.iyuce.itoefl.Utils.Interface.HttpInterface;

import java.io.File;
import java.util.ArrayList;

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
     * 给定几个参数，position,Url,path
     */
    private void doDownLoad(final int pos, final String path) {
        HttpUtil.downLoad(pos, path, new HttpInterface() {
            @Override
            public void inProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                if (pos != -1) {
                    ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                    imgview.setBackgroundResource(R.mipmap.icon_download_finish);
                    TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(((int) (progress * 100) + "%"));
                }
            }

            @Override
            public void doSuccess(File file, Call call, Response response) {
                if (pos != -1) {
                    //下载箭头、文字设为不可见
                    ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
                    imgview.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });

//        OkGo.get("http://img.enhance.cn/toefl/zip/listenaudiozip/1402.zip")
//                .execute(new FileCallback(path, "") {
//                    @Override
//                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
//                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
//                        LogUtil.i(currentSize + "||" + totalSize + "||" + progress);
//                        if (pos != -1) {
//                            ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
//                            imgview.setBackgroundResource(R.mipmap.icon_download_finish);
//                            TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
//                            textView.setVisibility(View.VISIBLE);
//                            textView.setText(((int) (progress * 100) + "%"));
//                        }
//                    }
//
//                    @Override
//                    public void onSuccess(File file, Call call, Response response) {
//                        if (pos != -1) {
//                            //下载箭头、文字设为不可见
//                            ImageView imgview = (ImageView) mRecyclerView.getChildAt(pos).findViewById(R.id.img_recycler_item_top_listenering_page_download);
//                            imgview.setVisibility(View.INVISIBLE);
//                            TextView textView = (TextView) mRecyclerView.getChildAt(pos).findViewById(R.id.txt_recycler_item_top_listenering_page_download);
//                            textView.setVisibility(View.INVISIBLE);
//                        }
//                    }
//                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_header_title_menu:
                for (int i = 0; i < mDataList.size(); i++) {
                    mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            Constants.FILE_PATH_ITOEFL_EXERCISE + File.separator + mDataList.get(i);
                    doDownLoad(i, mSavePath);
                }
                break;
            case R.id.imgbtn_header_title:
                finish();
                break;
        }
    }

    @Override
    public void OnPageItemClick(int pos) {
        mImgReward.setBackgroundResource(R.mipmap.icon_reward_finish);

        //关键是末位路径
        mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                Constants.FILE_PATH_ITOEFL_EXERCISE + File.separator + mDataList.get(pos);
        //从List的属性中，判断是否下载，是则进入，否则下载
        if (pos > 4) {
            doDownLoad(pos, mSavePath);
            return;
        }
        Intent intent = new Intent(this, PageReadyActivity.class);
        intent.putExtra("local_path", mSavePath);
        startActivity(intent);
    }
}