package com.iyuce.itoefl.UI.Listening.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/22
 * 分类
 */
public class FragmentClassify extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<String> dataList = new ArrayList<>();
//    private TopListeneringAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_listenering_order, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_top_listenering_order);
//        dataList.add("课程作业");
//        dataList.add("研究项目");
//        dataList.add("住宿与餐饮");
//        dataList.add("人类学");
//        dataList.add("哲学");
//        mAdapter = new TopListeneringAdapter(getActivity(), dataList, 2);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.setAdapter(mAdapter);
    }
}