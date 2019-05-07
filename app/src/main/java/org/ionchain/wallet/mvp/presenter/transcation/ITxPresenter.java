package org.ionchain.wallet.mvp.presenter.transcation;

import org.ionc.wallet.callback.OnTxRecoderCallback;

/**
 * 交易记录
 */
public interface ITxPresenter {
    /**
     * @param type
     * @param key        钱包地址
     * @param pageNumber
     * @param pageSize
     */
    void getTxRecord(String type, String key, String pageNumber, String pageSize, OnTxRecoderCallback callback);
}
