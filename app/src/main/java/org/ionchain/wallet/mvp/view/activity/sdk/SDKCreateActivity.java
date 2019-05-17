package org.ionchain.wallet.mvp.view.activity.sdk;

import org.ionc.wallet.activity.base.AbsByCreateActivity;
import org.ionc.wallet.bean.WalletBeanNew;

import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;


/**
 * user: binny
 * date:2019/1/9
 * description：
 */
public class SDKCreateActivity extends AbsByCreateActivity {
    @Override
    public void onSDKCreateSuccess(WalletBeanNew walletBean) {
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
