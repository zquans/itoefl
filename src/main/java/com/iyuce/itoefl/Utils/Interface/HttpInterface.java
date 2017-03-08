package com.iyuce.itoefl.Utils.Interface;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2017/2/16
 */
public interface HttpInterface {

    void inProgress(long currentSize, long totalSize, float progress, long networkSpeed);

    void doSuccess(File file, Call call, Response response);

    void onBefore();

    void onAfter();
}