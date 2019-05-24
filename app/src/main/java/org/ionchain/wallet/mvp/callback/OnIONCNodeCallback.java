package org.ionchain.wallet.mvp.callback;

import org.ionchain.wallet.bean.NodeBean;

import java.util.List;

public interface OnIONCNodeCallback {
    /**
     * @param dataBean 主链节点
     */
    void onIONCNodeSuccess(List<NodeBean.DataBean> dataBean);

    void onIONCNodeError(String error);

    void onIONCNodeStart();

    void onIONCNodeFinish();
}
