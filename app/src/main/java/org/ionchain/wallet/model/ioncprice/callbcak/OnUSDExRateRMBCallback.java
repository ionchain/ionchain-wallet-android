package org.ionchain.wallet.model.ioncprice.callbcak;

import org.ionchain.wallet.bean.USDExRmb;

/**
 * 获取美元与人民币之间的汇率
 */
public interface OnUSDExRateRMBCallback {
    /**
     * 开始获取汇率
     */
    void onUSDExRateRMBStart();
    /**
     * @param dataBean IONC 的美元价格
     */
    void onUSDExRateRMBSuccess(USDExRmb.DataBean dataBean);

    /**
     * @param error 请求usd价格失败
     */
    void onUSDExRateRMBFailure(String error);


    void onUSDExRateRMBFinish();


}
