package org.ionc.wallet.callback;

import org.ionc.wallet.bean.WalletBean;

/**
* 创建钱包结果的回调
* */
public interface OnCreateWalletCallback {
    /**
     * 成功
     * @param walletBean 钱包
     */
    void onCreateSuccess(WalletBean walletBean);

    /**
     * 失败
     * @param error 原因
     */
    void onCreateFailure(String error);
}
