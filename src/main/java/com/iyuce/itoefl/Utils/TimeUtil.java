package com.iyuce.itoefl.Utils;

/**
 * Created by LeBang on 2017/2/7
 */
public class TimeUtil {

    public static String toTimeShow(int time) {
        int h, m, s;
        h = time / 60 / 60;
        m = (time - 60 * (h * 60)) / 60;
        s = time % 60;
        if (m < 10 && s < 10) {
            return "0" + m + ":0" + s;
        } else if (m < 10 && s >= 10) {
            return "0" + m + ":" + s;
        } else if (m >= 10 && s < 10) {
            return m + ":0" + s;
        } else {
            return m + ":" + s;
        }
    }
}
