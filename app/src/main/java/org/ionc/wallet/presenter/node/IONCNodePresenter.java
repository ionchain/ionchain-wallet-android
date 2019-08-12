package org.ionc.wallet.presenter.node;

import org.ionc.wallet.callback.OnIONCNodeCallback;
import org.ionc.wallet.model.node.INodeModel;
import org.ionc.wallet.model.node.NodeModel;

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
