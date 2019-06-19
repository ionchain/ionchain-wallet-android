package org.ionchain.wallet.mvp.view.fragment.txrecord;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.bean.TxRecordBeanTemp;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Collections;


public class TxRecordAllFragment extends AbsTxRecordBaseFragment implements
        OnTxRecordFromNodeCallback
         {


    @Override
    protected int getType() {
        return TYPE_ALL;
    }


    @Override
    protected void handleShow() {
    }

    @Override
    protected void handleHidden() {
    }

    /**
     * 获取到网络数据之后{@link #onTxRecordRefreshNetDataSuccess(TxRecordBeanTemp.DataBean)}
     * 将网络数据缓存到list view 的数据集中{@link #mListData}
     * 在此处理
     */
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
            adapterLv.notifyDataSetChanged();
        }
    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean recordBean) {
        LoggerUtils.i("执行失败 " + recordBean);
        ToastUtil.showShortToast(error);
    }



}
