package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.TxRecoderBean

/**
 *
 * AUTHOR binny
 *
 * TIME 2018/11/13 16:33
 *
 *
 */
interface OnTxRecoderCallback:OnLoadingView {

    /**
     * 请求成功
     * */
    fun onTxRecoderSuccess(beans: ArrayList<TxRecoderBean.DataBean.ItemBean>)


    /**
     * 请求失败
     * */
    fun onTxRecoderFailure(error: String)
}