package org.ionchain.wallet.mvp.presenter.node;

import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;

public interface INodePresenter {
    /**
     *
     * @param callback
     */
    void getNodes(String url , OnIONCNodeCallback callback);

}
