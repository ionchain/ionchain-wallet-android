package org.ionc.wallet.model.node;

import org.ionc.wallet.callback.OnIONCNodeCallback;

public interface INodeModel {
    /**
     *
     * @param callback
     * @param cancelTag
     */
    void getNodes(OnIONCNodeCallback callback, Object cancelTag);
}
