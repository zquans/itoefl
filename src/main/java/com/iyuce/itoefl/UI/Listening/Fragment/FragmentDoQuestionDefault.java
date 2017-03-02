package com.iyuce.itoefl.UI.Listening.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.R;
import com.iyuce.itoefl.UI.Listening.Adapter.QuestionAdapter;

public class FragmentDoQuestionDefault extends Fragment implements QuestionAdapter.OnQuestionItemClickListener {

    //接收参数
    private String total_question, current_question, current_music, current_question_id, local_path, local_paper_code;

    //提供给Activity用于判断是否播放录音完毕
    private boolean isFinish = true;

    //提供给Activity一个默认答案，如果为空则未答完，不让进入下一题
    private String answerDefault;

    public String selectAnswer() {
        return answerDefault;
    }

    public boolean finishMediaPlayer() {
        return isFinish;
    }

    //获取到的参数  QuestionId(用于在Fragment中继续查表)    Sort题号     MusicQuestion音频
    public static FragmentDoQuestionDefault newInstance(String total_question,
                                                        String current_question, String current_music, String current_question_id, String local_path, String local_paper_code) {
        FragmentDoQuestionDefault fragment = new FragmentDoQuestionDefault();
        Bundle args = new Bundle();
        args.putString("total_question", total_question);
        args.putString("current_question", current_question);
        args.putString("current_music", current_music);
        args.putString("current_question_id", current_question_id);
        args.putString("local_path", local_path);
        args.putString("local_paper_code", local_paper_code);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            total_question = getArguments().getString("total_question");
            current_question = getArguments().getString("current_question");
            current_music = getArguments().getString("current_music");
            current_question_id = getArguments().getString("current_question_id");
            local_path = getArguments().getString("local_path");
            local_paper_code = getArguments().getString("local_paper_code");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen_do_question, container, false);

        return view;
    }

    //Adapter提供给Fragment的方法
    @Override
    public void onQuestionClick(int pos) {
    }
}