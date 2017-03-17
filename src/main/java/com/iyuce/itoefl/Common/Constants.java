package com.iyuce.itoefl.Common;

/**
 * Created by LeBang on 2017/1/23
 */
public class Constants {

    //权限
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 0x0002;

    public static final String URL_DOWNLOAD_MAIN_DATABASE = "http://tpo.iyuce.com/Content/upload/TPO/tpo.zip";
    public static final String URL_TPO_MAIN_STATUS = "http://api.iyuce.com/v1/tpo/gettpomainstatus";
    public static final String URL_WEB_ONE_TO_ONE = "http://www.iyuce.com/m/appjxy.html";
    public static final String URL_TO_SUGGESTION = "http://api.iyuce.com/v1/service/feedback";
    public static final String URL_CHECK_UPDATE = "http://www.iyuce.com/Scripts/andoird.json";

    //APP包名
    public static final String AppPackageName = "com.woyuce.activity";
    public static final String AppName = "ITOEFL";

    public static final String NONE = "none";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String NULL = "null";

    //题型
    public static final String QUESTION_TYPE_SINGEL = "SINGLE";
    public static final String QUESTION_TYPE_MULTI = "MULTI";
    public static final String QUESTION_TYPE_JUDGE = "JUDGE";
    public static final String QUESTION_TYPE_SORT = "SORT";
    public static final String QUESTION_TYPE_NEST = "NEST";

    //音频状态
    public static final int FLAG_AUDIO_PLAY = 0;
    public static final int FLAG_AUDIO_PAUSE = 1;

    //文件夹路径相关
    public static final String FILE_PATH_ITOEFL = "/ITOEFL";        //根路径下一级路径
    public static final String FILE_PATH_ITOEFL_EXERCISE = "/ITOEFL/EXERCISE";  //根路径下二级路径

    //数据库相关
    public static final int DATABASE_VERSION = 1;                   //默认版本号
    public static final String SQLITE_TPO = "TPO.sqlite";           //根库
    public static final String SQLITE_DOWNLOAD = "DOWNLOAD.db";   //用户操作记录库
    public static final String TABLE_ALREADY_DOWNLOAD = "downloaded_table";  //下载记录表
    public static final String TABLE_ALREADY_PRACTICED = "practiced_table";  //练习记录表

    //Exercise 相关的表
    public static final String TABLE_SQLITE_MASTER = "sqlite_master";   //sql系统表
    public static final String TABLE_PAPER = "Iup_TpoPaper";
    public static final String TABLE_PAPER_RULE = "Iup_TpoPaperRule";
    public static final String TABLE_QUESTION = "Iup_TpoQuestion";
    public static final String TABLE_OPTION = "Iup_TpoOption";
    public static final String TABLE_QUESTION_CHILD = "Iup_TpoQuestion_Child";

    //Exercise 相关表中的字段column
    public static final String TABLE_NAME = "tbl_name";  //sql系统表中的字段
    public static final String NAME = "name";            //sql系统表中的字段
    public static final String ID = "Id";
    public static final String PaperName = "PaperName";
    public static final String PaperId = "PaperId";
    public static final String PaperCode = "PaperCode";
    public static final String DownUrl = "DownUrl";
    public static final String DownTime = "DownTime";
    public static final String RuleTitle = "RuleTitle";
    public static final String RuleTitle_En = "RuleTitle_En";
    public static final String QuestionGrade = "QuestionGrade";
    public static final String MusicPicture = "MusicPicture";
    public static final String QuestionType = "QuestionType";
    public static final String RuleName = "RuleName";
    public static final String QuestionCount = "QuestionCount";
    public static final String Sort = "Sort";
    public static final String MusicQuestion = "MusicQuestion";
    public static final String QuestionId = "QuestionId";
    public static final String PaperRuleId = "PaperRuleId";
    public static final String MusicAnswer = "MusicAnswer";
    public static final String MasterId = "MasterId";
    public static final String Content = "Content";
    public static final String Answer = "Answer";
    public static final String Detail = "Detail";
    public static final String Code = "Code";

    //自建的表中的column字段
    public static final String SECTION = "Section";
    public static final String MODULE = "Module";
    public static final String DOWNLOAD = "Download";
    public static final String LOADING = "Loading";
    public static final String Practiced = "Practiced";
    public static final String UserSelect = "UserSelect";
    public static final String Bingo = "Bingo";
    public static final String TimeCount = "TimeCount";
}