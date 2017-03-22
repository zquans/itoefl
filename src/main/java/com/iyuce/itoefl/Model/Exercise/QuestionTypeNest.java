package com.iyuce.itoefl.Model.Exercise;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/8
 */
public class QuestionTypeNest {

    //选择题用
    public String number;
    public String content;
    public String state;
    public ArrayList<QuestionChild> questionChildList;

    //内部类
    private class QuestionChild {
        public String content;
        public String answer;
        public String masterId;
        public String sort;
        public ArrayList<ChildOption> childOptionList;

        //内部类的内部类
        private class ChildOption {
            public String questionId;
            public String content;
            public String code;
            public String sort;
        }
    }
}