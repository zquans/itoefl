package com.iyuce.itoefl.Utils;

import java.io.File;

/**
 * Created by Administrator on 2017/3/15
 */
public class FileUtil {

    /**
     * 获取文件夹大小
     */
    public static long getFileSize(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    public static String dealLength(long length) {
        String size;
        if (length > 1048576) {
            size = length / 1048576 + "MB";
        } else if (length > 1024) {
            size = length / 1024 + "KB";
        } else {
            size = length + "KB";
        }
        return size;
    }

    public static String dealPdfLength(long length) {
        String size;
        if (length > 1048576) {
            size = length / 1048576 + "GB";
        } else if (length > 1024) {
            size = length / 1024 + "MB";
        } else {
            size = length + "B";
        }
        return size;
    }
}