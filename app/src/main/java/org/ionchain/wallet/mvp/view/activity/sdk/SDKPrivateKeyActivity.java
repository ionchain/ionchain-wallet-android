package org.ionchain.wallet.mvp.view.activity.sdk;

import org.ionc.wallet.activity.base.AbsByPrivateKeyActivity;
import org.ionc.wallet.bean.WalletBeanNew;

import org.ionchain.wallet.mvp.view.activity.MainActivity;

/**
 * user: binny
 * date:2019/1/9
 * description：
 */
public class SDKPrivateKeyActivity extends AbsByPrivateKeyActivity {

    @Override
    public void onSDKCreateSuccess(WalletBeanNew walletBean) {
        skip(MainActivity.class);
    }

    @Override
    public void onSDKCreateFailure(String e) {

    }
}
