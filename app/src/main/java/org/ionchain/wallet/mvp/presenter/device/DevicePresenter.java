package org.ionchain.wallet.mvp.presenter.device;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.mvp.model.device.DeviceModel;
import org.ionchain.wallet.mvp.model.device.IDeviceModel;
import org.jetbrains.annotations.NotNull;

/**
 * 设别信息
 */
public class DevicePresenter implements IDeviceModel {

    private DeviceModel mDeviceModel;

    public DevicePresenter() {
        mDeviceModel = new DeviceModel();
    }

    @Override
    public void getCurrentWalletDevicesList(@NotNull WalletBeanNew walletBean, @NotNull OnDeviceListCallback callback) {
        mDeviceModel.getCurrentWalletDevicesList(walletBean, callback);
    }

    @Override
    public void getAllWalletDeviceList(String addressSet, OnDeviceListCallback callback) {
        mDeviceModel.getAllWalletDeviceList(addressSet,callback);
    }


    @Override
    public void bindDeviceToWallet(@NotNull String address, @NotNull String cksn, @NotNull OnBindDeviceCallback callback) {
        mDeviceModel.bindDeviceToWallet(address, cksn, callback);
    }

    @Override
    public void unbindDeviceToWallet(@NotNull String address, @NotNull String cksn, @NotNull OnUnbindDeviceCallback callback) {
        mDeviceModel.unbindDeviceToWallet(address, cksn, callback);
    }

    @Override
    public void getDeviceDetail(String cksn, OnDeviceDetailCallback callback) {

    }
}
