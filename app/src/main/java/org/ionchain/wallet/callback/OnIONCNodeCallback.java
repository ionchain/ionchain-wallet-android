package org.ionchain.wallet.callback;

import org.ionc.wallet.callback.OnDataParseErrorCallback;
import org.ionchain.wallet.bean.NodeBean;

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
