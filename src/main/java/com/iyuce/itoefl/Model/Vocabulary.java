package com.iyuce.itoefl.Model;

import java.io.Serializable;

/**
 * Created by LeBang on 2017/4/5
 */
public class Vocabulary implements Serializable {

    public String title;
    public String description;
    public String img;
    public String path;
    public String modify_at;
    public String book_mark;
    public long size;
    public int key;
    public boolean isDownLoad;
    public boolean downLoading = false;
}
