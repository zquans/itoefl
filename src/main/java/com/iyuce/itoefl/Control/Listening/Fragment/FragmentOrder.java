package com.iyuce.itoefl.Control.Listening.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.BaseFragment;
import com.iyuce.itoefl.Control.Listening.Adapter.TopListeneringModuleAdapter;
import com.iyuce.itoefl.Model.Exercise.ListenModule;
import com.iyuce.itoefl.R;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/22
 * 顺序
 */
public class FragmentOrder extends BaseFragment {

    private ArrayList<ListenModule> mModuleList;
    private TopListeneringModuleAdapter mAdapter;

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
        //数据
        mModuleList = (ArrayList<ListenModule>) getArguments().get("mModuleList");
        //视图
        TextView mTxtTitle = (TextView) view.findViewById(R.id.txt_fragment_top_listenering_order);
        mTxtTitle.setVisibility(View.VISIBLE);
        mTxtTitle.setText(getString(R.string.count_tpo, mModuleList.get(mModuleList.size() - 1).name));
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_top_listenering_order);
        mAdapter = new TopListeneringModuleAdapter(getActivity(), mModuleList);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }
}