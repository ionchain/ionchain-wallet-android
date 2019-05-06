package org.ionchain.wallet.mvp.presenter;

import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.callback.OnTxRecoderCallback;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.mvp.model.home.HomePageModel;
import org.ionchain.wallet.mvp.model.txrecoder.TxRecoderModel;
import org.ionchain.wallet.mvp.model.update.OnCheckUpdateInfoCallback;
import org.ionchain.wallet.mvp.model.update.UpdateModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Presenter implements IPresenter {
    private HomePageModel mHomePageModel = null;

    public void initHomePageModel() {
        if (mHomePageModel == null) {
            mHomePageModel = new HomePageModel();
        }
    }

    @Override
    public void getCurrentWalletDevicesList(@NotNull WalletBean walletBean, @NotNull OnDeviceListCallback callback) {
        mHomePageModel.getCurrentWalletDevicesList(walletBean, callback);
    }

    @Override
    public void getAllWalletDevicesList(List<WalletBean> walletBeans, OnDeviceListCallback callback) {

    }

    @Override
    public void bindDeviceToWallet(@NotNull String address, @NotNull String cksn, @NotNull OnBindDeviceCallback callback) {
        mHomePageModel.bindDeviceToWallet(address, cksn, callback);
    }

    @Override
    public void unbindDeviceToWallet(@NotNull String address, @NotNull String cksn, @NotNull OnUnbindDeviceCallback callback) {
        mHomePageModel.unbindDeviceToWallet(address, cksn, callback);
    }

    @Override
    public void getDeviceDetail(@NotNull String cksn, @NotNull OnDeviceDetailCallback callback) {

    }

    @Override
    public void getTxRecord(@NotNull String type, @NotNull String key, @NotNull String pageNumber, @NotNull String pageSize, @NotNull OnTxRecoderCallback callback) {

        new TxRecoderModel().getTxRecoder(type, key, pageNumber, pageSize, callback);
    }

    @Override
    public void checkForUpdate(OnCheckUpdateInfoCallback callback) {
       new UpdateModel().update(callback);
    }
}
