package org.ionchain.wallet.mvp.presenter.node;

import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.model.node.NodeMOdel;

public class IONCNodePresenter implements INodePresenter {
    private NodeMOdel mNodeMOdel;

    public IONCNodePresenter() {
        mNodeMOdel = new NodeMOdel();
    }

    @Override
    public void getNodes(String url, OnIONCNodeCallback callback) {
        mNodeMOdel.getNodes(url,callback);
    }
}
