package org.ionchain.wallet.view.activity.webview;

import android.content.Intent;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;

import static org.ionchain.wallet.constant.ConstantParams.TX_HASH;
import static org.ionchain.wallet.utils.UrlUtils.getExplorerUrl;

public class TxRecordBrowserActivity extends AbsWebActivity  {
    private String url;

    private String hash;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }


    @Override
    protected void initCommonTitle() {
        super.initCommonTitle();
        setActivityTitle(getAppString(R.string.ionchain_explore));
    }

    @Override
    protected void initWebData() {
        url = getExplorerUrl() + hash;
        LoggerUtils.i("url ", url);
        mWebView.loadUrl(url);
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
    protected void setWebPageTitle(String title) {
        LoggerUtils.i("title = ",title);
    }
}
