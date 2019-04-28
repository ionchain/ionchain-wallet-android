package org.ionc.wallet.callback;

import org.ionc.wallet.bean.TxRecoderBean;

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
    void onTxRecordSuccess(ArrayList<TxRecoderBean.DataBean.ItemBean> beans);


    /**
     * 请求失败
     */
    void onTxRecordFailure(String error);
}
