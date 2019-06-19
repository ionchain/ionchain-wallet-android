package org.ionchain.wallet.mvp.model.txrecoder;


import org.ionchain.wallet.mvp.callback.OnTxRecordNetDataCallback;

/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:46
 */
public interface ITxRecoderModel {
    /**
     * 获取交易记录
     * @param isLoadMore
     * @param type
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param callback
     */
    void getTxRecord(boolean isLoadMore, String type, String key, String pageNumber, String pageSize, OnTxRecordNetDataCallback callback);
}
