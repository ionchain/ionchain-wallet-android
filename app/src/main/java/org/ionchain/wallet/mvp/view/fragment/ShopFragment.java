package org.ionchain.wallet.mvp.view.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.dao.WalletDaoTools;
import com.ionc.wallet.sdk.utils.Logger;
import com.ionc.wallet.sdk.widget.IONCAllWalletDialogSDK;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;

import java.util.List;

/**
 * user: binny
 * date:2018/12/18
 * description：${END}
 */
public class ShopFragment extends AbsBaseFragment implements IONCAllWalletDialogSDK.OnTxResultCallback {
    private WebView mWebView;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_shop;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void initView(View view) {
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

    }

    @Override
    protected void initData() {
        mWebView.loadUrl("file:///android_asset/testshop.html");
    }

    @Override
    public void OnTxSuccess(String hashTx) {
        Logger.i(hashTx);
        mWebView.loadUrl("javascript:onTxSuccess(" + hashTx + ")");
    }

    @Override
    public void onTxFailure(String error) {
        Logger.e(error);
        mWebView.loadUrl("javascript:onTxFailure(" + error + ")");
    }

    public class TestSDK {
        @JavascriptInterface
        public void getAllWallet() {
            List<WalletBean> beans = WalletDaoTools.getAllWallet();
            new IONCAllWalletDialogSDK(mActivity, beans, ShopFragment.this).show();
        }

    }

}
