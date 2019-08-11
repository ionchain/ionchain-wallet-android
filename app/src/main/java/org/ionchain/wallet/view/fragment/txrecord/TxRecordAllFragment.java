package org.ionchain.wallet.view.fragment.txrecord;

import org.sdk.wallet.bean.TxRecordBean;


public class TxRecordAllFragment extends AbsTxRecordBaseFragment {


    @Override
    protected int getType() {
        return TYPE_ALL;
    }


    @Override
    public void onNewTxRecordByTx(TxRecordBean txRecordBean) {
        mListAllData.add(0, txRecordBean);
        super.onNewTxRecordByTx(txRecordBean);
    }

}
