package org.ionc.ionclib.callback;

import org.ionc.ionclib.bean.TxRecordBean;

/**
 * user: binny
 * date:2018/12/7
 * description：转账结果的回调
 */
public interface OnTxRecordFromNodeCallback {
    /**
     * 转账成功
     *
     */
    void OnTxRecordNodeSuccess(TxRecordBean txRecordBean);

    /**
     * 转账失败
     *  @param error
     * @param recordBean
     */
    void onTxRecordNodeFailure(String error, TxRecordBean recordBean);

    void onTxRecordNodeStart();

}
