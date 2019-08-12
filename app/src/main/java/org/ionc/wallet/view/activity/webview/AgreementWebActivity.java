package org.ionc.wallet.view.activity.webview;

import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.sdk.wallet.utils.LoggerUtils;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;

import static org.ionc.wallet.App.isCurrentLanguageZN;

public class AgreementWebActivity extends AbsWebActivity implements OnRefreshListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }


    @Override
    protected void initWebData() {
        if (isCurrentLanguageZN()) {
            mWebView.loadUrl(BuildConfig.URL_PROTOCOL_CN); //加载协议

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
