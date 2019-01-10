package org.ionchain.wallet.mvp.view.activity.sdk;

import com.ionc.wallet.sdk.activity.base.AbsByKeystoreActivity;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.utils.Logger;

import org.ionchain.wallet.mvp.view.activity.MainActivity;

/**
 * user: binny
 * date:2019/1/9
 * description：
 */
public class SDKKeyStoreActivity extends AbsByKeystoreActivity {


    @Override
    public void onSDKCreateSuccess(WalletBean walletBean) {

        skip(MainActivity.class);
    }

    @Override
    public void onSDKCreateFailure(String e) {
        Logger.e("e", "onCreateFailure: " + e);
    }
}
