package org.ionc.wallet.model.txrecoder;


import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.App;
import org.ionchain.wallet.R;
import org.ionc.wallet.bean.TxRecordBeanTemp;
import org.ionc.wallet.callback.OnTxRecordBrowserDataCallback;
import org.ionc.wallet.utils.NetUtils;

/**
 * AUTHOR binny
 * <p>
 * <p>
 * TIME 2018/11/13 16:15
 */
public class TxRecordModel implements ITxRecoderModel {
    final HttpParams params = new HttpParams();


    /**
     * @param url
     * @param type       类型
     * @param address    钱包地址
     * @param pageNumber 页数
     * @param pageSize   每页的数据量
     * @param callback   请求回调
     */
    @Override
    public void getTxRecord(String url, String type, String address, String pageNumber, String pageSize, final OnTxRecordBrowserDataCallback callback) {
        params.clear();
        params.put("type", type);
        params.put("key", address);
        params.put("pageNumber", pageNumber);
        params.put("pageSize", pageSize);
        NetUtils.get("getTxRecord", url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                LoggerUtils.j(json);
                TxRecordBeanTemp txRecordBeanTemp = NetUtils.gsonToBean(json, TxRecordBeanTemp.class);
                if (txRecordBeanTemp == null || txRecordBeanTemp.getData() == null || txRecordBeanTemp.getData().getData() == null || txRecordBeanTemp.getData().getData().size() == 0) {
                    callback.onTxRecordSuccessDataNUll();
                    return;
                }
                TxRecordBeanTemp.DataBean beans = txRecordBeanTemp.getData();
                callback.onTxRecordBrowserSuccess(beans);

            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onLoadFinish();
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                callback.onLoadStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onTxRecordBrowserFailure(App.mAppInstance.getString(R.string.error_net_tx_recorder));
            }
        }, callback);
    }
}
