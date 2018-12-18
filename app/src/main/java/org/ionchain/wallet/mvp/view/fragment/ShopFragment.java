package org.ionchain.wallet.mvp.view.fragment;

import android.view.View;
import android.webkit.WebView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;

/**
 * user: binny
 * date:2018/12/18
 * description：${END}
 */
public class ShopFragment extends AbsBaseFragment {
    private WebView mWebView;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    protected void initView(View view) {
        mWebView = view.findViewById(R.id.web_view);
    }

    @Override
    protected void initData() {
        mWebView.loadUrl("https://www.baidu.com");
    }
}
