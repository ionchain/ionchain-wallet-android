package org.ionc.wallet.activity;

import org.ionc.wallet.bean.WalletBean;

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