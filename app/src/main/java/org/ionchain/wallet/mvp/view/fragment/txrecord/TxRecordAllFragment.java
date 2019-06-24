package org.ionchain.wallet.mvp.view.fragment.txrecord;

import android.text.TextUtils;
import android.util.Log;

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
     * 将网络数据缓存到list view 的数据集中{@link #mListData}
     * 在此处理
     *
     * @param listNet
     */
    @Override
    protected void onAfterNetDataSuccess(List<TxRecordBean> listNet) {
        int size = mListData.size();
        for (TxRecordBean b :
                listNet) {

            for (int i = 0; i < size; i++) {
                if (TextUtils.isEmpty(mListData.get(i).getHash())) {
                    Log.i("beannet", "本地哈希为空 ");
                    continue;
                }
                if (b.getHash().contains(mListData.get(i).getHash())) {
                    Log.i("beannet", "已存在 = " + mListData.get(i).getHash() + " = " + b.getHash());
                }
            }
            Log.i("beannet", "不存在 = " + b.toString());
            mListData.add(b);

        }
        mCommonAdapter.notifyDataSetChanged();
    }

    @Override
    protected void visible() {

    }

    /**
     * 数据集指向新的钱包记录
     *
     * @param currentWallet 新钱包
     */
    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {
        LoggerUtils.i("local-data", "地址切换");
        mWalletBeanNew = currentWallet;
        mListIn.clear();
        mListOut.clear();
        mListData.clear();
        if (mCommonAdapter == null) {
            LoggerUtils.i("地址切换 = mCommonAdapter999 =null");
            return;
        }
        mCommonAdapter.notifyDataSetChanged();
        getLocalData();//onAddressChanged
    }

    @Override
    public void onPullToDown(WalletBeanNew walletBeanNew) {
        super.onPullToDown(walletBeanNew);
        if (mVisibleToUser) {
            LoggerUtils.i("beannet", "all");
            mTxRecordPresenter.getTxRecordAll("3", mWalletBeanNew.getAddress(), "1", "10", this);
        }
    }

    @Override
    public void onNewTxRecordByTx(TxRecordBean txRecordBean) {
        mListData.add(0, txRecordBean);
        super.onNewTxRecordByTx(txRecordBean);
    }
}
