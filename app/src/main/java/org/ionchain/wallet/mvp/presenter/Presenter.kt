package org.ionchain.wallet.mvp.presenter

import org.ionc.wallet.bean.WalletBean
import org.ionc.wallet.callback.OnTxRecoderCallback
import org.ionchain.wallet.mvp.callback.OnBindDeviceCallback
import org.ionchain.wallet.mvp.callback.OnDeviceDetailCallback
import org.ionchain.wallet.mvp.callback.OnDeviceListCallback
import org.ionchain.wallet.mvp.callback.OnUnbindDeviceCallback
import org.ionchain.wallet.mvp.model.home.HomePageModel
import org.ionchain.wallet.mvp.model.txrecoder.TxRecoderModel

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 枢纽
 */
class Presenter : IPresenter {
    override fun getTxRedocer(type: String, key: String, pageNumber: String, pageSize: String,callback: OnTxRecoderCallback) {
        var model = TxRecoderModel()
        model.getTxRecoder(type,key,pageNumber,pageSize,callback)
    }

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
