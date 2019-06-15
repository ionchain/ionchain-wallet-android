package org.ionchain.wallet.mvp.model.node;

import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;

public interface INodeModel {
    /**
     *
     * @param callback
     * @param cancelTag
     */
    void getNodes(OnIONCNodeCallback callback, Object cancelTag);
}
