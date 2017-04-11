package com.iyuce.itoefl.Control.Mine;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.BaseFragment;
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
 * Created by LeBang on 2017/4/6
 */
public class FragmentManageDownLoad extends BaseFragment implements ManageDownloadAdapter.OnDeleteDownLoadListener {

    private RecyclerView mRecyclerView;
    private ManageDownloadAdapter mAdapter;
    private ArrayList<ManageDownload> mFileList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_download, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_activity_manage_download);
        initData();
        mAdapter = new ManageDownloadAdapter(getActivity(), mFileList);
        mAdapter.setOnDeleteDownLoadListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    //初始化数据
    private void initData() {
        //TODO 唯一区别的参数,但是文件结构还是有差别
        File file = new File(SDCardUtil.getExercisePath() + File.separator);
        countListSize(file);
    }

    //计算数据
    public void countListSize(File dir) {
        ManageDownload fileDownload;
        File[] files = dir.listFiles();
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
        new AlertDialog.Builder(getActivity()).setMessage("确定要删除吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFile(pos);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void deleteFile(int pos) {
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

    /**
     * 修改数据库用户操作表
     */
    private void changeDatabase(int pos) {
        String downloaded_sql_path = SDCardUtil.getExercisePath() + File.separator + Constants.SQLITE_DOWNLOAD;
        SQLiteDatabase mDatabase = DbUtil.getHelper(getActivity(), downloaded_sql_path).getWritableDatabase();
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