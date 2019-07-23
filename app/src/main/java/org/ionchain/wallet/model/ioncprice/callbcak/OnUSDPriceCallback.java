package org.ionchain.wallet.model.ioncprice.callbcak;

public interface OnUSDPriceCallback {
    /**
     * 开始获取余额,可以用来显示获取状态,比如一个进度条
     */
    void onUSDPriceStart();
    /**
     * @param usdPrice IONC 的美元价格
     */
    void onUSDPriceSuccess(double usdPrice);

    /**
     * @param error 请求usd价格失败
     */
    void onUSDPriceFailure(String error);

    void onUSDPriceFinish();
}
