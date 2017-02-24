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

import com.iyuce.itoefl.Model.Exercise.ListenResultContent;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.ResultContentAdapter;

import java.util.ArrayList;

public class FragmentDoResult extends Fragment implements View.OnClickListener {

    private TextView mTxtPageMiddle, mTxtPageRight, mTxtQuestion, mTxtTimeCount;

    private RecyclerView mRecyclerView;
    private ArrayList<ListenResultContent> mResultList = new ArrayList<>();
    private ArrayList<String> mOptionList;
    private ResultContentAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    private String page_current, page_total, page_question, answer_select, answer_option, time_count;

    public static FragmentDoResult newInstance(String page_current, String page_total, String page_question,
                                               ArrayList<String> option_list, String answer_select, String answer_option, String time_count) {
        FragmentDoResult fragment = new FragmentDoResult();
        Bundle bundle = new Bundle();
        bundle.putString("page_current", page_current);
        bundle.putString("page_total", page_total);
        bundle.putString("page_question", page_question);
        bundle.putStringArrayList("option_list", option_list);
        bundle.putString("answer_select", answer_select);
        bundle.putString("answer_option", answer_option);
        bundle.putString("time_count", time_count);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page_current = getArguments().getString("page_current");
            page_total = getArguments().getString("page_total");
            page_question = getArguments().getString("page_question");
            mOptionList = getArguments().getStringArrayList("option_list");
            answer_select = getArguments().getString("answer_select");
            answer_option = getArguments().getString("answer_option");
            time_count = getArguments().getString("time_count");
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
        mTxtQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_title);
        mTxtTimeCount = (TextView) view.findViewById(R.id.txt_fragment_do_result_time_count);

        mTxtPageMiddle.setText(page_current);
        mTxtPageRight.setText(page_total);
        mTxtQuestion.setText(page_question);
        mTxtTimeCount.setText("本题用时 " + time_count + " 秒");
        mTxtPageMiddle.setOnClickListener(this);

        //TODO 用户的选择，正确答案，这两种设置应该更优雅
        ListenResultContent result;
        for (int i = 'A'; i < 'E'; i++) {
            result = new ListenResultContent();
            result.number = String.valueOf((char) i);
            mResultList.add(result);
        }
        for (int i = 0; i < mOptionList.size(); i++) {
            mResultList.get(i).content = mOptionList.get(i);
            if (mResultList.get(i).number.equals(answer_select)) {
                mResultList.get(i).state = "false";
            } else if (mResultList.get(i).number.equals(answer_option)) {
                mResultList.get(i).state = "true";
            } else {
                mResultList.get(i).state = "none";
            }
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ResultContentAdapter(getActivity(), mResultList);
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