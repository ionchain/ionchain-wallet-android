package org.ionc.wallet.model.ioncprice;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.bean.USDExRmb;
import org.ionc.wallet.bean.USDPriceBean;
import org.ionc.wallet.model.ioncprice.callbcak.OnUSDExRateRMBCallback;
import org.ionc.wallet.model.ioncprice.callbcak.OnUSDPriceCallback;
import org.ionc.wallet.utils.NetUtils;

import java.util.Objects;

import static org.ionc.wallet.constant.ConstantNetCancelTag.NET_CANCEL_TAG_USD_PRICE;
import static org.ionc.wallet.constant.ConstantNetCancelTag.NET_CANCEL_TAG_USD_RMB_RATE;
import static org.ionc.wallet.utils.UrlUtils.getUSDExRateUrl;
import static org.ionc.wallet.utils.UrlUtils.getUSDPriceUrl;

public class PriceModel implements IPriceModel {

    /**
     * 先获取对应的美元价格
     * 在获取USD--RMB 的汇率
     *
     * @param usdPriceCallback 获取美元价格/IONC
     */
    @Override
    public void getUSDPrice(final OnUSDPriceCallback usdPriceCallback) {
        //todo price
        NetUtils.get("getUSDPrice", getUSDPriceUrl(), new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                LoggerUtils.j( json);
                USDPriceBean updateBean = NetUtils.gsonToBean(json, USDPriceBean.class);
                if (updateBean == null||updateBean.getData()==null||updateBean.getData().getMarketinfo()==null) {
                    usdPriceCallback.onUSDPriceFailure("data");
                    return;
                }  else {
                    usdPriceCallback.onUSDPriceSuccess(Objects.requireNonNull(updateBean).getData().getMarketinfo().getPrice());
                }
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                LoggerUtils.i(":onTxStart");
                usdPriceCallback.onUSDPriceStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                usdPriceCallback.onUSDPriceFailure(response.getException().getMessage());
            }

            @Override
            public void onFinish() {
                usdPriceCallback.onUSDPriceFinish();
            }
        }, NET_CANCEL_TAG_USD_PRICE);
    }

    /**
     * @param usdExRateRMBCallback 获取人民币与美元的汇率
     */
    @Override
    public void getUSDExchangeRateRMB(final OnUSDExRateRMBCallback usdExRateRMBCallback) {
        NetUtils.get("getUSDExchangeRateRMB", getUSDExRateUrl(), new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                LoggerUtils.i("getUSDExchangeRateRMB: $json" + json);
                USDExRmb usdExRmb = NetUtils.gsonToBean(json, USDExRmb.class);
                try {
                    usdExRateRMBCallback.onUSDExRateRMBSuccess(Objects.requireNonNull(usdExRmb).getData());

                }catch (NullPointerException e) {
                    usdExRateRMBCallback.onUSDExRateRMBFailure("dataerror");
                }
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                LoggerUtils.i(":onTxStart");
                usdExRateRMBCallback.onUSDExRateRMBStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                LoggerUtils.i("onError");
                usdExRateRMBCallback.onUSDExRateRMBFailure(response.getException().getMessage());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                LoggerUtils.i("onFinish");
                usdExRateRMBCallback.onUSDExRateRMBFinish();
            }
        }, NET_CANCEL_TAG_USD_RMB_RATE);
    }
}
