package org.ionchain.wallet.mvp.model.ioncprice;

import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDPriceCallback;

public class PricelModel implements IPriceModel {
    /**
     * 先获取对应的美元价格
     * 在获取USD--RMB 的汇率
     * @param usdPriceCallback 获取美元价格/IONC
     */
    @Override
    public void getUSDPrice(OnUSDPriceCallback usdPriceCallback) {
          //todo price
    }

    /**
     * @param usdExRateRMBCallback 获取人民币与美元的汇率 
     */
    @Override
    public void getUSDExchangeRateRMB(OnUSDExRateRMBCallback usdExRateRMBCallback) {

    }
}
