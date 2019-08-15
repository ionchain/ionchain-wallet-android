package org.ionc.wallet.view.fragment.txrecord;

import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.view.fragment.AssetFragment;

public class TxRecordFailureFragment extends AbsTxRecordBaseFragment implements OnTxRecordFromNodeCallback,
        AssetFragment.OnPullToRefreshCallback {
    @Override
    protected int getType() {
        return TYPE_FAILURE;
    }

}
