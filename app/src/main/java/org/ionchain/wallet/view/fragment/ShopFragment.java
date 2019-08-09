package org.ionchain.wallet.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.view.activity.transaction.TxOutActivity;
import org.ionchain.wallet.view.base.AbsBaseFragment;
import org.ionchain.wallet.view.widget.dialog.more.MoreWalletDialog;

import static org.ionchain.wallet.constant.ConstantIntentParam.INTENT_PARAM_CURRENT_WALLET;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_ADDRESS;
import static org.ionchain.wallet.constant.ConstantParams.CURRENT_KSP;
import static org.ionchain.wallet.constant.ConstantParams.TX_ACTIVITY_FOR_RESULT_CODE;

public class ShopFragment extends AbsBaseFragment implements OnRefreshListener, MoreWalletDialog.OnMoreWalletDialogItemClickedListener {

    private TextView commonTitle;
    private SmartRefreshLayout mRefreshLayout;
    private WebView mWebView;
    private ImageView mImageView;
    private MoreWalletDialog mMoreWalletDialog;


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    protected void initView(View view) {
        commonTitle = (TextView) view.findViewById(R.id.common_title);
        mRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.smart_refresh_layout);
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mImageView = (ImageView) view.findViewById(R.id.no_net_error);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        mRefreshLayout.setOnRefreshListener(this);
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

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        String appCacheDir = mActivity.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //5.0及以上webview不支持http和https混合模式，需要通过配置来开启混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.addJavascriptInterface(new TransferForWeb(), "android");
        mWebView.loadUrl("file:///android_asset/shop.html");
    }

    @Override
    protected void setListener() {
        super.setListener();
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                commonTitle.setText(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LoggerUtils.e("shop", "onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LoggerUtils.e("shop", "onPageFinished");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LoggerUtils.e("shop", "onReceivedError");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                LoggerUtils.e("shop", "onReceivedSslError");
                handler.proceed(); //表示等待证书响应
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                LoggerUtils.e("shop", "shouldOverrideUrlLoading");
                return true;
            }
        });
    }

    @Override
    protected void handleHidden() {
        super.handleHidden();
    }

    @Override
    protected void handleShow() {
        super.handleShow();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mWebView.reload();
    }

    @Override
    public void onMoreWalletDialogItemClick(WalletBeanNew wallet, int position) {
        Intent intent = new Intent(mActivity, TxOutActivity.class);
        intent.putExtra(INTENT_PARAM_CURRENT_WALLET, wallet);
        intent.putExtra(CURRENT_ADDRESS, wallet.getAddress());
        intent.putExtra(CURRENT_KSP, wallet.getKeystore());
        startActivityForResult(intent, TX_ACTIVITY_FOR_RESULT_CODE);
    }

    @Override
    public void onMoreWalletDialogImportBtnClick(MoreWalletDialog moreWalletDialog) {

    }

    @Override
    public void onMoreWalletDialogCreateBtnClick(MoreWalletDialog moreWalletDialog) {

    }

    public class TransferForWeb {
        @JavascriptInterface
        public void showWallets() {
            LoggerUtils.e("TransferForWeb");
            mMoreWalletDialog = new MoreWalletDialog(mActivity);
            mMoreWalletDialog.setLisenter(ShopFragment.this);
            mMoreWalletDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == TX_ACTIVITY_FOR_RESULT_CODE) {
//            LoggerUtils.i("requestCode", "requestCode = " + requestCode + "resultCode = " + resultCode + "address = " + mCurrentWallet.getAddress());
//            TxRecordBean t;
//            if (data != null) {
//                t = data.getParcelableExtra(TX_ACTIVITY_RESULT);
//                if (t == null) {
//                    return;
//                }
//                if (DEFAULT_TRANSCATION_HASH_NULL.equals(t.getHash())) {
//                    //交易失败
//                    saveTxRecordBean(t);
//                    return;
//                }
//                //交易成功
//                //刷刷新余额
////                //请求交易区块等信息
////                IONCTransfers.ethTransaction(getHostNode()
////                        , t.getHash()
////                        , t
////                        , this);
//            }
//        }
    }
}
