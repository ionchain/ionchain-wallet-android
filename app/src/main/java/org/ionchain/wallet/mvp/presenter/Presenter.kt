package org.ionchain.wallet.mvp.presenter

import org.ionchain.wallet.bean.WalletBean
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback
import org.ionchain.wallet.mvp.model.home.HomePageModel

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 枢纽
 */
class Presenter : IPresenter {
    private var mHomePageModel: HomePageModel? = null

    fun initHomePageModel() {
        if (mHomePageModel == null) {
            mHomePageModel = HomePageModel()
        }
    }

    override fun getCurrentWalletDevicesList(walletBean: WalletBean, listData: OnDeviceListCallback) {
        mHomePageModel!!.getCurrentWalletDevicesList(walletBean, listData)
    }

    override fun bindDeviceToWallet(address: String, cksn: String, callback: OnBindDeviceCallback) {
        mHomePageModel!!.bindDeviceToWallet(address, cksn, callback)
    }

    override fun unbindDeviceToWallet(address: String, cksn: String, callback: OnUnbindDeviceCallback) {
        mHomePageModel!!.unbindDeviceToWallet(address, cksn, callback)
    }

    override fun getDeviceDetail(cksn: String, deviceBean: OnDeviceDetailCallback) {

    }

    override fun getAllWalletDevicesList(walletBeans: List<WalletBean>, callback: OnDeviceListCallback) {

    }
}
