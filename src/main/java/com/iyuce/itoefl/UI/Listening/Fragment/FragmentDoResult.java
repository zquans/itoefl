package com.iyuce.itoefl.UI.Listening.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.ResultContentAdapter;

import java.util.ArrayList;

public class FragmentDoResult extends Fragment implements View.OnClickListener {

    private TextView mTxtPageMiddle, mTxtPageRight;

    private RecyclerView mRecyclerView;
    private ArrayList<String> mDataList = new ArrayList<>();
    private ResultContentAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    private String mPageNumber;

    public static FragmentDoResult newInstance(String param) {
        FragmentDoResult fragment = new FragmentDoResult();
        Bundle args = new Bundle();
        args.putString("page_number", param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPageNumber = getArguments().getString("page_number");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen_do_result, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_do_result);
        mTxtPageMiddle = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_middle);
        mTxtPageRight = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_right);

        //从Activity中获取到题号
        mTxtPageMiddle.setText(mPageNumber);
        mTxtPageMiddle.setOnClickListener(this);

        //Fragment内部的题目数据
        for (int i = 0; i < 5; i++) {
            mDataList.add("linx: musicPath musicPath musicPath musicPath musicPath" + i);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ResultContentAdapter(mDataList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void doMything(String string) {
        if (mListener != null) {
            mListener.onFragmentInteraction(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        doMything("a ha a ha");
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String s);
    }
}