package com.iyuce.itoefl.UI.Main;

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

/**
 * Created by LeBang on 2017/1/22
 */
public class FragmentLecture extends Fragment {

    private WebView mWebView;

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
//        jsInterface = new JsInterface(getActivity());
//        // 设置JS的接口
//        web.addJavascriptInterface(jsInterface, "woyuce");

        //设置浏览器标识
//        String localVersion = PreferenceUtil.getSharePre(getActivity()).getString("localVersion", "2.8");
//        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + "; woyuce/" + localVersion);

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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
            }
        });
    }
}