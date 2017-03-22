package com.iyuce.itoefl.Model.Exercise;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LeBang on 2017/3/21
 */
public class QuestionNest implements Serializable {

    public String content;
    public int answer;
    public int select;
    public ArrayList<String> options;
}