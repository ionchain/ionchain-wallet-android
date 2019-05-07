package org.ionchain.wallet.mvp.model.txrecoder;


import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.bean.TxRecoderBean;
import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.constant.ConstantErrorCode;
import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;
import org.ionchain.wallet.utils.NetUtils;

import java.util.List;

import static org.ionchain.wallet.constant.ConstantUrl.TX_RECORD_URL_GET;

/**
 * AUTHOR binny
 * <p>
 * <p>
 * TIME 2018/11/13 16:15
 */
public class TxRecoderModel implements ITxRecoderModel {
    final HttpParams params = new HttpParams();


    @Override
    public void getTxRecoder(String type, String key, String pageNumber, String pageSize, final OnTxRecordCallback callback) {
        params.clear();
        params.put("type", type);
        params.put("key", key);
        params.put("pageNumber", pageNumber);
        params.put("pageSize", pageSize);
        NetUtils.get(TX_RECORD_URL_GET, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                Logger.i(json);
                TxRecoderBean txRecoderBean = NetUtils.gsonToBean(json, TxRecoderBean.class);
                if (txRecoderBean == null || txRecoderBean.getData() == null || txRecoderBean.getData().getData() == null) {
                    callback.onTxRecordFailure(App.mContext.getResources().getString(R.string.data_parase_error));
                    return;
                }
                List<TxRecoderBean.DataBean.ItemBean> beans = txRecoderBean.getData().getData();
                callback.onTxRecordSuccess(beans);
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
                callback.onTxRecordFailure(ConstantErrorCode.ERROR_CODE_NET_WORK_ERROR_TX_RECORD);
            }
        }, callback);
    }
}
