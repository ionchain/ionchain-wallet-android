package org.ionchain.wallet.mvp.presenter.ioncrmb;

import org.ionchain.wallet.mvp.model.ioncprice.PriceModel;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDPriceCallback;

public class PricePresenter implements IPricePresenter {
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
