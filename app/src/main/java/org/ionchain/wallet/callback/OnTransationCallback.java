package org.ionchain.wallet.callback;

/**
 * USER: binny
 * DATE: 2018/9/17
 * 描述: 转账结果
 */
public interface OnTransationCallback {
    /**
     * 转账成功
     *
     * @param hashTx 转账成功的hash值
     */
    void OnTxSuccess(String hashTx);

    /**
     * 转账失败
     *
     * @param error
     */
    void onTxFailure(String error);
}
