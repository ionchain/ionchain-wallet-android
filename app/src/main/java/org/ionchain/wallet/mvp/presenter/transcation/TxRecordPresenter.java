package org.ionchain.wallet.mvp.presenter.transcation;

import org.ionchain.wallet.mvp.callback.OnTxRecordBrowserDataCallback;
import org.ionchain.wallet.mvp.model.txrecoder.TxRecordModel;
import org.ionchain.wallet.utils.UrlUtils;

public class TxRecordPresenter implements ITxRecordPresenter {
     private TxRecordModel mTxRecordModel;

    public TxRecordPresenter() {
        mTxRecordModel = new TxRecordModel();
    }



    @Override
    public void getTxRecordFrom(String type, String address, String pageNumber, String pageSize, OnTxRecordBrowserDataCallback callback) {
        mTxRecordModel.getTxRecord(UrlUtils.getTxRecordFromUrl(), type, address, pageNumber, pageSize, callback);
    }

    @Override
    public void getTxRecordTo(String type, String address, String pageNumber, String pageSize, OnTxRecordBrowserDataCallback callback) {
        mTxRecordModel.getTxRecord(UrlUtils.getTxRecordToUrl(), type, address, pageNumber, pageSize, callback);
    }

    @Override
    public void getTxRecordAll(String type, String address, String pageNumber, String pageSize, OnTxRecordBrowserDataCallback callback) {
        mTxRecordModel.getTxRecord(UrlUtils.getTxRecordAllUrl(), type, address, pageNumber, pageSize, callback);
    }
}
