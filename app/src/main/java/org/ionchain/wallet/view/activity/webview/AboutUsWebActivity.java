package org.ionchain.wallet.view.activity.webview;

import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.BuildConfig;

import static org.ionchain.wallet.App.isCurrentLanguageZN;

public class AboutUsWebActivity extends AbsWebActivity implements OnRefreshListener {



    @Override
    protected void initWebData() {
        if (isCurrentLanguageZN()) {
            mWebView.loadUrl(BuildConfig.URL_ABOUTUS_CN); //加载关于我们
        } else {
            mWebView.loadUrl(BuildConfig.URL_ABOUT_US_EN); //加载关于我们
        }
    }

    @Override
    protected void setWebPageTitle(String title) {
        LoggerUtils.i("title = ",title);
        setActivityTitle(title);
    }
}