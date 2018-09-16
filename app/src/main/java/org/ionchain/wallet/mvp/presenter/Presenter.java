package org.ionchain.wallet.mvp.presenter;

import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnBindDeviceCallback;
import org.ionchain.wallet.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.callback.OnDeviceListCallback;
import org.ionchain.wallet.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.mvp.model.home.HomePageModel;

import java.util.List;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 枢纽
 */
public class Presenter implements IPresenter {
    private HomePageModel mHomePageModel;

    public Presenter() {

    }

    public void initHomePageModel() {
        if (mHomePageModel == null) {
            mHomePageModel = new HomePageModel();
        }
    }

    @Override
    public void getCurrentWalletDevicesList(WalletBean walletBean, OnDeviceListCallback listData) {
        mHomePageModel.getCurrentWalletDevicesList(walletBean, listData);
    }

    @Override
    public void getAllWalletDevicesList(List<WalletBean> walletBeans, OnDeviceListCallback listData) {

    }

    @Override
    public void bindDeviceToWallet(String address, String cksn, OnBindDeviceCallback callback) {
        mHomePageModel.bindDeviceToWallet(address, cksn, callback);
    }

    @Override
    public void unbindDeviceToWallet(String address, String cksn, OnUnbindDeviceCallback callback) {
        mHomePageModel.unbindDeviceToWallet(address, cksn, callback);
    }

    @Override
    public void getDeviceDetail(String cksn,OnDeviceDetailCallback deviceBean) {

    }
}
