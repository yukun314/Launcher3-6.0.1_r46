package com.zyk.launcher3.setting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zyk.launcher3.R;
import com.zyk.launcher3.config.Config;
import com.zyk.launcher3.json.Content;
import com.zyk.launcher3.json.Contents;
import com.zyk.launcher3.json.Version;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyk on 2016/7/3.
 */
public class HelpFeedbackActivity extends Activity {
    private String filePath ;
    private ProgressBar progressbar;
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_feedback);
        mWebView = (WebView) findViewById(R.id.activity_help_feedback_webview);
        progressbar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 3));
        mWebView.addView(progressbar);
        initNavigation();
        setWebView();
        File file = getExternalFilesDir("");
        filePath = file.getAbsolutePath();
        File helpFile = new File(filePath,"help.html");
        if(helpFile.exists()){
            String data = "";
            try {
                FileInputStream fis = new FileInputStream(helpFile);
                StringBuffer out = new StringBuffer();
                byte[] b = new byte[1024];
                for (int n; (n = fis.read(b)) != -1; ) {
                    out.append(new String(b, 0, n));
                }
                fis.close();
                data = new String(out.toString().getBytes(),"UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String baseURL = "file://"+filePath+"/";
            System.out.println("baseURL:"+baseURL);
            mWebView.loadDataWithBaseURL(baseURL, data, "text/html", "utf-8",null);
        }else{
            mWebView.loadUrl("https://yukun314.github.io/launcher3/help.html");
        }

        loadData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = downloadData(Config.baseURL + "/version.json");
                if (str != null && !str.equals("error")) {
                    Gson json = new Gson();
                    Version version = json.fromJson(str, Version.class);
                    int oldVersion = toVersion(filePath);
                    if (oldVersion < version.version) {//有新版本
                        //把version保存到本地
                        version.savaFile(filePath, "version.json");
                        String cs = downloadData(Config.baseURL + "/" + version.url);
                        Gson cjson = new Gson();
                        Type type = new TypeToken<List<Content>>() {
                        }.getType();
                        List<Content> contents = cjson.fromJson(cs, type);
                        System.out.println("contents.size:" + contents.size());
                        for (Content c : contents) {
                            downloadAndSaveData(Config.baseURL + "/" + c.url, filePath + "/" + c.url);
                        }
                        System.out.println("cs:" + cs);
                    }
                }
            }
        }.start();
    }

    private int toVersion(String filePath){
        int version = 0;
        try {
            File file = new File(filePath,"version.json");
            if(file!= null && file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                Version v = (Version) ois.readObject();
                version = v.version;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    private String downloadData(String urlString) {
        try {
            URL url = new URL(urlString);
            //打开到url的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //以下为java IO部分，大体来说就是先检查文件夹是否存在，不存在则创建,然后的文件名重复问题，没有考虑
            InputStream in = connection.getInputStream();
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[512];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            in.close();
            return out.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * @param urlString 文件的网络全路径
     * @param filePath 保存到本地的全路径
     */
    private void downloadAndSaveData(String urlString, String filePath) {
        OutputStream output = null;
        InputStream in = null;
        try {
            URL url = new URL(urlString);
            //打开到url的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //以下为java IO部分，大体来说就是先检查文件夹是否存在，不存在则创建,然后的文件名重复问题，没有考虑
            in = connection.getInputStream();
            File file = new File(filePath);
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if(!file.exists()) {
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            byte[] buffer = new byte[1024*5];
            int length;
            while((length=(in.read(buffer))) >0){
                output.write(buffer,0,length);
            }
            //下面的方法会导致文件的内容改变
//            while (in.read(buffer) != -1) {
//                output.write(buffer);
//            }
            output.flush();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(output != null) {
                    output.close();
                }
                if(in != null ) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void initNavigation(){
        View view = findViewById(R.id.activity_help_feedback_navigation);
        ImageView back = (ImageView) view.findViewById(R.id.activity_navigation_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView message = (TextView) view.findViewById(R.id.activity_navigation_message);
        message.setText(R.string.help_button_text);
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

        ws.setAllowFileAccess(true);// 设置允许访问文件数据
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
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(View.GONE);
            } else {
                if (progressbar.getVisibility() == View.GONE)
                    progressbar.setVisibility(View.VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }


    }
}
