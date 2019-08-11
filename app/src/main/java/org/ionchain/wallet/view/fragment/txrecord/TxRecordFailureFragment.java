package org.ionchain.wallet.view.fragment.txrecord;

import org.sdk.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionchain.wallet.view.fragment.AssetFragment;

public class TxRecordFailureFragment extends AbsTxRecordBaseFragment implements OnTxRecordFromNodeCallback,
        AssetFragment.OnPullToRefreshCallback {
    @Override
    protected int getType() {
        return TYPE_FAILURE;
    }

}
