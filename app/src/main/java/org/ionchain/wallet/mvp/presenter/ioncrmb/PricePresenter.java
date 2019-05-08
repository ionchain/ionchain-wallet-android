package org.ionchain.wallet.mvp.presenter.ioncrmb;

import org.ionchain.wallet.mvp.model.ioncprice.PricelModel;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDPriceCallback;

public class PricePresenter implements IPricePresenter {
    private PricelModel mPricelModel;

    public PricePresenter() {
        mPricelModel = new PricelModel();
    }

    @Override
    public void getUSDPrice(OnUSDPriceCallback usdPriceCallback) {
        mPricelModel.getUSDPrice(usdPriceCallback);
    }

    @Override
    public void getUSDExchangeRateRMB(OnUSDExRateRMBCallback usdExRateRMBCallback) {
        mPricelModel.getUSDExchangeRateRMB(usdExRateRMBCallback);
    }
}
