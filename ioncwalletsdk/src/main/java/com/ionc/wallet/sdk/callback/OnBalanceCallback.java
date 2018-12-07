package com.ionc.wallet.sdk.callback;

import com.ionc.wallet.sdk.bean.WalletBean;

public interface OnBalanceCallback {
    /**
     * 查询余额成功
     *
     * @param walletBean 余额
     */
    void onBalanceSuccess(WalletBean walletBean);
    /**
     * 查询余额失败
     *
     * @param error 失败信息
     */
    void onBalanceFailure(String error );

}
