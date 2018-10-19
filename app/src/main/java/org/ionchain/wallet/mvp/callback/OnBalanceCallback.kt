package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.WalletBean

/**
 * USER: binny
 * DATE: 2018/9/17
 * 描述: 余额查询接口
 */
interface OnBalanceCallback {
    /**
     * 查询余额成功
     *
     * @param walletBean 余额
     */
    fun onBalanceSuccess(walletBean: WalletBean)

    /**
     * 查询余额失败
     *
     * @param error 失败信息
     */
    fun onBalanceFailure(error: String)
}
