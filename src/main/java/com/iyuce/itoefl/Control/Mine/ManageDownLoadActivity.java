package com.iyuce.itoefl.Control.Mine;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iyuce.itoefl.BaseActivity;
import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Control.Mine.Adapter.ManageDownloadAdapter;
import com.iyuce.itoefl.Model.ManageDownload;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.FileUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/23
 */
public class ManageDownLoadActivity extends BaseActivity
        implements ManageDownloadAdapter.OnDeleteDownLoadListener {

    private TextView mTxtTitle;
    private RecyclerView mRecyclerView;
    private ManageDownloadAdapter mAdapter;
    private ArrayList<ManageDownload> mFileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_download);

        findViewById(R.id.txt_header_title_menu).setVisibility(View.GONE);
        findViewById(R.id.imgbtn_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        mTxtTitle = (TextView) findViewById(R.id.txt_header_title_item);
        mTxtTitle.setText("离线管理");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_activity_manage_download);

        initData();
        mAdapter = new ManageDownloadAdapter(this, mFileList);
        mAdapter.setOnDeleteDownLoadListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    //初始化数据
    private void initData() {
        File file = new File(SDCardUtil.getExercisePath() + File.separator);
        countListSize(file);
    }

    //计算数据
    public void countListSize(File dir) {
        ManageDownload fileDownload;
        File[] files = dir.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                LogUtil.e("===" + file.getName() + "|||file.length = " + FileUtil.dealLength(FileUtil.getFileSize(file)));
                fileDownload = new ManageDownload();
                fileDownload.path = file.toString();
                fileDownload.name = file.getName();
                fileDownload.size = FileUtil.dealLength(FileUtil.getFileSize(file));
                mFileList.add(fileDownload);
            }
        }
    }

    @Override
    public void OnDelete(final int pos) {
        new AlertDialog.Builder(this).setMessage("确定要删除吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(mFileList.get(pos).path);
                //删除文件夹
                LogUtil.i(mFileList.get(pos).name + " delete = " + deleteFile(file));
                //修改数据库用户操作表
                changeDatabase(pos);
                //刷新UI
                mFileList.clear();
                initData();
                mAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("取消", null).show();
    }

    /**
     * 修改数据库用户操作表
     */
    private void changeDatabase(int pos) {
        String downloaded_sql_path = SDCardUtil.getExercisePath() + File.separator + Constants.SQLITE_DOWNLOAD;
        SQLiteDatabase mDatabase = DbUtil.getHelper(this, downloaded_sql_path).getWritableDatabase();
        String sql_delete = "DELETE FROM " + Constants.TABLE_ALREADY_DOWNLOAD +
                " WHERE " + Constants.SECTION + " = \"" + mFileList.get(pos).name + "\"";
        mDatabase.execSQL(sql_delete);
        mDatabase.close();
    }

    /**
     * 删除文件夹
     */
    private boolean deleteFile(File targetfile) {
        if (targetfile.isDirectory()) {
            File[] files = targetfile.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        }
        boolean isDelete = targetfile.delete();
        return isDelete;
    }
}