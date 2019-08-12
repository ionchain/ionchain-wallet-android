package org.ionc.wallet.presenter.device;

import org.sdk.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBindDeviceCallback;
import org.ionc.wallet.callback.OnDeviceDetailCallback;
import org.ionc.wallet.callback.OnDeviceListCallback;
import org.ionc.wallet.callback.OnUnbindDeviceCallback;
import org.ionc.wallet.model.device.DeviceModel;
import org.ionc.wallet.model.device.IDeviceModel;
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
