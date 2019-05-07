package org.ionchain.wallet.mvp.model.device

import org.ionc.wallet.bean.WalletBean
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 首页数据的业务类
 */
interface IDeviceModel {
    /**
     * 获取用户当前钱包的设备列表
     *
     * @param walletBean 钱包地址
     * @param callback   钱包数据集
     */
    fun getCurrentWalletDevicesList(walletBean: WalletBean, callback: OnDeviceListCallback)

    /**
     * 获取用户所有钱包的相关的设备
     *
     * @param walletBeans 用户的所有钱包
     * @param callback    钱包数据集
     */
    fun getAllWalletDeviceList(walletBeans: List<WalletBean>, callback: OnDeviceListCallback)

    /**
     * 将设备与钱包绑定
     *
     * @param address  钱包地址
     * @param cksn     设备的唯一识别码
     * @param callback 绑定成功，返回的结果
     */
    fun bindDeviceToWallet(address: String, cksn: String, callback: OnBindDeviceCallback)

    /**
     * 将设备与钱包解绑定
     *
     * @param address  钱包地址
     * @param cksn     设备的唯一识别码
     * @param callback
     */
    fun unbindDeviceToWallet(address: String, cksn: String, callback: OnUnbindDeviceCallback)

    /**
     * @param cksn     设备识别码
     * @param callback 设备信息
     */
    fun getDeviceDetail(cksn: String, callback: OnDeviceDetailCallback)
}
