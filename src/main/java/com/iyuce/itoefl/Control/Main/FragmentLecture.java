package com.iyuce.itoefl.Control.Main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.R;
import com.iyuce.itoefl.Utils.Interface.JsInterface;

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentLecture extends Fragment {

    private WebView mWebView;
    private JsInterface mJsInterface;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_lecture, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mWebView = (WebView) view.findViewById(R.id.web_fragment_lecture);
        mWebView.loadUrl(Constants.URL_WEB_ONE_TO_ONE);

        // 允许JS交互
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 实例化接口JsInterface
        mJsInterface = new JsInterface(getActivity());
//        // 设置JS的接口
        mWebView.addJavascriptInterface(mJsInterface, "woyuce");

        //设置浏览器标识
//        String localVersion = PreferenceUtil.getSharePre(getActivity()).getString("localVersion", "2.8");
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + "; woyuce/" + 2.8);

        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

		/* 设置缓存相关 */
        // web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // web.getSettings().setDomStorageEnabled(true);
        // web.getSettings().setDatabaseEnabled(true);
        // String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() +
        // "/webcachetab2";
        // LogUtil.e("tab3 cache", "cacheDirPath=" + cacheDirPath);
        // web.getSettings().setDatabasePath(cacheDirPath);
        // web.getSettings().setAppCachePath(cacheDirPath);
        // web.getSettings().setAppCacheEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("file:///android_asset/index.html");
            }
        });
    }
}