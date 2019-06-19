package org.ionchain.wallet.mvp.presenter.transcation;

import org.ionchain.wallet.mvp.callback.OnTxRecordNetDataCallback;
import org.ionchain.wallet.mvp.model.txrecoder.ITxRecoderModel;
import org.ionchain.wallet.mvp.model.txrecoder.TxRecordModel;

public class TxRecordPresenter implements ITxRecoderModel {

    @Override
    public void getTxRecord(boolean isLoadMore, String type, String address, String pageNumber, String pageSize, OnTxRecordNetDataCallback callback) {
        new TxRecordModel().getTxRecord(isLoadMore,type, address, pageNumber, pageSize, callback);
    }
}
