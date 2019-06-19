package org.ionchain.wallet.mvp.view.fragment.txrecord;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.mvp.view.fragment.AssetFragment;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Collections;

public class TxRecordOutFragment extends AbsTxRecordBaseFragment implements OnTxRecordFromNodeCallback,
        AssetFragment.OnPullToRefreshCallback {


    @Override
    protected int getType() {
        return TYPE_OUT;
    }


    @Override
    protected void onAfterNetDataSuccess() {

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
        LoggerUtils.e("执行失败 " + recordBean);
        ToastUtil.showShortToast(error);
    }

    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {
        LoggerUtils.i("地址切换，清空缓存 " + TAG + "mCommonAdapter = " + mCommonAdapter);
        mListData.clear();
        mCommonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNewRecord(TxRecordBean txRecordBean) {

    }
}
