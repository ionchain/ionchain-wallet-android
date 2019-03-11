package org.ionc.wallet.sdk.callback;

import org.ionc.wallet.sdk.bean.TxRecoderBean;

import java.util.ArrayList;

/**
 * user: binny
 * date:2018/12/7
 * description：交易记录
 */
public interface OnTxRecoderCallback {
    /**
     * 请求成功
     */
    void onTxRecoderSuccess(ArrayList<TxRecoderBean.DataBean.ItemBean> beans);


    /**
     * 请求失败
     */
    void onTxRecoderFailure(String error);
}
