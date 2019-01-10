package org.ionchain.wallet.mvp.view.activity.sdk;

import com.ionc.wallet.sdk.activity.base.AbsByCreateActivity;
import com.ionc.wallet.sdk.bean.WalletBean;

import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;

import static com.ionc.wallet.sdk.IONCWalletSDK.saveWallet;

/**
 * user: binny
 * date:2019/1/9
 * description：
 */
public class SDKCreateActivity extends AbsByCreateActivity {
    @Override
    public void onSDKCreateSuccess(WalletBean walletBean) {
        hideProgress();
        skip(MainActivity.class);
    }

    @Override
    public void onSDKCreateFailure(String e) {
        hideProgress();
        ToastUtil.showToastLonger(e);
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }
}
