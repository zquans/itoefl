package com.iyuce.itoefl.Control.Listening.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.iyuce.itoefl.Control.Listening.Adapter.ResultContentAdapter;
import com.iyuce.itoefl.Control.Listening.Adapter.ResultContentNestAdapter;
import com.iyuce.itoefl.Model.Exercise.ListenResultContent;
import com.iyuce.itoefl.Model.Exercise.QuestionNest;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.DbUtil;
import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.StringUtil;

import java.util.ArrayList;

public class FragmentDoResult extends Fragment {

    private TextView mTxtPageMiddle, mTxtPageRight, mTxtQuestion, mTxtAnswerHint, mTxtTimeCount;
    private WebView mWebExplain;

    private RecyclerView mRecyclerView;
    private ArrayList<ListenResultContent> mResultList = new ArrayList<>();
    private ArrayList<String> mOptionContentList;
    private ArrayList<String> mOptionCodeList;
    private ResultContentAdapter mAdapter;

    private String local_path, local_paper_code, question_id;
    private String page_current, page_total, page_question, question_type, detail, answer_select, answer_real, time_count;

    public static FragmentDoResult newInstance(String local_path, String local_paper_code, String question_id, String page_current, String page_total, String page_question,
                                               ArrayList<String> option_content_list, ArrayList<String> option_code_list,
                                               String question_type, String detail, String answer_select, String answer_real, String time_count) {
        FragmentDoResult fragment = new FragmentDoResult();
        Bundle bundle = new Bundle();
        bundle.putString("local_path", local_path);
        bundle.putString("local_paper_code", local_paper_code);
        bundle.putString("question_id", question_id);
        bundle.putString("page_current", page_current);
        bundle.putString("page_total", page_total);
        bundle.putString("page_question", page_question);
        bundle.putStringArrayList("option_content_list", option_content_list);
        bundle.putStringArrayList("option_code_list", option_code_list);
        bundle.putString("question_type", question_type);
        bundle.putString("detail", detail);
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
            local_path = getArguments().getString("local_path");
            local_paper_code = getArguments().getString("local_paper_code");
            question_id = getArguments().getString("question_id");
            page_current = getArguments().getString("page_current");
            page_total = getArguments().getString("page_total");
            page_question = getArguments().getString("page_question");
            mOptionContentList = getArguments().getStringArrayList("option_content_list");
            mOptionCodeList = getArguments().getStringArrayList("option_code_list");
            question_type = getArguments().getString("question_type");
            detail = getArguments().getString("detail");
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

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_fragment_do_result);
        mTxtPageMiddle = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_middle);
        mTxtPageRight = (TextView) view.findViewById(R.id.txt_fragment_do_result_page_right);
        mTxtQuestion = (TextView) view.findViewById(R.id.txt_fragment_do_result_title);
        mTxtAnswerHint = (TextView) view.findViewById(R.id.txt_fragment_do_result_answer_hint);
        mTxtTimeCount = (TextView) view.findViewById(R.id.txt_fragment_do_result_time_count);
        mWebExplain = (WebView) view.findViewById(R.id.txt_fragment_do_result_web);
        mWebExplain.setBackgroundColor(0); // 设置背景色
        mWebExplain.getBackground().setAlpha(0);
        mWebExplain.loadData(StringUtil.ParaseToHtml(detail), "text/html; charset=UTF-8", null);

        mTxtPageMiddle.setText(page_current);
        mTxtPageRight.setText(page_total);
        mTxtQuestion.setText(page_question);
        mTxtTimeCount.setText("本题用时 " + time_count + " 秒");

        initData();
    }

    private void initData() {
        LogUtil.i("question_type = " + question_type + ", answer_select = " + answer_select + ",answer_real = " + answer_real);
//        if (TextUtils.isEmpty(answer_select) || TextUtils.isEmpty(answer_real)) {
//            answer_real = "[2,1,0,3]";
////            return;
//        }
        ListenResultContent result;
        switch (question_type) {
            case Constants.QUESTION_TYPE_NEST:
                //嵌套题
                String[] nestSelectList = StringUtil.transferStringToArray(answer_select);
                String[] nestAnswerList = StringUtil.transferStringToArray(answer_real);
                ArrayList<QuestionNest> mNestContentList = new ArrayList<>();

                //TODO 数据源需要重新查表
                //数据源
                SQLiteDatabase mDatabase = DbUtil.getHelper(getActivity(), local_path + "/" + local_paper_code + ".sqlite").getWritableDatabase();
                //查表Child和Option
                Cursor cursor = mDatabase.query(Constants.TABLE_QUESTION_CHILD, null, Constants.MasterId + " =? ", new String[]{question_id}, null, null, null);
                //开启事务批量操作
                mDatabase.beginTransaction();
                if (cursor != null) {
                    QuestionNest questionNest;
                    while (cursor.moveToNext()) {
                        questionNest = new QuestionNest();
                        //内容
                        questionNest.content = cursor.getString(cursor.getColumnIndex(Constants.Content));
                        //子选项
                        String id_for_options = cursor.getString(cursor.getColumnIndex(Constants.ID));
                        questionNest.options = DbUtil.queryToArrayList(mDatabase, Constants.TABLE_OPTION, Constants.Content, Constants.QuestionId + " =? ", id_for_options);
                        mNestContentList.add(questionNest);
                    }
                    cursor.close();
                }
                //批量操作成功,关闭事务
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();
                mDatabase.close();

                for (int i = 0; i < mNestContentList.size(); i++) {
                    //三选一题
                    //0,1,2分别表示第一个，第二个，第三个
                    if (mNestContentList.get(i).options.size() == 3) {
                        if (nestSelectList[i].trim().equals("A")) {
                            mNestContentList.get(i).select = 0;
                        } else if (nestSelectList[i].trim().equals("B")) {
                            mNestContentList.get(i).select = 1;
                        } else if (nestSelectList[i].trim().equals("C")) {
                            mNestContentList.get(i).select = 2;
                        }
                        if (nestAnswerList[i].trim().equals("A")) {
                            mNestContentList.get(i).answer = 0;
                        } else if (nestAnswerList[i].trim().equals("B")) {
                            mNestContentList.get(i).answer = 1;
                        } else if (nestAnswerList[i].trim().equals("C")) {
                            mNestContentList.get(i).answer = 2;
                        }
                    } else {
                        //其他题
                        if (nestSelectList[i].trim().equals("A")) {
                            mNestContentList.get(i).select = 0;
                        } else {
                            mNestContentList.get(i).select = 1;
                        }
                        if (nestAnswerList[i].trim().equals("A")) {
                            mNestContentList.get(i).answer = 0;
                        } else {
                            mNestContentList.get(i).answer = 1;
                        }
                    }
                }


                //装载适配器
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                ResultContentNestAdapter mAdapter = new ResultContentNestAdapter(getActivity(), mNestContentList);
                mRecyclerView.setAdapter(mAdapter);
                return;
            case Constants.QUESTION_TYPE_JUDGE:
                //判断题
                String[] judgeSelectList = StringUtil.transferStringToArray(answer_select);
                String[] judgeAnswerList = StringUtil.transferStringToArray(answer_real);

                for (int i = 0; i < judgeSelectList.length; i++) {
                    result = new ListenResultContent();
                    result.judgeSelect = judgeSelectList[i].trim();
                    result.judgeAnswer = judgeAnswerList[i].trim();
                    if (result.judgeSelect.trim().contains(result.judgeAnswer.trim())) {
                        result.state = Constants.TRUE;
                    } else if (result.judgeSelect.contains(Constants.NULL)) {
                        result.state = Constants.NULL;
                    } else {
                        result.state = Constants.FALSE;
                    }
                    result.content = mOptionContentList.get(i);
                    mResultList.add(result);
                }
                break;
            case Constants.QUESTION_TYPE_SORT:
                //排序
                String[] sortSelectList = StringUtil.transferStringToArray(answer_select);
                String[] sortAnswerList = StringUtil.transferStringToArray(answer_real);

                //借助一个中间帮助数组，克隆mOptionContentList
                ArrayList<String> helpList = new ArrayList<>();
                for (int i = 0; i < mOptionContentList.size(); i++) {
                    helpList.add(mOptionContentList.get(i));
                }
                //按用户的选择重新排列顺序
                for (int i = 0; i < sortSelectList.length; i++) {
                    int j = Integer.parseInt(sortSelectList[i].trim());
                    helpList.set(i, mOptionContentList.get(j));
                    // LogUtil.i("j = " + j);
                }

                for (int i = 0; i < sortSelectList.length; i++) {
                    result = new ListenResultContent();
                    result.judgeSelect = sortSelectList[i].trim();
                    result.judgeAnswer = sortAnswerList[i].trim();
                    if (StringUtil.transferNumberToAlpha(result.judgeSelect)
                            .contains(result.judgeAnswer.trim())) {
                        result.state = Constants.TRUE;
                    } else {
                        result.state = Constants.FALSE;
                    }
//                    result.number = StringUtil.transferNumberToAlpha(sortAnswerList[i]);
                    result.content = helpList.get(i);
                    mResultList.add(result);
                    mTxtAnswerHint.setVisibility(View.VISIBLE);
                    mTxtAnswerHint.setText("正确排序是" + StringUtil.transferNumberToAlpha(answer_real));
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
                        mResultList.get(i).state = Constants.TRUE;
                    } else if (answer_select.contains(mResultList.get(i).number)) {
                        mResultList.get(i).state = Constants.FALSE;
                    } else {
                        mResultList.get(i).state = Constants.NONE;
                    }
                }
                break;
        }
        //装载适配器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ResultContentAdapter(getActivity(), mResultList, question_type);
        mRecyclerView.setAdapter(mAdapter);
    }

}