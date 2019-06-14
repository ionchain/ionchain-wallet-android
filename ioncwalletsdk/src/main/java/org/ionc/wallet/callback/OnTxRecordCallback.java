package org.ionc.wallet.callback;

import org.ionc.wallet.bean.TxRecordBean;

/**
 * user: binny
 * date:2018/12/7
 * description：转账结果的回调
 */
public interface OnTxRecordCallback {
    /**
     * 转账成功
     *
     * @param hashTx 转账成功的hash值
     */
    void OnTxRecordSuccess(TxRecordBean  txRecordBean);

    /**
     * 转账失败
     *
     * @param error
     */
    void onTxRecordFailure(String error);

}
