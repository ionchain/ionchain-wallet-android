package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.WalletBean

/**
 * USER: binny
 * DATE: 2018/9/12
 * 描述: 余额刷新的结果回调
 */
interface OnCreateWalletCallback {
    /**
     * 成功
     *
     * @param walletBean 钱包
     */
    fun onCreateSuccess(walletBean: WalletBean)

    /**
     * 失败
     *
     * @param result 失败原因
     */
    fun onCreateFailure(result: String)

}
