package org.ionchain.wallet.presenter.node;

import org.ionchain.wallet.callback.OnIONCNodeCallback;
import org.ionchain.wallet.model.node.INodeModel;
import org.ionchain.wallet.model.node.NodeModel;

public class IONCNodePresenter implements INodeModel {
    private NodeModel mNodeModel;

    public IONCNodePresenter() {
        mNodeModel = new NodeModel();
    }

    @Override
    public void getNodes(OnIONCNodeCallback callback, Object cancelTag) {
        mNodeModel.getNodes(callback, cancelTag);
    }
}
