package org.ionchain.wallet.mvp.callback

/**
 * USER: binny
 * DATE: 2018/9/17
 * 描述: 转账结果
 */
interface OnTransationCallback {
    /**
     * 转账成功
     *
     * @param hashTx 转账成功的hash值
     */
    fun OnTxSuccess(hashTx: String)

    /**
     * 转账失败
     *
     * @param error
     */
    fun onTxFailure(error: String)
}
