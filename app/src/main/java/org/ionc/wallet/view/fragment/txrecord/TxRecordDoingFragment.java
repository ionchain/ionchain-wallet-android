package org.ionc.wallet.view.fragment.txrecord;

import org.sdk.wallet.bean.TxRecordBean;
import org.sdk.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.view.fragment.AssetFragment;

public class TxRecordDoingFragment extends AbsTxRecordBaseFragment implements OnTxRecordFromNodeCallback,
        AssetFragment.OnPullToRefreshCallback {
    OnPullToDownSuccessCallback mSuccessCallback;

    public void setSuccessCallback(OnPullToDownSuccessCallback successCallback) {
        mSuccessCallback = successCallback;
    }

    @Override
    protected int getType() {
        return TYPE_DOING;
    }


    public interface OnPullToDownSuccessCallback{
        void onSuccess(TxRecordBean txRecordBean);
    }

    @Override
    protected void pullToDownSuccess(TxRecordBean txRecordBean) {
        super.pullToDownSuccess(txRecordBean);
        mSuccessCallback.onSuccess(txRecordBean);
    }
}
