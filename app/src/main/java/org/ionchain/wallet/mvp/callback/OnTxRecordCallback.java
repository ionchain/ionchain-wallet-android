package org.ionchain.wallet.mvp.callback;


import org.ionchain.wallet.bean.TxRecoderBean;

import java.util.List;

/**
 * user: binny
 * date:2018/12/7
 * description：交易记录
 */
public interface OnTxRecordCallback extends OnLoadingView {
    /**
     * 请求成功
     */
    void onTxRecordRefreshSuccess(TxRecoderBean.DataBean beans);

    /**
     * 加载更多
     * @param beans
     */
    void onTxRecordLoadMoreSuccess(List<TxRecoderBean.DataBean.ItemBean> beans);


    /**
     * 请求失败
     */
    void onTxRecordFailure(String error);
}
