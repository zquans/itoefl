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

    public static String transferNumberToAlpha(String orginal) {
        return orginal.replace("0", "A")
                .replace("1", "B")
                .replace("2", "C")
                .replace("3", "D")
                .replace("4", "E");
    }

    public static String transferAlpahToNumber(String orginal) {
        return orginal.replace(",", "")
                .replace("A", "1")
                .replace("B", "1")
                .replace("C", "2")
                .replace("D", "3")
                .replace("E", "4");
    }

    public static String trimAll(String orginal) {
        return orginal.replace(" ", "");
    }

    public static String[] transferStringToArray(String orginal) {
        return orginal.replace("[", " ")
                .replace("]", " ")
                .trim()
                .split(",");
    }
}