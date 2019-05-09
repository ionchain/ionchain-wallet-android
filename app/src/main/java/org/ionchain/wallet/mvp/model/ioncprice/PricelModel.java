package org.ionchain.wallet.mvp.model.ioncprice;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.bean.USDExRmb;
import org.ionchain.wallet.bean.USDPriceBean;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionchain.wallet.mvp.model.ioncprice.callbcak.OnUSDPriceCallback;
import org.ionchain.wallet.utils.NetUtils;

import java.util.Objects;

import static org.ionchain.wallet.constant.ConstantUrl.URL_USD_EX_RATE_RMB_PRICE;
import static org.ionchain.wallet.constant.ConstantUrl.URL_USD_PRICE;

public class PricelModel implements IPriceModel {
    private String TAG = this.getClass().getSimpleName();

    /**
     * 先获取对应的美元价格
     * 在获取USD--RMB 的汇率
     *
     * @param usdPriceCallback 获取美元价格/IONC
     */
    @Override
    public void getUSDPrice(final OnUSDPriceCallback usdPriceCallback) {
        //todo price
        NetUtils.get(URL_USD_PRICE, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                Logger.j(TAG, "onCreateSuccess: $json" + json);
                USDPriceBean updateBean = NetUtils.gsonToBean(json, USDPriceBean.class);
                usdPriceCallback.onUSDPriceSuccess(Objects.requireNonNull(updateBean).getData().getMarketinfo().getPrice());
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                Logger.i(":onStart");
                usdPriceCallback.onUSDPriceStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Logger.i("onError");
                usdPriceCallback.onUSDPriceFailure(response.getException().getMessage());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Logger.i("onFinish");
            }
        }, usdPriceCallback);
    }

    /**
     * @param usdExRateRMBCallback 获取人民币与美元的汇率
     */
    @Override
    public void getUSDExchangeRateRMB(final OnUSDExRateRMBCallback usdExRateRMBCallback) {
        NetUtils.get(URL_USD_EX_RATE_RMB_PRICE, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                Logger.j(TAG, "onCreateSuccess: $json" + json);
                USDExRmb usdExRmb = NetUtils.gsonToBean(json, USDExRmb.class);
                usdExRateRMBCallback.onUSDExRateRMBSuccess(Objects.requireNonNull(usdExRmb).getData());
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                Logger.i(":onStart");
                usdExRateRMBCallback.onUSDExRateRMBStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Logger.i("onError");
                usdExRateRMBCallback.onUSDExRateRMBFailure(response.getException().getMessage());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Logger.i("onFinish");
            }
        }, usdExRateRMBCallback);
    }
}
