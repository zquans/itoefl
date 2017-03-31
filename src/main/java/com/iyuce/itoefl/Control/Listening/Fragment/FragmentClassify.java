package com.iyuce.itoefl.Control.Listening.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.Control.Listening.Adapter.TopListeneringClassifyAdapter;
import com.iyuce.itoefl.Model.Exercise.ListenModule;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/22
 * 分类
 */
public class FragmentClassify extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<String> mCategoryList = new ArrayList<>();
    private TopListeneringClassifyAdapter mAdapter;
    private ArrayList<ListenModule> mModuleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_listenering_order, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mModuleList = (ArrayList<ListenModule>) getArguments().get("mModuleeList");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_top_listenering_order);
        mCategoryList.add("课程作业");
        mCategoryList.add("研究项目");
        mCategoryList.add("住宿与餐饮");
        mCategoryList.add("人类学");
        mCategoryList.add("哲学");
        mAdapter = new TopListeneringClassifyAdapter(getActivity(), mCategoryList, mModuleList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }
}