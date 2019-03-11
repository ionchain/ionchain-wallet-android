package org.ionchain.wallet.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.sdk.utils.Logger;
import org.ionc.wallet.sdk.widget.IONCAllWalletDialogSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;

/**
 * user: binny
 * date:2018/12/18
 * description：${END}
 */
public class ShopFragment extends AbsBaseFragment implements IONCAllWalletDialogSDK.OnTxResultCallback, OnRefreshListener {
    private WebView mWebView;
    private SmartRefreshLayout mRefresh;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_shop;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void initView(View view) {
        mRefresh = view.findViewById(R.id.refresh_asset);
        mRefresh.setOnRefreshListener(this);
        mWebView = view.findViewById(R.id.web_view);
        //声明WebSettings子类
        WebSettings webSettings = mWebView.getSettings();

//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);


        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebView.addJavascriptInterface(new TestSDK(), "ionc_sdk_android");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Logger.i("url = " + url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.i("onPageStarted,url = " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.i("onPageFinished,url = " + url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                Logger.i("onPageCommitVisible,url = " + url);
            }
        });
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    if (mWebView != null && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        mWebView.loadUrl("http://walletapi.ionchain.org/home.html#/");
//        mWebView.loadUrl("file:///android_asset/testshop.html");
    }

    //交易成功
    @Override
    public void OnTxSuccess(String hashTx) {
        Logger.i(hashTx);
        //此处可以直接弹，不许需要调用js
        mWebView.loadUrl("javascript:onTxSuccess(" + hashTx + ")");
    }

    //交易失败
    @Override
    public void onTxFailure(String error) {
        Logger.e(error);
        mWebView.loadUrl("javascript:onTxFailure(" + error + ")");
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mWebView.reload();
        mRefresh.finishRefresh();
    }

    public class TestSDK {
        @JavascriptInterface
        public void getAllWallet() {
            IONCWalletSDK.getInstance().transactionDialog(mActivity,ShopFragment.this);
        }

    }


}
