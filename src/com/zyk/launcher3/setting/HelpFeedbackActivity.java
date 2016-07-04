package com.zyk.launcher3.setting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.zyk.launcher3.R;

/**
 * Created by zyk on 2016/7/3.
 */
public class HelpFeedbackActivity extends Activity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback);
        mWebView = (WebView) findViewById(R.id.activity_help_feedback_webview);
        setWebView();
        mWebView.loadUrl("https://yukun314.github.io/launcher3/help.html");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setWebView() {
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
//        if(isNetworkAvailable() | isWifiConnected()){
//            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
//        }else {
//            ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }

        //FIXME 为了测试方便 暂时使用只使用网络
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 开启 DOM storage API 功能
        ws.setDomStorageEnabled(true);
        // 开启 database storage API 功能
        ws.setDatabaseEnabled(true);
        // 开启 Application Caches 功能
        ws.setAppCacheEnabled(true);

        //设置适应屏幕
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setBackgroundColor(Color.argb(0, 0, 0, 0));
    }

    /**
     * 判断是否有网络连接
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());//isAvailable
    }

    //是否连接WIFI
    private boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 帮助 WebView 处理各种通知、请求事件等
     */
    private class MyWebViewClient extends WebViewClient {

        //        如果页面中链接,如果希望点击链接继续在当前browser中响应,而不是新开Android的系统browser中响应该链接,必须覆盖 WebView的WebViewClient对象.
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
//            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    /**
     * 辅助 WebView 处理 Javascript 的对话框,网站图标,网站 title
     */
    private class MyWebChromeClient extends WebChromeClient {

    }
}
