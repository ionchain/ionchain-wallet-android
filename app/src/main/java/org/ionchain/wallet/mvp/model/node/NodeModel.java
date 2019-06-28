package org.ionchain.wallet.mvp.model.node;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.bean.NodeBean;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.utils.NetUtils;

import static org.ionchain.wallet.utils.UrlUtils.getNodeListUrl;

public class NodeModel implements INodeModel {

    @Override
    public void getNodes(final OnIONCNodeCallback callback, Object cancelTag) {
        NetUtils.get("getNodes",getNodeListUrl(), new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String json = response.body();
                LoggerUtils.j("node" , json);
                NodeBean nodeBean = NetUtils.gsonToBean(json, NodeBean.class);
                if (nodeBean == null || nodeBean.getData() == null) {
                    callback.onIONCNodeError("");
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
                callback.onIONCNodeError(response.getException().getMessage());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                callback.onIONCNodeFinish();
            }
        }, cancelTag);
    }
}
