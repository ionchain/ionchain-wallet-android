package org.ionchain.wallet.mvp.presenter.node;

import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.model.node.NodeModel;

public class IONCNodePresenter implements INodePresenter {
    private NodeModel mNodeModel;

    public IONCNodePresenter() {
        mNodeModel = new NodeModel();
    }

    @Override
    public void getNodes(OnIONCNodeCallback callback) {
        mNodeModel.getNodes(callback);
    }
}
