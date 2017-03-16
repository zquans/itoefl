package com.iyuce.itoefl.UI.Listening.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.Exercise.ListenModule;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.TopListeneringModuleAdapter;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.SDCardUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/22
 * 顺序
 */
public class FragmentOrder extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<ListenModule> mModuleeList = new ArrayList<>();
    private TopListeneringModuleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_listenering_order, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mModuleeList.clear();
        initData();
        mAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
        if (initData()) return;

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_top_listenering_order);

        mAdapter = new TopListeneringModuleAdapter(getActivity(), mModuleeList);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 装载数据
     */
    private boolean initData() {
        String path = SDCardUtil.getExercisePath();
        String tpo_path = path + File.separator + Constants.SQLITE_TPO;

        SQLiteDatabase mDatabase = DbUtil.getHelper(getActivity(), tpo_path).getWritableDatabase();
        //从默认主表中查，是否有这张表
        String isNone_Paper = DbUtil.queryToString(mDatabase, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_PAPER);
        if (TextUtils.equals(isNone_Paper, Constants.NONE)) {
            ToastUtil.showMessage(getActivity(), "网络不佳，未获取到数据,请返回重试");
            mDatabase.close();
            return true;
        }
        ArrayList<String> nameList = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_PAPER, null, Constants.PaperName);
        mDatabase.close();

        //提供module 和 progress
        ListenModule mListenModule;
        for (int i = 0; i < nameList.size(); i++) {
            mListenModule = new ListenModule();
            mListenModule.name = nameList.get(i);

            String practiced_path = path + File.separator + Constants.SQLITE_DOWNLOAD;
            SQLiteDatabase mDatabaseDownload = DbUtil.getHelper(getActivity(), practiced_path).getWritableDatabase();
            String isNone_Download = DbUtil.queryToString(mDatabaseDownload, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_ALREADY_DOWNLOAD);
            if (!TextUtils.equals(isNone_Download, Constants.NONE)) {
                String practiced_count_sql = "SELECT COUNT(*) FROM " + Constants.TABLE_ALREADY_DOWNLOAD + " WHERE " + Constants.SECTION + " =? and " + Constants.Practiced + " =?";
                String practiced_count = DbUtil.cursorToString(mDatabaseDownload.rawQuery(practiced_count_sql, new String[]{nameList.get(i), "true"}));
                mListenModule.practiced_count = practiced_count;//拿练习过的数据数量
//                LogUtil.i("practiced_count = " + practiced_count);
            }
            mDatabaseDownload.close();

            SQLiteDatabase mDatabaseTpo = DbUtil.getHelper(getActivity(), tpo_path).getWritableDatabase();
            String isNone_Tpo = DbUtil.queryToString(mDatabaseTpo, Constants.TABLE_SQLITE_MASTER, Constants.NAME, Constants.TABLE_NAME, Constants.TABLE_PAPER_RULE);
            if (!TextUtils.equals(isNone_Tpo, Constants.NONE)) {
                String total_count_sql = "SELECT COUNT(*) FROM " + Constants.TABLE_PAPER_RULE + " WHERE " + Constants.PaperCode + " =? ";
                String total_count = DbUtil.cursorToString(mDatabaseTpo.rawQuery(total_count_sql, new String[]{nameList.get(i)}));
                mListenModule.total_count = total_count;//拿所有的module的数据数量
//                LogUtil.i("total_count = " + total_count);
            }
            mDatabaseTpo.close();
            mModuleeList.add(mListenModule);
        }
        return false;
    }
}