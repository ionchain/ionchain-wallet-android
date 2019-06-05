package org.ionchain.wallet.mvp.presenter.transcation;

import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;

/**
 * 交易记录
 */
public interface ITxPresenter {
    /**
     * @param isLoadMore
     * @param type
     * @param key        钱包地址
     * @param pageNumber
     * @param pageSize
     * @param callback
     */
    void getTxRecord(boolean isLoadMore, String type, String key, String pageNumber, String pageSize, OnTxRecordCallback callback);
}
