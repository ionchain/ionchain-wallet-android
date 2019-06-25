package org.ionchain.wallet.mvp.view.fragment.txrecord;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.bean.TxRecordBeanTemp;

import java.util.List;


public class TxRecordAllFragment extends AbsTxRecordBaseFragment {


    @Override
    protected int getType() {
        return TYPE_ALL;
    }

    /**
     * 获取到网络数据之后{@link #onTxRecordBrowserSuccess(TxRecordBeanTemp.DataBean)}
     * 将网络数据缓存到list view 的数据集中{@link #mListOutAndInData}
     * 在此处理
     *
     * @param listNet
     */
    @Override
    protected void onAfterNetDataSuccess(List<TxRecordBean> listNet) {
        if (listNet.size() == 0) {
            LoggerUtils.i("beannet", "size = 0,无新数据");
            return;
        }
        int size = mListOutAndInData.size();
        LoggerUtils.i("beannet", "size = " + size);
        for (TxRecordBean b :
                listNet) {
            mListOutAndInData.add(0, b);
            IONCWalletSDK.getInstance().saveTxRecordBean(b);
        }
        mTxRecordAdapter.notifyDataSetChanged();
    }



    @Override
    public void onNewTxRecordByTx(TxRecordBean txRecordBean) {
        mListOutAndInData.add(0, txRecordBean);
        super.onNewTxRecordByTx(txRecordBean);
    }

    @Override
    public void onPullToUp(WalletBeanNew walletBeanNew) {

        super.onPullToUp(walletBeanNew);

    }
}
