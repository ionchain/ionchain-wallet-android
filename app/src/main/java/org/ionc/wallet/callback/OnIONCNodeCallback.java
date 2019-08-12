package org.ionc.wallet.callback;

import org.sdk.wallet.callback.OnDataParseErrorCallback;
import org.ionc.wallet.bean.NodeBean;

import java.util.List;

public interface OnIONCNodeCallback extends OnDataParseErrorCallback {
    void onIONCNodeStart();
    /**
     * @param dataBean 主链节点
     */
    void onIONCNodeSuccess(List<NodeBean.DataBean> dataBean);

    void onIONCNodeError(String error);

    void onIONCNodeFinish();
}
