package org.ionc.wallet.model.ioncprice;

import org.ionc.wallet.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionc.wallet.model.ioncprice.callbcak.OnUSDPriceCallback;

/**
 * 获取离子比对应的美元价格以及人民币汇率
 */
public interface IPriceModel {
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
