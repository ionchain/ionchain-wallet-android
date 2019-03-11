package org.ionchain.wallet.mvp.view.activity.sdk;

import android.content.Intent;

import org.ionc.wallet.sdk.activity.base.AbsSelectCreateModeWalletActivity;

/**
 * user: binny
 * date:2019/1/9
 * description：SDK中的钱包选择界面
 */
public class SDKSelectCreateModeWalletActivity extends AbsSelectCreateModeWalletActivity {

    @Override
    protected Intent getKeyStoreActivity() {
        return new Intent(this,SDKKeyStoreActivity.class);
    }

    @Override
    protected Intent getMnemonicActivity() {
        return new Intent(this,SDKMnemonicActivity.class);
    }

    @Override
    protected Intent getPrivateKeyActivity() {
        return new Intent(this,SDKPrivateKeyActivity.class);
    }

    @Override
    protected Intent getCreateActivity() {
        return new Intent(this,SDKCreateActivity.class);
    }
}
