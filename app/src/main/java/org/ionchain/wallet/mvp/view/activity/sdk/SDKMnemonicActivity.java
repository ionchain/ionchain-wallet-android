package org.ionchain.wallet.mvp.view.activity.sdk;

import com.ionc.wallet.sdk.activity.base.AbsByMnemonicActivity;
import com.ionc.wallet.sdk.bean.WalletBean;

import org.ionchain.wallet.mvp.view.activity.MainActivity;

/**
 * user: binny
 * date:2019/1/9
 * description：
 */
public class SDKMnemonicActivity extends AbsByMnemonicActivity {

    @Override
    public void onSDKCreateSuccess(WalletBean walletBean) {
        skip(MainActivity.class);
    }

    @Override
    public void onSDKCreateFailure(String e) {

    }
}
