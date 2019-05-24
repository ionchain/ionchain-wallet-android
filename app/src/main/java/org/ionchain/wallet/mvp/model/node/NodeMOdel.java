package org.ionchain.wallet.mvp.model.node;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.NodeBean;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.utils.NetUtils;

public class NodeMOdel implements INodeModel {

    @Override
    public void getNodes(String url, final OnIONCNodeCallback callback) {
        NetUtils.get(url, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                LoggerUtils.i(json);
                NodeBean nodeBean = NetUtils.gsonToBean(json, NodeBean.class);
                if (nodeBean == null || nodeBean.getData() == null) {
                    callback.onIONCNodeError(App.mContext.getResources().getString(R.string.error_data_parase));
                    return;
                }
                callback.onIONCNodeSuccess(nodeBean.getData());
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                callback.onIONCNodeStart();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onIONCNodeError(App.mContext.getString(R.string.error_net_node));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onIONCNodeFinish();
            }
        },callback);
    }
}
