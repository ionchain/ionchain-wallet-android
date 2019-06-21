package org.ionchain.wallet.mvp.view.fragment.txrecord;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.bean.TxRecordBeanTemp;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Collections;
import java.util.List;


public class TxRecordAllFragment extends AbsTxRecordBaseFragment implements
        OnTxRecordFromNodeCallback {


    @Override
    protected int getType() {
        return TYPE_ALL;
    }

    /**
     * 获取到网络数据之后{@link #onTxRecordRefreshNetDataSuccess(TxRecordBeanTemp.DataBean)}
     * 将网络数据缓存到list view 的数据集中{@link #mListData}
     * 在此处理
     * @param listNet
     */
    @Override
    protected void onAfterNetDataSuccess(List<TxRecordBean> listNet) {
        for (TxRecordBean b :
                listNet) {
            LoggerUtils.i("bean", b.toString());
        }
    }

    @Override
    public void OnTxRecordNodeSuccess(TxRecordBean txRecordBean) {
        LoggerUtils.i("执行完成 " + txRecordBean.toString());
        if (mTxHashUnpackedTemp.contains(txRecordBean)) {
            mTxHashUnpackedTemp.remove(txRecordBean);//移除已打包
            LoggerUtils.i("txRecordBean " + txRecordBean.toString());
            IONCWalletSDK.getInstance().updateTxRecordBean(txRecordBean);
            Collections.sort(mListData);
            mCommonAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean recordBean) {
        LoggerUtils.i("执行失败 " + recordBean);
        ToastUtil.showShortToast(error);
    }


    /**
     * 数据集指向新的钱包记录
     *
     * @param currentWallet 新钱包
     */
    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {
        LoggerUtils.i("地址切换");
        mWalletBeanNew = currentWallet;
        mListIn.clear();
        mListOut.clear();
        mListData.clear();
        if (mCommonAdapter == null) {
            LoggerUtils.i("地址切换 = mCommonAdapter999 =null");
            return;
        }
        mCommonAdapter.notifyDataSetChanged();
        getLocalData();
    }

    @Override
    public void onPullToDown(WalletBeanNew walletBeanNew) {
        super.onPullToDown(walletBeanNew);
        if (isVisible()) {
            mTxRecordPresenter.getTxRecordAll("3", mWalletBeanNew.getAddress(), "1", "10", this);
        }
    }
}
