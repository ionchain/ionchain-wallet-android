package org.sdk.wallet.callback;

import org.sdk.wallet.bean.WalletBeanNew;

/**
* 创建钱包结果的回调
* */
public interface OnCreateWalletCallback {
    /**
     * 成功
     * @param walletBean 钱包
     */
    void onCreateSuccess(WalletBeanNew walletBean);

    /**
     * 失败
     * @param error 原因
     */
    void onCreateFailure(String error);
}
