package org.ionchain.wallet.mvp.presenter.transcation;

import org.ionchain.wallet.mvp.callback.OnTxRecordBrowserDataCallback;

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
