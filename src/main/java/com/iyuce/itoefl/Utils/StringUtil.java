package com.iyuce.itoefl.Utils;

/**
 * Created by LeBang on 2017/2/27
 */
public class StringUtil {

    public static String ParaseToHtml(String orginal) {
        return orginal.replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&nbsp;", "\r");
    }
}