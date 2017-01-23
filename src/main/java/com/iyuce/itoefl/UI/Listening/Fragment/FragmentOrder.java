package com.iyuce.itoefl.UI.Listening.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.TopListeneringAdapter;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/1/22
 * 顺序
 */
public class FragmentOrder extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<String> dataList = new ArrayList<>();
    private TopListeneringAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_listenering_order, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_top_listenering_order);
        for (int i = 'a'; i < 'z'; i++) {
            dataList.add(i + "");
        }
        mAdapter = new TopListeneringAdapter(getActivity(), dataList, 5);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }
}