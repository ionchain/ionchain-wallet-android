package org.ionchain.wallet.mvp.presenter.transcation;

import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;
import org.ionchain.wallet.mvp.model.txrecoder.TxRecoderModel;

public class TxRecordPresenter implements ITxPresenter {
    @Override
    public void getTxRecord(String type, String key, String pageNumber, String pageSize, OnTxRecordCallback callback) {
        new TxRecoderModel().getTxRecoder(type, key, pageNumber, pageSize, callback);
    }
}
