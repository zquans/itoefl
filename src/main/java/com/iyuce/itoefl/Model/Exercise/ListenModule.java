package com.iyuce.itoefl.Model.Exercise;

import java.io.Serializable;

/**
 * Created by LeBang on 2017/3/7
 */
public class ListenModule implements Serializable {

    //Module和Classify通用属性
    public String name;
    public String practiced_count;
    public String total_count;

    //Classify 特有属性
    public String code_name;
    public String code;
    public String parent;
}