package org.ionchain.wallet.callback;

import org.ionchain.wallet.comm.api.model.Wallet;

/**
 * USER: binny
 * DATE: 2018/9/12
 * 描述: 余额刷新的结果回调
 */
public interface OnBalanceRefreshCallback {
    /**
     * 成功
     *
     * @param balance 余额
     */
    void onSuccess(Wallet balance);

    /**
     * 请求失败
     * @param s
     */
    void onFailure(String s);
}
