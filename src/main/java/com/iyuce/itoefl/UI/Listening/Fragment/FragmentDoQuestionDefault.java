package com.iyuce.itoefl.UI.Listening.Fragment;

import android.support.v4.app.Fragment;

public class FragmentDoQuestionDefault extends Fragment {

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
}