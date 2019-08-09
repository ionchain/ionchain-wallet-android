package org.ionc.wallet.callback;

import java.math.BigInteger;

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
     * @param nonce
     */
    void onTxSuccess(String hashTx, BigInteger nonce);

    /**
     * 转账失败
     *
     * @param error
     */
    void onTxFailure(String error);

    /**
     * 交易正在进行
     */
    void onTxStart();
}
