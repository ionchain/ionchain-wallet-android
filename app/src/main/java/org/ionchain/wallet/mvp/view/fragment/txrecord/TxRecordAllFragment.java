package org.ionchain.wallet.mvp.view.fragment.txrecord;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;


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

    @Override
    public void onPullToUp(WalletBeanNew walletBeanNew) {

        super.onPullToUp(walletBeanNew);

    }
}
