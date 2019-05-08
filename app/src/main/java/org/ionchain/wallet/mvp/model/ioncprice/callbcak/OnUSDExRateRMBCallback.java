package org.ionchain.wallet.mvp.model.ioncprice.callbcak;

/**
 * 获取美元与人民币之间的汇率
 */
public interface OnUSDExRateRMBCallback {
    /**
     * 开始获取汇率
     */
    void onUSDExRateRMBStart();
    /**
     * @param usdPrice IONC 的美元价格
     */
    void onUSDExRateRMBSuccess(double usdPrice);

    /**
     * @param error 请求usd价格失败
     */
    void onUSDExRateRMBFailure(String error);
}
