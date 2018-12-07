package com.ionc.wallet.sdk.callback;

/**
 * user: binny
 * date:2018/12/7
 * description：转账结果的回调
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
