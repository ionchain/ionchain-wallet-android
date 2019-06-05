package org.ionchain.wallet.mvp.presenter.transcation;

import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;
import org.ionchain.wallet.mvp.model.txrecoder.TxRecordModel;

public class TxRecordPresenter implements ITxPresenter {
    @Override
    public void getTxRecord(boolean isLoadMore, String type, String key, String pageNumber, String pageSize, OnTxRecordCallback callback) {
        new TxRecordModel().getTxRecord(isLoadMore,type, key, pageNumber, pageSize, callback);
    }
}
