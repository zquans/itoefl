package com.iyuce.itoefl.Utils.Interface;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.iyuce.itoefl.Utils.LogUtil;
import com.iyuce.itoefl.Utils.ToastUtil;

/**
 * Created by LeBang on 2016/12/22
 */
public class JsInterface {

    private Context mContext;

    public JsInterface(Context context) {
        this.mContext = context;
    }

    // js调用java
    @JavascriptInterface
    public void openExternalApp(final String url) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
//                String url = "alipays://";  //支付宝
//                String url = "weixin://";   //微信
//                String url = "mqqwpa://";   //腾讯QQ

                //主线程更新UI
                LogUtil.i("JS 调用了 JAVA --->" + url);
                //判断微信
                ToDoOrNotToDoJs(url, "weixin", "com.tencent.mm", "微信");
                //判断QQ
                ToDoOrNotToDoJs(url, "mqqwpa", "com.tencent.mobileqq", "QQ");
//                判断支付宝
//                ToDoOrNotToDoJs(url, "alipays", "com.eg.android.AlipayGphone", "支付宝");
            }
        });
    }

    /**
     * 判断是否去做JS需要调用的操作
     */
    private void ToDoOrNotToDoJs(String url, String url_head, String package_name, String toast_content) {
        if (url.contains(url_head)) {
            boolean isExist = isApkInstalled(mContext, package_name);
            if (!isExist) {
                ToastUtil.showMessage(mContext, "亲,请先安装" + toast_content);
            } else {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        }
    }

    /**
     * packageName="com.tencent.mobileqq" QQ包名
     * "com.tencent.mm" 微信包名
     *
     * @return
     */
    public static boolean isApkInstalled(Context context, String packageName) { // 该方法判断手机中是否有对应应用包
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}