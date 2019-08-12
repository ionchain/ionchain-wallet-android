package org.ionc.wallet.presenter.transcation;

import org.ionc.wallet.callback.OnTxRecordBrowserDataCallback;

public interface ITxRecordPresenter {
    /**
     * 获取交易记录
     * @param type
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param callback
     */
    void getTxRecordFrom( String type, String key, String pageNumber, String pageSize, OnTxRecordBrowserDataCallback callback); /**
     * 获取交易记录
     * @param type
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param callback
     */
    void getTxRecordTo( String type, String key, String pageNumber, String pageSize, OnTxRecordBrowserDataCallback callback);

    /**
     * 获取交易记录
     *
     * @param type
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param callback
     */
    void getTxRecordAll(String type, String key, String pageNumber, String pageSize, OnTxRecordBrowserDataCallback callback);
}
