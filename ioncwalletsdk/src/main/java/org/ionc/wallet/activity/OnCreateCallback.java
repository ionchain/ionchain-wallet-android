package org.ionc.wallet.activity;

import org.ionc.wallet.bean.WalletBeanNew;

public interface OnCreateCallback {
    /**
     * 创建成功
     *
     * @param walletBean
     */
    void onSDKCreateSuccess(WalletBeanNew walletBean);

    /**
     * @param e 异常
     */
    void onSDKCreateFailure(String e);

}
