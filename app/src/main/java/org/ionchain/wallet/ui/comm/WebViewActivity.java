package org.ionchain.wallet.ui.comm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
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
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.network.SonicRuntimeImpl;
import org.ionchain.wallet.comm.network.SonicSessionClientImpl;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by siberiawolf on 17/5/26.
 */

public class WebViewActivity extends BaseActivity {

    public static final int MODE_DEFAULT = 0;

    public static final int MODE_SONIC = 1;

    public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

    public static final int PERMISSION_REQUEST_CODE_STORAGE = 1;

    public final static String PARAM_MODE = "param_mode";

    private SonicSession sonicSession;

    @BindView(R.id.webViewLayout)
    LinearLayout webViewLayout;

    @BindView(R.id.mProgressBar)
    ProgressBar mProgressBar;

    WebView mWebView;
    String mUrl = "";
    String mTitle = "";
    String mContent = "";

    SonicSessionClientImpl sonicSessionClient = null;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;

                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }

                    break;
            }
        }catch (Throwable e){
            Logger.e(TAG,e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过代码注册你的AppKey和AppSecret
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        int mode = getIntent().getIntExtra(PARAM_MODE, -1);
        mUrl = getIntent().getStringExtra(Comm.SERIALIZABLE_DATA);
        mContent = getIntent().getStringExtra(Comm.SERIALIZABLE_DATA2);
        if(TextUtils.isEmpty(mUrl) && TextUtils.isEmpty(mContent)){
            ToastUtil.showShortToast("参数传入错误");
            finish();
            return;
        }

        if(!TextUtils.isEmpty(mUrl)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

            // init sonic engine if necessary, or maybe u can do this when application created
            if (!SonicEngine.isGetInstanceAllowed()) {
                SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
            }

            sonicSessionClient = null;

            // if it's sonic mode , startup sonic session at first time
            if (MODE_DEFAULT != mode) { // sonic mode
                SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();

                // if it's offline pkg mode, we need to intercept the session connection
                if (MODE_SONIC_WITH_OFFLINE_CACHE == mode) {
                    sessionConfigBuilder.setCacheInterceptor(new SonicCacheInterceptor(null) {
                        @Override
                        public String getCacheData(SonicSession session) {
                            return null; // offline pkg does not need cache
                        }
                    });

                    sessionConfigBuilder.setConnectionIntercepter(new SonicSessionConnectionInterceptor() {
                        @Override
                        public SonicSessionConnection getConnection(SonicSession session, Intent intent) {
                            return new OfflinePkgSessionConnection(WebViewActivity.this, session, intent);
                        }
                    });
                }

                // create sonic session and run sonic flow
                sonicSession = SonicEngine.getInstance().createSession(mUrl, sessionConfigBuilder.build());
                if (null != sonicSession) {
                    sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
                } else {
                    // this only happen when a same sonic session is already running,
                    // u can comment following codes to feedback as a default mode.
                    throw new UnknownError("create session fail!");
                }
            }
        }

        setContentView(R.layout.activity_webview);
        setVisGoneLine(View.GONE);
        initWebView();

    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {


        mTitle = getIntent().getStringExtra(Comm.SERIALIZABLE_DATA1);
        if(!TextUtils.isEmpty(mTitle)){
            setTitle(mTitle);
        }


        // webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(mWebView);
            sonicSessionClient.clientReady();
        } else { // default mode
            if(!TextUtils.isEmpty(mUrl)){
                mWebView.loadUrl(mUrl);
            }else if(!TextUtils.isEmpty(mContent)){
                mWebView.loadDataWithBaseURL("about:blank",mContent,"text/html", "utf-8",null);
            }

        }

    }

    void initWebView(){
        try{

            mWebView = new WebView(this);
            //支持JavaScript
            mWebView.getSettings().setJavaScriptEnabled(true);
            //支持自动加载JavaScript
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

            mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "JavaScriptInterface");

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
        return R.drawable.qmui_icon_topbar_back;
    }

    @Override
    public int getActivityTitleContent() {
        return 0;
    }


    @Override
    protected void onDestroy() {

        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }

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

            if(TextUtils.isEmpty(mTitle)){
                setTitle(title);
            }

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


    class MyJavaScriptInterface{
        @JavascriptInterface
        public void toFilmDetails(String filmId){


        }

        @JavascriptInterface
        public void toHotList(){
        }


    }

    private static class OfflinePkgSessionConnection extends SonicSessionConnection {

        private final WeakReference<Context> context;

        public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
            super(session, intent);
            this.context = new WeakReference<Context>(context);
        }

        @Override
        protected int internalConnect() {
            Context ctx = context.get();
            if (null != ctx) {
                try {
                    InputStream offlineHtmlInputStream = ctx.getAssets().open("sonic-demo-index.html");
                    responseStream = new BufferedInputStream(offlineHtmlInputStream);
                    return SonicConstants.ERROR_CODE_SUCCESS;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return SonicConstants.ERROR_CODE_UNKNOWN;
        }

        @Override
        protected BufferedInputStream internalGetResponseStream() {
            return responseStream;
        }

        @Override
        public void disconnect() {
            if (null != responseStream) {
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getResponseCode() {
            return 200;
        }

        @Override
        public Map<String, List<String>> getResponseHeaderFields() {
            return new HashMap<>(0);
        }

        @Override
        public String getResponseHeaderField(String key) {
            return "";
        }
    }


}
