package org.ionchain.wallet.mvp.view.activity.webview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import static org.ionchain.wallet.App.isCurrentLanguageZN;
import static org.ionchain.wallet.constant.ConstantParams.URL_REQUEST_TYPE;
import static org.ionchain.wallet.constant.ConstantParams.URL_TAG_PROTOCOL;

public class WebActivity extends AbsBaseActivity implements OnRefreshListener {

    private static final String NET_ERR_INTERNET_DISCONNECTED = "net::ERR_INTERNET_DISCONNECTED";
    RelativeLayout mNetErrorHintPage;
    WebView mWebView;
    SmartRefreshLayout smartRefreshLayout;
    private boolean request_error = false;


    /**
     * 请求类型
     */
    private char mRequestType = URL_TAG_PROTOCOL;
    /**
     * web view title
     */
    private TextView mWebTitle;

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mRequestType = intent.getCharExtra(URL_REQUEST_TYPE, mRequestType);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initData() {
        smartRefreshLayout.setOnRefreshListener(this);
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
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //5.0及以上webview不支持http和https混合模式，需要通过配置来开启混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (isCurrentLanguageZN()) {
            if (mRequestType == URL_TAG_PROTOCOL) {
                 LoggerUtils.i("webview", "URL_TAG_PROTOCOL = " + URL_TAG_PROTOCOL);
                mWebView.loadUrl(BuildConfig.URL_PROTOCOL_CN); //加载协议
            } else {
                mWebView.loadUrl(BuildConfig.URL_ABOUTUS_CN); //加载关于我们
            }

        } else {
            if (mRequestType == URL_TAG_PROTOCOL) {
                mWebView.loadUrl(BuildConfig.URL_PROTOCOL_EN); //加载协议

            } else {
                mWebView.loadUrl(BuildConfig.URL_ABOUT_US_EN); //加载关于我们
            }

        }

    }

    @Override
    protected void setListener() {
        super.setListener();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showProgress(getAppString(R.string.app_loading));
                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                request_error = true;

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (NET_ERR_INTERNET_DISCONNECTED.contentEquals(error.getDescription())) {
                    request_error = true;
                }
//                hideProgress();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed(); //表示等待证书响应
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                hideProgress();
                smartRefreshLayout.finishRefresh();
                if (!request_error) {
                    mWebView.setVisibility(View.VISIBLE);
                    mNetErrorHintPage.setVisibility(View.GONE);
                } else {
                    mWebView.setVisibility(View.GONE);
                    mNetErrorHintPage.setVisibility(View.VISIBLE);
                }
                request_error = false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null) {
                    mWebTitle.setText(title);
                }
            }
        });
    }

    @Override
    protected void initView() {
        mImmersionBar.titleView(R.id.toolbarlayout)
                .statusBarDarkFont(true)
                .execute();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWebView = findViewById(R.id.web_view);
        mNetErrorHintPage = findViewById(R.id.net_error_hint_page_rl);
        smartRefreshLayout = findViewById(R.id.smart_refresh_layout);
        if (BuildConfig.DEBUG) {
            smartRefreshLayout.setEnableRefresh(false);
        }
        mWebTitle = findViewById(R.id.web_title);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            //再次打开页面时，若界面没有消亡，会导致进度条不显示并且界面崩溃
            mWebView.stopLoading();
            mWebView.onPause();
            mWebView.clearCache(true);
            mWebView.clearHistory();
            //动态创建webview调用
            //ViewGroup parent = (ViewGroup) mWebView.getParent();
            //if (parent != null) {
            //  parent.removeView(mWebView);
            //}
            mWebView.removeAllViews();
            //先结束未结束线程，以免可能会导致空指针异常
            mWebView.destroy();
            mWebView = null;
            super.onDestroy();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mWebView.reload();
    }

}
