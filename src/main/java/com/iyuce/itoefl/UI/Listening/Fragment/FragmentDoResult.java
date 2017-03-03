package com.iyuce.itoefl.UI.Listening.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.Exercise.ListenResultContent;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.ResultContentAdapter;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.StringUtil;

import java.util.ArrayList;

public class FragmentDoResult extends Fragment {

    private TextView mTxtPageMiddle, mTxtPageRight, mTxtQuestion, mTxtTimeCount;
    private WebView mWebExplain;

    private RecyclerView mRecyclerView;
    private ArrayList<ListenResultContent> mResultList = new ArrayList<>();
    private ArrayList<String> mOptionContentList;
    private ArrayList<String> mOptionCodeList;
    private ResultContentAdapter mAdapter;

    private String page_current, page_total, page_question, question_type, answer_select, answer_real, time_count;

    public static FragmentDoResult newInstance(String page_current, String page_total, String page_question,
                                               ArrayList<String> option_content_list, ArrayList<String> option_code_list,
                                               String question_type, String answer_select, String answer_real, String time_count) {
        FragmentDoResult fragment = new FragmentDoResult();
        Bundle bundle = new Bundle();
        bundle.putString("page_current", page_current);
        bundle.putString("page_total", page_total);
        bundle.putString("page_question", page_question);
        bundle.putStringArrayList("option_content_list", option_content_list);
        bundle.putStringArrayList("option_code_list", option_code_list);
        bundle.putString("question_type", question_type);
        bundle.putString("answer_select", answer_select);
        bundle.putString("answer_real", answer_real);
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
            answer_real = getArguments().getString("answer_real");
            time_count = getArguments().getString("time_count");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen_do_result, container, false);

        initView(view);
        return view;
    }

    //TODO 不止一段录音的doResult
    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_do_result);
        mTxtPageMiddle = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_middle);
        mTxtPageRight = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_right);
        mTxtQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_title);
        mTxtTimeCount = (TextView) view.findViewById(R.id.txt_fragment_do_result_time_count);
        mWebExplain = (WebView) view.findViewById(R.id.txt_fragment_do_result_web);
        mWebExplain.loadData(StringUtil.ParaseToHtml("&lt;p&gt;\n" +
                "\t&lt;span style=&quot;font-size:10.5pt;font-family:&amp;quot;&quot;&gt;I\n" +
                "set an announcement for an event. And this morning I checked the events section\n" +
                "of the university\\'s website. And nothing, there is no mention of it&lt;/span&gt;\n" +
                "&lt;/p&gt;\n" +
                "&lt;p&gt;\n" +
                "\t&lt;span style=&quot;font-size:10.5pt;font-family:&amp;quot;&quot;&gt;选C。&lt;/span&gt;\n" +
                "&lt;/p&gt;"), "text/html; charset=UTF-8", null);

        mTxtPageMiddle.setText(page_current);
        mTxtPageRight.setText(page_total);
        mTxtQuestion.setText(page_question);
        mTxtTimeCount.setText("本题用时 " + time_count + " 秒");

        initData();

        //装载适配器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ResultContentAdapter(getActivity(), mResultList, question_type);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        LogUtil.i("question_type = " + question_type + "answer_select = " + answer_select + ",answer_option = " + answer_real);

        ListenResultContent result;
        switch (question_type) {
            case Constants.QUESTION_TYPE_JUDGE:
                //判断题
                String[] judgeSelectList = StringUtil.transferStringToArray(answer_select);
                String[] judgeAnswerList = StringUtil.transferStringToArray(answer_real);

                for (int i = 0; i < judgeSelectList.length; i++) {
                    result = new ListenResultContent();
                    result.judgeSelect = judgeSelectList[i].trim();
                    result.judgeAnswer = judgeAnswerList[i].trim();
                    if (result.judgeSelect.trim().contains(result.judgeAnswer.trim())) {
                        result.state = "true";
                    } else if (result.judgeSelect.contains("null")) {
                        result.state = "null";
                    } else {
                        result.state = "false";
                    }
                    result.content = mOptionContentList.get(i);
                    mResultList.add(result);
                }
                break;
            case Constants.QUESTION_TYPE_SORT:
                //排序
                String[] sortSelectList = StringUtil.transferStringToArray(answer_select);
                String[] sortAnswerList = StringUtil.transferStringToArray(answer_real);

                //TODO 内容重新排序或许可以用Collections的相关方法
                for (int i = 0; i < sortSelectList.length; i++) {
                    result = new ListenResultContent();
                    result.judgeSelect = sortSelectList[i].trim();
                    result.judgeAnswer = sortAnswerList[i].trim();
                    if (result.judgeSelect.contains(result.judgeAnswer.trim())) {
                        result.state = "true";
                    } else {
                        result.state = "false";
                    }
                    result.number = sortAnswerList[i];
                    result.content = mOptionContentList.get(i);
                    mResultList.add(result);
                }
                break;
            default:
                //单选和多选
                answer_real = StringUtil.transferNumberToAlpha(answer_real);
                answer_select = StringUtil.transferNumberToAlpha(answer_select);

                for (int i = 0; i < mOptionCodeList.size(); i++) {
                    result = new ListenResultContent();
                    result.number = mOptionCodeList.get(i);
                    result.content = mOptionContentList.get(i);
                    mResultList.add(result);
                }

                for (int i = 0; i < mOptionContentList.size(); i++) {
                    //遍历做判断,answer_option或answer_select包含该题，则修改该题的图标,先判断正确，后判断错误
                    if (answer_real.contains(mResultList.get(i).number)) {
                        mResultList.get(i).state = "true";
                    } else if (answer_select.contains(mResultList.get(i).number)) {
                        mResultList.get(i).state = "false";
                    } else {
                        mResultList.get(i).state = "none";
                    }
                }
                break;
        }
    }

}