package com.iyuce.itoefl.UI.Listening.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuce.itoefl.UI.Listening.Adapter.QuestionAdapter;

public class FragmentDoQuestionDefault extends Fragment implements QuestionAdapter.OnQuestionItemClickListener {

    //提供给Activity用于判断是否播放录音完毕
    public boolean isFinish = true;

    //提供给Activity一个默认答案，如果为空则未答完，不让进入下一题
    public String answerDefault;

    public String selectAnswer() {
        return answerDefault;
    }

    public boolean finishMediaPlayer() {
        return isFinish;
    }

    //获取到的参数
    public static FragmentDoQuestionDefault newInstance(String total_question, String current_question,
                                                        String current_music, String current_question_id,
                                                        String local_path, String local_paper_code) {
        FragmentDoQuestionDefault fragment = new FragmentDoQuestionDefault();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return null;
    }

    //Adapter提供给Fragment的方法
    @Override
    public void onQuestionClick(int pos) {
    }
}