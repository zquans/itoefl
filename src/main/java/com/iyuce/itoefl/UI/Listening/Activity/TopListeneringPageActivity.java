package com.iyuce.itoefl.UI.Listening.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.TopListeneringPageAdapter;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.ZipUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class TopListeneringPageActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> mDataList = new ArrayList<>();

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
            mSavePath = sdPath + "Itoefl";
            //建立目标路径文件
            File dir = new File(mSavePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    private void initView() {
        //back button in <include header>
        findViewById(R.id.txt_header_title_menu).setOnClickListener(this);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_top_listenering_page);
        for (int i = 0; i < 7; i++) {
            mDataList.add("Lecture " + i);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TopListeneringPageAdapter(this, mDataList));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_header_title_menu:
                doDownLoad();
                break;
            case R.id.imgbtn_header_title:
                finish();
                break;
        }
    }

    private void doDownLoad() {
        OkGo.get("http://img.enhance.cn/toefl/zip/listenaudiozip/1402.zip")
                .execute(new FileCallback(mSavePath, "") {
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        LogUtil.i(currentSize + "||" + totalSize + "||" + progress);
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        /**Zip解压文件夹*/
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
                            //解压zip到对应路径
                            //ZipUtil.UnZipFolder("/storage/emulated/0/download/1402.zip", "/storage/emulated/0/download/le");
                            ZipUtil.UnZipFolder(file.getAbsolutePath(), mSavePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //输出对应的SQLite数据库路径
                        LogUtil.i("mSQLitePath = " + mSQLitePath);
                    }
                });
    }
}