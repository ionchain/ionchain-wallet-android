package org.ionchain.wallet.mvp.view.activity;

import android.webkit.WebView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

public class WebActivity extends AbsBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        getMImmersionBar().titleView(R.id.toolbarlayout)
                .statusBarDarkFont(true)
                .execute();
        WebView webView = findViewById(R.id.web_view);
        webView.loadUrl("http://192.168.23.112/wallet-agreement.html");
    }
}
