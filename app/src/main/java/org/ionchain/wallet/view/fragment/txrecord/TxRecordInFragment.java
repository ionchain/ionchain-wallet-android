package org.ionchain.wallet.view.fragment.txrecord;

import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionchain.wallet.view.fragment.AssetFragment;

public class TxRecordInFragment extends AbsTxRecordBaseFragment implements OnTxRecordFromNodeCallback,
        AssetFragment.OnPullToRefreshCallback {
    @Override
    protected int getType() {
        return TYPE_IN;
    }

}
