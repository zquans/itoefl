package com.iyuce.itoefl.UI.Listening.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuce.itoefl.Model.Exercise.ListenResultContent;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.ResultContentAdapter;

import java.util.ArrayList;

public class FragmentDoResult extends Fragment implements View.OnClickListener {

    private TextView mTxtPageMiddle, mTxtPageRight, mTxtQuestion, mTxtTimeCount, mTxtExplain;

    private RecyclerView mRecyclerView;
    private ArrayList<ListenResultContent> mResultList = new ArrayList<>();
    private ArrayList<String> mOptionContentList;
    private ArrayList<String> mOptionCodeList;
    private ResultContentAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    private String page_current, page_total, page_question, question_type, answer_select, answer_option, time_count;

    public static FragmentDoResult newInstance(String page_current, String page_total, String page_question,
                                               ArrayList<String> option_content_list, ArrayList<String> option_code_list,
                                               String question_type, String answer_select, String answer_option, String time_count) {
        FragmentDoResult fragment = new FragmentDoResult();
        Bundle bundle = new Bundle();
        bundle.putString("page_current", page_current);
        bundle.putString("page_total", page_total);
        bundle.putString("page_question", page_question);
        bundle.putStringArrayList("option_content_list", option_content_list);
        bundle.putStringArrayList("option_code_list", option_code_list);
        bundle.putString("question_type", question_type);
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
            mOptionContentList = getArguments().getStringArrayList("option_content_list");
            mOptionCodeList = getArguments().getStringArrayList("option_code_list");
            question_type = getArguments().getString("question_type");
            answer_select = getArguments().getString("answer_select");
            answer_option = getArguments().getString("answer_option");
            time_count = getArguments().getString("time_count");
            //将多选题的数字转换为字母ABCD
            answer_select = answer_select.replace("0", "A").replace("1", "B").replace("2", "C")
                    .replace("3", "D").replace("4", "E");
//            LogUtil.i("answer_select replace = " + answer_select);
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
        mTxtExplain = (TextView) view.findViewById(R.id.txt_fragment_do_result_explain_content);

        mTxtPageMiddle.setText(page_current);
        mTxtPageRight.setText(page_total);
        mTxtQuestion.setText(page_question);
        mTxtTimeCount.setText("本题用时 " + time_count + " 秒");
        mTxtPageMiddle.setOnClickListener(this);

        //把用户的选择、正确答案,这两个属性集合在一个对象中传给Adapter
        ListenResultContent result;
        for (int i = 0; i < mOptionCodeList.size(); i++) {
            result = new ListenResultContent();
            result.number = mOptionCodeList.get(i);
            result.content = mOptionContentList.get(i);
            mResultList.add(result);
        }

        //TODO 多选题模拟数据
        if (TextUtils.isEmpty(answer_option))
            answer_option = "BD";
        //遍历做判断,answer_option或answer_select包含该题，则修改该题的图标
        for (int i = 0; i < mOptionContentList.size(); i++) {
            if (answer_option.contains(mResultList.get(i).number)) {
                mResultList.get(i).state = "true";
            } else if (answer_select.contains(mResultList.get(i).number)) {
                mResultList.get(i).state = "false";
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