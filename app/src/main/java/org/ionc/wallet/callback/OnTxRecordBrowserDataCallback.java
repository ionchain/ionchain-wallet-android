package org.ionc.wallet.callback;


import org.ionc.wallet.bean.TxRecordBeanTemp;

/**
 * user: binny
 * date:2018/12/7
 * description：网络上的交易记录
 */
public interface OnTxRecordBrowserDataCallback extends OnLoadingView {
    /**
     * 请求成功
     */
    void onTxRecordBrowserSuccess(TxRecordBeanTemp.DataBean beans);

    /**
     * 加载更多
     */
    void onTxRecordSuccessDataNUll();


    /**
     * 请求失败
     */
    void onTxRecordBrowserFailure(String error);
}
