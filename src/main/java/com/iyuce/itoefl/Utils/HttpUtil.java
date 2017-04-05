package com.iyuce.itoefl.Utils;

import android.text.TextUtils;

import com.iyuce.itoefl.Utils.Interface.Http.DownLoadInterface;
import com.iyuce.itoefl.Utils.Interface.Http.RequestInterface;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2017/2/16
 */
public class HttpUtil {

    /**
     * Get请求
     */
    public static void get(String url, final RequestInterface requestInterface) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkGo.post(url).execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                requestInterface.doSuccess(s, call, response);
            }
        });
    }

    /**
     * Post请求
     */
    public static void post(String url, HttpHeaders headers, HttpParams params, final RequestInterface requestInterface) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkGo.post(url).headers(headers).params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        requestInterface.doSuccess(s, call, response);
                    }
                });
    }

    /**
     * Get请求,返回文件
     */
    public static void downLoad(String url, final String path, final DownLoadInterface downLoadInterface) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OkGo.get(url).execute(new FileCallback(path, "") {
            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                downLoadInterface.inProgress(currentSize, totalSize, progress, networkSpeed);
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                downLoadInterface.doSuccess(file, call, response);
            }

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                downLoadInterface.onBefore();
            }

            @Override
            public void onAfter(File file, Exception e) {
                super.onAfter(file, e);
                downLoadInterface.onAfter();
            }
        });
    }
}