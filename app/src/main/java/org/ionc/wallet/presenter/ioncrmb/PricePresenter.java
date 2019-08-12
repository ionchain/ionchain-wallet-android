package org.ionc.wallet.presenter.ioncrmb;

import org.ionc.wallet.model.ioncprice.IPriceModel;
import org.ionc.wallet.model.ioncprice.PriceModel;
import org.ionc.wallet.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionc.wallet.model.ioncprice.callbcak.OnUSDPriceCallback;

public class PricePresenter implements IPriceModel {
    private PriceModel mPriceModel;

    public PricePresenter() {
        mPriceModel = new PriceModel();
    }

    @Override
    public void getUSDPrice(OnUSDPriceCallback usdPriceCallback) {
        mPriceModel.getUSDPrice(usdPriceCallback);
    }

    @Override
    public void getUSDExchangeRateRMB(OnUSDExRateRMBCallback usdExRateRMBCallback) {
        mPriceModel.getUSDExchangeRateRMB(usdExRateRMBCallback);
    }
}
