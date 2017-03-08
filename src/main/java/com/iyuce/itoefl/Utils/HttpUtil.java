package com.iyuce.itoefl.Utils;

import com.iyuce.itoefl.Utils.Interface.HttpInterface;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by LeBang on 2017/2/16
 */
public class HttpUtil {

    public static void downLoad(String url,final String path, final HttpInterface mHttpInterface) {
//        String url;
//        if (pos % 2 == 0) {
//            url = "http://img.enhance.cn/toefl/zip/listenaudiozip/1402.zip";
//        } else {
//            url = "http://xm.iyuce.com/app/TPO18_L1.zip";
//        }
//        OkGo.get("http://img.enhance.cn/toefl/zip/listenaudiozip/1402.zip")
//        OkGo.get("http://xm.iyuce.com/app/TPO18L1.zip")
        OkGo.get(url)
                .execute(new FileCallback(path, "") {
                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                        mHttpInterface.inProgress(currentSize, totalSize, progress, networkSpeed);
                    }

                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        mHttpInterface.doSuccess(file, call, response);
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        mHttpInterface.onBefore();
                    }

                    @Override
                    public void onAfter(File file, Exception e) {
                        super.onAfter(file, e);
                        mHttpInterface.onAfter();
                    }
                });
    }
}