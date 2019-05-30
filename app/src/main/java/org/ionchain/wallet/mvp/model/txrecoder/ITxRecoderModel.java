package org.ionchain.wallet.mvp.model.txrecoder;


import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;

/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:46
 */
public interface ITxRecoderModel {
    /**
     * 获取交易记录
     * @param type
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param callback
     */
    void getTxRecord(String type, String key, String pageNumber, String pageSize, OnTxRecordCallback callback);
}
