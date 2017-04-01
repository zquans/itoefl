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

    private ArrayList<String> mClassifyNameList = new ArrayList<>();
    private ArrayList<String> mClassifyCodeList = new ArrayList<>();
    private ArrayList<ListenModule> mClassifyList = new ArrayList<>();
    private TopListeneringClassifyAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_listenering_order, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
        mClassifyNameList = (ArrayList<String>) getArguments().get("mClassifyNameList");
        mClassifyCodeList = (ArrayList<String>) getArguments().get("mClassifyCodeList");
        mClassifyList = (ArrayList<ListenModule>) getArguments().get("mClassifyList");
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_top_listenering_order);
        mAdapter = new TopListeneringClassifyAdapter(getActivity(), mClassifyNameList, mClassifyCodeList, mClassifyList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }
}