package org.ionchain.wallet.mvp.presenter

import org.ionchain.wallet.bean.WalletBean
import org.ionchain.wallet.mvp.callback.*

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 中间层
 */
interface IPresenter {

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
    fun getAllWalletDevicesList(walletBeans: List<WalletBean>, callback: OnDeviceListCallback)

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
     * @param address 钱包地址
     * @param cksn    设备的唯一识别码
     */
    fun unbindDeviceToWallet(address: String, cksn: String, callback: OnUnbindDeviceCallback)

    /**
     * @param cksn     设备识别码
     * @param callback 设备信息
     */
    fun getDeviceDetail(cksn: String, callback: OnDeviceDetailCallback)
    /**
     * @param type
     * @param key 钱包地址
     * @param pageNumber
     * @param pageSize
     * */
    fun  getTxRedocer(type:String,key:String,pageNumber:String,pageSize:String,callback: OnTxRecoderCallback)
}
