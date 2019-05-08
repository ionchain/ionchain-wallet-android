package org.ionchain.wallet.mvp.presenter.ioncrmb;

import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDPriceCallback;

/**
 * 获取币价信息
 */
public interface IPricePresenter {
    /**
     * @param usdPriceCallback 获取美元价格/IONC
     */
    void getUSDPrice(OnUSDPriceCallback usdPriceCallback);

    /**
     *
     * @param usdExRateRMBCallback 获取人民币与美元的汇率
     */
    void getUSDExchangeRateRMB(OnUSDExRateRMBCallback usdExRateRMBCallback);
}
