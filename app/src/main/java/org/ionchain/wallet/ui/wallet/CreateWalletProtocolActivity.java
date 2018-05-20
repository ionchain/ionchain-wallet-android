package org.ionchain.wallet.ui.wallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.model.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;

import butterknife.BindView;

public class CreateWalletProtocolActivity extends BaseActivity {


    @BindView(R.id.webViewLayout)
    LinearLayout webViewLayout;

    @BindView(R.id.mProgressBar)
    ProgressBar mProgressBar;

    WebView mWebView;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.nextBtn:
                    transfer(CreateWalletSelectActivity.class);
                    break;
                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_create_wallet_protocol);

    }

    @Override
    protected void setListener() {

        setOnClickListener(R.id.nextBtn);

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initWebView();
        mWebView.loadUrl("file:///android_asset/xy.html");
    }


    void initWebView(){
        try{

            mWebView = new WebView(this);
            //支持JavaScript
            mWebView.getSettings().setJavaScriptEnabled(true);
            //支持自动加载JavaScript
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


            //获取焦点
            mWebView.requestFocus();
            mWebView.requestFocusFromTouch();

            // 0 手机 1pc
            // mWebView.getSettings().setUserAgent(1);
//            mWebView.getSettings().setUserAgentString("Android");
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


            // 允许js操作DOM模型
            mWebView.getSettings().setDomStorageEnabled(true);
            // 允许调用文件
            mWebView.getSettings().setAllowFileAccess(true);

            // 是否自动加载图片
            mWebView.getSettings().setLoadsImagesAutomatically(true);
            //支持自由缩放
            mWebView.getSettings().setSupportZoom(false);
            //不支持缩放的控制显示
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.getSettings().setSupportMultipleWindows(true);
            //支持双击缩放
            mWebView.getSettings().setUseWideViewPort(true);
            //自适应模式
            mWebView.getSettings().setLoadWithOverviewMode(true);
            //不保存数据
            mWebView.getSettings().setSaveFormData(false);
            //不保存密码
            mWebView.getSettings().setSavePassword(false);


            mWebView.setWebViewClient(new WebViewClientDemo());
            mWebView.setWebChromeClient(new WebViewChromeDemo());
            mWebView.setDownloadListener(new WebViewDownloadListener());

            CookieManager.getInstance().removeSessionCookie();

            // android 5.0以上默认不支持Mixed Content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }

            mWebView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

            webViewLayout.addView(mWebView);

            Logger.i("UserAgent==>"+mWebView.getSettings().getUserAgentString());

        }catch (Throwable e){
            Logger.e(TAG,e);
        }
    }


    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return 0;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_create_wallet_protocol_title;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if(mWebView != null){
            webViewLayout.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }


    private class WebViewDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            // TODO Auto-generated method stub
            try{
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(intent);
            }catch(Throwable e){
                Logger.e("WebView==onDownloadStart", e);
            }

        }

    }


    private class WebViewClientDemo extends WebViewClient {
        @Override
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            mWebView.getSettings().setBlockNetworkImage(true);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                String url = request.getUrl().toString();
                Logger.i("shouldOverrideUrlLoading==url======>"+url);
                if(url.startsWith("http:") || url.startsWith("https:") ) {
                    view.loadUrl(url);
                    return false;
                }
            }else{
                mWebView.loadUrl(request.toString());
            }

            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            Logger.i("onPageStarted==url======>"+url);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            Logger.i("onPageFinished==url======>"+url);
            mWebView.getSettings().setBlockNetworkImage(false);
            super.onPageFinished(view, url);

            mProgressBar.setVisibility(View.GONE);


        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {  //有效处理https  请求
            // TODO Auto-generated method stub
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);




        }
    }

    private class WebViewChromeDemo extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // TODO Auto-generated method stub
            super.onProgressChanged(view, newProgress);
            try{

                if(newProgress == 100){
                    mWebView.requestFocus();
                    mProgressBar.setVisibility(View.GONE);
                }else{
                    mProgressBar.setProgress(newProgress);
                }

            }catch(Throwable e){
                Logger.e("WebView==onProgressChanged", e);
            }

            //LogUtils.info("newProgress====>"+newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            // TODO Auto-generated method stub
            Logger.i("onReceivedTitle==title======>"+title);
            super.onReceivedTitle(view, title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            Logger.i("onJsAlert==url======>"+url);

            showAlertDialog(message);

            result.cancel();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url,
                                   String message, JsResult result) {
            // TODO Auto-generated method stub
            Logger.i("onJsConfirm==url======>"+url);
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            // TODO Auto-generated method stub
            Logger.i("onJsPrompt==url======>"+url);
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }


    }
}
