package org.ionc.wallet.model.device;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnBindDeviceCallback;
import org.ionc.wallet.callback.OnDeviceDetailCallback;
import org.ionc.wallet.callback.OnDeviceListCallback;
import org.ionc.wallet.callback.OnUnbindDeviceCallback;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 首页数据的业务类
 */
public interface IDeviceModel {
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
     * @param addressSet  所有设备的地址集合
     * @param callback    钱包数据集
     */
    void getAllWalletDeviceList(String addressSet, OnDeviceListCallback callback);

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
     * @param address  钱包地址
     * @param cksn     设备的唯一识别码
     * @param callback
     */
    void unbindDeviceToWallet(String address, String cksn, OnUnbindDeviceCallback callback);

    /**
     * @param cksn     设备识别码
     * @param callback 设备信息
     */
    void getDeviceDetail(String cksn, OnDeviceDetailCallback callback);
}
