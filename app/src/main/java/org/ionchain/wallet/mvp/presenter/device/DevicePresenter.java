package org.ionchain.wallet.mvp.presenter.device;

import org.ionc.wallet.bean.WalletBean;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;
import org.ionchain.wallet.mvp.model.device.DeviceModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 设别信息
 */
public class DevicePresenter implements IDevicePresenter {

    private DeviceModel mDeviceModel;

    public DevicePresenter() {
        mDeviceModel = new DeviceModel();
    }

    @Override
    public void getCurrentWalletDevicesList(@NotNull WalletBean walletBean, @NotNull OnDeviceListCallback callback) {
        mDeviceModel.getCurrentWalletDevicesList(walletBean, callback);
    }

    @Override
    public void getAllWalletDevicesList(List<WalletBean> walletBeans, OnDeviceListCallback callback) {

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
