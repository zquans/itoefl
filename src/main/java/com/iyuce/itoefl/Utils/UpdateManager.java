package com.iyuce.itoefl.Utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Response;

/**
 * LeBang
 * 2016-9-30
 */
public class UpdateManager {

    private ProgressBar mProgressBar;
    private Dialog mDownLoadDialog;

    private static final int DOWNLOADING = 1;
    private static final int DOWNLOAD_FINISH = 0;

    private String mVersion;
    private String mVersionURL;
    private String mMessage;
    private String mSavePath;
    private int mProgress;
    private boolean mIsCancel = false;

    private Context mContext;

    public UpdateManager(Context context) {
        mContext = context;
    }

    private Handler mUpdateProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    mProgressBar.setProgress(mProgress);
                    break;
                case DOWNLOAD_FINISH:
                    mDownLoadDialog.dismiss();
                    installAPK();
                    break;
            }
        }
    };

    public void checkUpdate() {
        OkGo.get(Constants.URL_CHECK_UPDATE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        doCheckSuccess(s);
                    }
                });
    }

    private void doCheckSuccess(String s) {
        try {
//            String parseString = new String(jsonString.getBytes("ISO-8859-1"), "utf-8");
            JSONObject obj;
            obj = new JSONObject(s);
            if (obj.getString(Constants.CODE_HTTP).equals(Constants.CODE_HTTP_SUCCESS)) {
                JSONArray data = obj.getJSONArray(Constants.DATA_HTTP);
                obj = data.getJSONObject(0);
                mVersion = obj.getString("version");
                mVersionURL = obj.getString("apkurl");
                mMessage = obj.getString("detail");
//                LogUtil.e("URL = " + mVersionURL + ",mVersion =" + mVersion + ",mMessage = " + mMessage);
            }
            if (isUpdate()) {
                showNoticeDialog();
            } else {
                ToastUtil.showMessage(mContext, "don't need update");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //比较本地版本是否需要更新
    public boolean isUpdate() {
        float serverVersion = Float.parseFloat(mVersion);
        PreferenceUtil.save(mContext, Constants.Preference_Version_Service, String.valueOf(serverVersion));
        String localVersion = null;

        try {
            //调用系统方法获取versionName作比较
            localVersion = mContext.getPackageManager().getPackageInfo(Constants.AppPackageName, 0).versionName;
            PreferenceUtil.save(mContext, Constants.Preference_Version_Local, localVersion);
            LogUtil.e("localVersion = " + localVersion + ",serverVersion = " + serverVersion);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return serverVersion > Float.parseFloat(localVersion);
    }

    protected void showNoticeDialog() {     //show 弹窗供选择是否更新
        Builder builder = new Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("发现新版本");
        builder.setMessage(mMessage);
        builder.setPositiveButton("更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("下次再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void showDownloadDialog() {     //显示下载进度
        Builder builder = new Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("下载中");

        View view = LayoutInflater.from(mContext).inflate(R.layout.style_dialog_progress, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.update_progress);
        builder.setView(view);
        builder.setNegativeButton("取消下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mIsCancel = true;
            }
        });
        mDownLoadDialog = builder.create();
        mDownLoadDialog.show();

        //下载文件
        downloadAPK();
    }

    //文件下载的操作   1.存储卡    2.输入流
    private void downloadAPK() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        mSavePath = SDCardUtil.getItoeflPath();
                        File dir = new File(mSavePath);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        HttpURLConnection conn = (HttpURLConnection) new URL(mVersionURL).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        int length = conn.getContentLength();

                        File apkFile = new File(mSavePath, Constants.AppName + mVersion);
                        FileOutputStream fos = new FileOutputStream(apkFile);

                        int count = 0;
                        byte[] buffer = new byte[1024];

                        while (!mIsCancel) {
                            int numread = is.read(buffer);
                            count += numread;
                            mProgress = (int) (((float) count / length) * 100);
                            // 更新进度条
                            mUpdateProgressHandler.sendEmptyMessage(DOWNLOADING);
                            // 下载完成
                            if (numread < 0) {
                                mUpdateProgressHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                                break;
                            }
                            fos.write(buffer, 0, numread);
                        }
                        fos.close();
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //安装下载好的APK
    private void installAPK() {
        //移除引导值，使下一次运行仍有引导画面
        PreferenceUtil.removefirstguide(mContext);
        File apkFile = new File(mSavePath, Constants.AppName + mVersion);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("file://" + apkFile.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}