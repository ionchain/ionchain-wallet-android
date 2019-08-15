package org.ionc.wallet.view.activity.webview;

import org.ionc.wallet.utils.LoggerUtils;

import static org.ionc.wallet.App.isCurrentLanguageZN;

public class UseHelpActivity extends AbsWebActivity {

    @Override
    protected void initWebData() {

        if (isCurrentLanguageZN()) {
            mWebView.loadUrl("https://ionchain.org/help/index.html");

        } else {
            mWebView.loadUrl("https://ionchain.org/help/index_en.html");
        }
    }

    @Override
    protected void setWebPageTitle(String title) {
        LoggerUtils.i("title = ",title);
        setActivityTitle(title);
    }
}
