package org.ionchain.wallet.mvp.view.activity.sdk;

import com.ionc.wallet.sdk.activity.base.AbsByPrivateKeyActivity;
import com.ionc.wallet.sdk.bean.WalletBean;

import org.ionchain.wallet.mvp.view.activity.MainActivity;

/**
 * user: binny
 * date:2019/1/9
 * description：
 */
public class SDKPrivateKeyActivity extends AbsByPrivateKeyActivity {

    @Override
    public void onSDKCreateSuccess(WalletBean walletBean) {
        skip(MainActivity.class);
    }

    @Override
    public void onSDKCreateFailure(String e) {

    }
}
