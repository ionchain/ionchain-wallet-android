package org.ionchain.wallet.mvp.view.activity.webview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseCommonTitleTwoActivity;

import static org.ionchain.wallet.constant.ConstantParams.TX_HASH;

public class TxRecordBrowserActivity extends AbsBaseCommonTitleTwoActivity implements OnRefreshListener {
    private WebView mWebView;
    private String url;

    private String hash;
    SmartRefreshLayout smartRefreshLayout;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_recore_browser;
    }

    @Override
    protected void initView() {
        mWebView = findViewById(R.id.tx_record_browser);
        smartRefreshLayout = findViewById(R.id.smart_refresh_layout);
    }

    @Override
    protected void setListener() {
        super.setListener();
        smartRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        //不现实水平滚动条
        mWebView.setHorizontalScrollBarEnabled(false);
        //不现实垂直滚动条
        mWebView.setVerticalScrollBarEnabled(true);

        //获取触摸焦点
        mWebView.requestFocusFromTouch();
        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setDomStorageEnabled(true);//主要是这句
        webSettings.setJavaScriptEnabled(true);//启用js
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(false); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //5.0及以上webview不支持http和https混合模式，需要通过配置来开启混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (BuildConfig.APP_DEBUG) {
            url = "http://192.168.23.142:3001/v1/general?queryValue=";
        } else {
            url = "http://explorer.ionchain.org/v1/general?queryValue=";
        }
        url = url + hash;
        LoggerUtils.i("url ", url);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                smartRefreshLayout.finishRefresh();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null) {
                    setActivityTitle(title);
                }
            }
        });
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        hash = intent.getStringExtra(TX_HASH);
    }

    @Override
    protected String getTitleName() {
        return "";
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mWebView.reload();
    }
}
