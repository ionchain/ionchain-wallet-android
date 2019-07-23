package org.ionchain.wallet.model.node;

import org.ionchain.wallet.callback.OnIONCNodeCallback;

public interface INodeModel {
    /**
     *
     * @param callback
     * @param cancelTag
     */
    void getNodes(OnIONCNodeCallback callback, Object cancelTag);
}
