package com.iyuce.itoefl.Common;

/**
 * Created by LeBang on 2017/1/23
 */
public class Constants {

    public static final String URL_WEB_ONE_TO_ONE = "http://www.iyuce.com/m/appjxy.html";

    //音频状态
    public static final int FLAG_AUDIO_PLAY = 0;
    public static final int FLAG_AUDIO_PAUSE = 1;

    //文件夹路径相关
    public static final String FILE_PATH_ITOEFL = "/ITOEFL";
    public static final String FILE_PATH_ITOEFL_EXERCISE = "/ITOEFL/EXERCISE";

    //数据库相关
    //TODO  约定好的表名及字段可以作为常量封装在此
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_ALREADY_DOWNLOAD = "downloaded_table";
    public static final String _ID = "_id";

    public static final String TABLE_USER = "usertable";
    public static final String TABLE_USER_NAME = "name";
    public static final String TABLE_USER_PASSWORD = "password";
}