package org.ionc.wallet.model.txrecoder;


import org.ionc.wallet.callback.OnTxRecordBrowserDataCallback;

/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:46
 */
public interface ITxRecoderModel {
    /**
     * 获取交易记录
     * @param url
     * @param type
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param callback
     */
    void getTxRecord(String url, String type, String key, String pageNumber, String pageSize, OnTxRecordBrowserDataCallback callback);
}
