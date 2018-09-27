package org.ionchain.wallet.mvp.callback;

import org.ionchain.wallet.bean.WalletBean;

/**
 * USER: binny
 * DATE: 2018/9/12
 * 描述: 余额刷新的结果回调
 */
public interface OnCreateWalletCallback{
    /**
     * 成功
     *
     * @param walletBean 钱包
     */
    void onCreateSuccess(WalletBean walletBean);

    /**
     * 失败
     *
     * @param result 失败原因
     */
    void onCreateFailure(String result);

}
