package org.ionchain.wallet.mvp.presenter.device;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback;
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback;
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback;

/**
 * 设备信息
 */
public interface IDevicePresenter {
    /**
     * 获取用户当前钱包的设备列表
     *
     * @param walletBean 钱包地址
     * @param callback   钱包数据集
     */
    void getCurrentWalletDevicesList(WalletBeanNew walletBean, OnDeviceListCallback callback);

    /**
     * 获取用户所有钱包的相关的设备
     *
     * @param address
     * @param callback    钱包数据集
     */
    void getAllWalletDevicesList(String address, OnDeviceListCallback callback);

    /**
     * 将设备与钱包绑定
     *
     * @param address  钱包地址
     * @param cksn     设备的唯一识别码
     * @param callback 绑定成功，返回的结果
     */
    void bindDeviceToWallet(String address, String cksn, OnBindDeviceCallback callback);

    /**
     * 将设备与钱包解绑定
     *
     * @param address 钱包地址
     * @param cksn    设备的唯一识别码
     */
    void unbindDeviceToWallet(String address, String cksn, OnUnbindDeviceCallback callback);

    /**
     * @param cksn     设备识别码
     * @param callback 设备信息
     */
    void getDeviceDetail(String cksn, OnDeviceDetailCallback callback);
}
