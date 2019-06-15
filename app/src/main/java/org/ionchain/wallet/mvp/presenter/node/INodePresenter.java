package org.ionchain.wallet.mvp.presenter.node;

import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;

public interface INodePresenter {
    /**
     *
     * @param callback
     * @param cancelTag
     */
    void getNodes(OnIONCNodeCallback callback, Object cancelTag);

}
