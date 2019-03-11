package org.ionc.wallet.sdk.activity;

import org.ionc.wallet.sdk.bean.WalletBean;

public interface OnCreateCallback {
    /**
     * 创建成功
     *
     * @param walletBean
     */
    void onSDKCreateSuccess(WalletBean walletBean);

    /**
     * @param e 异常
     */
    void onSDKCreateFailure(String e);

}