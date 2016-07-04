package com.zyk.launcher3.setting;

import android.app.Activity;
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

    private void setWebView(){
        WebSettings ws = mWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
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
