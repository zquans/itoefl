package com.iyuce.itoefl.Utils;

/**
 * Created by LeBang on 2017/2/27
 */
public class StringUtil {

    public static String ParaseToHtml(String orginal) {
        orginal = orginal + "&lt;style type=&quot;text/css&quot;&gt;html{font-size:65%;color:#fcfcfc}" +
                "*{font-size:1rem !important;background:none !important}&lt;/style&gt;\n";
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
                .replace("A", "0")
                .replace("B", "1")
                .replace("C", "2")
                .replace("D", "3")
                .replace("E", "4");
    }

    public static String trimAll(String orginal) {
        return orginal.replace(" ", "");
    }

    public static String transferStringListToString(String orginla) {
        return orginla.replace(",", "")
                .replace("[", "")
                .replace("]", "")
                .replace("0", "A")
                .replace("1", "B")
                .replace("2", "C")
                .replace("3", "D")
                .replace("4", "E")
                .replace(" ", "");
    }

    public static String transferBooleanToAlpha(String orginal) {
        return orginal.replace(" ", "")
                .replace("FALSE", "B")
                .replace("TRUE", "A")
                .replace("false", "B")
                .replace("true", "A");
    }

    public static String transferBooleanToNest(String orginal) {
        return orginal.replace(" ", "")
                .replace("TRUE", "A")
                .replace("NULL", "B")
                .replace("FALSE", "C")
                .replace("true", "A")
                .replace("null", "B")
                .replace("false", "C");

  
    public static String changeSpecial(String orginal) {
        return orginal.replace("\"", "“")
                .replace("\'", "‘");
    }

    public static String[] transferStringToArray(String orginal) {
        return orginal.replace("[", " ")
                .replace("]", " ")
                .trim()
                .split(",");
    }
}