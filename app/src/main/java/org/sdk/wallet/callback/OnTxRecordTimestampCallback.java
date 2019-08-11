package org.sdk.wallet.callback;

import org.sdk.wallet.bean.TxRecordBean;

/**
 * user: binny
 * date:2018/12/7
 * description：转账结果的回调
 */
public interface OnTxRecordTimestampCallback {
    /**
     * 转账成功
     *
     */
    void OnTxRecordTimestampSuccess(TxRecordBean txRecordBean);

    /**
     * 转账失败
     *  @param error
     * @param recordBean
     */
    void onTxRecordTimestampFailure(String error, TxRecordBean txRecordBean);

    void onTxRecordTimestampStart();

}
