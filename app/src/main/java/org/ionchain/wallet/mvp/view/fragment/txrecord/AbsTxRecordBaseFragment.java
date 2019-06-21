package org.ionchain.wallet.mvp.view.fragment.txrecord;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.lzy.okgo.OkGo;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.DateUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordViewHelper;
import org.ionchain.wallet.bean.TxRecordBeanTemp;
import org.ionchain.wallet.mvp.callback.OnTxRecordBrowserDataCallback;
import org.ionchain.wallet.mvp.presenter.transcation.TxRecordPresenter;
import org.ionchain.wallet.mvp.view.activity.transaction.TxRecordDetailActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseViewPagerFragment;
import org.ionchain.wallet.mvp.view.fragment.AssetFragment;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_TX_RECORD;

/**
 * 1、先网络数据，
 * 2、如果网络数据获取失败，则直接显示本地数据
 * 3、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
 */
public abstract class AbsTxRecordBaseFragment extends AbsBaseViewPagerFragment implements OnTxRecordBrowserDataCallback, AssetFragment.OnPullToRefreshCallback, OnTxRecordFromNodeCallback, TxRecordViewHelper.OnTxRecordItemClickedListener {

    private List<TxRecordBean> mListOutTemp = new ArrayList<>();
    private List<TxRecordBean> mListInTemp = new ArrayList<>();

    /**
     * @param walletBeanNew 初始化所所需的钱保
     */
    public void initRecordWalletBean(WalletBeanNew walletBeanNew) {
        mWalletBeanNew = walletBeanNew;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tx_record_all;
    }

    @Override
    protected void initView(View view) {
        ListView tx_record_lv = view.findViewById(R.id.tx_record_lv);
        mTxRecordViewHelper = new TxRecordViewHelper(this);
        switch (getType()) {
            case TYPE_ALL:
                mCommonAdapter = new CommonAdapter(mActivity, mListData, R.layout.item_txrecord, mTxRecordViewHelper);
                break;
            case TYPE_OUT:
                mCommonAdapter = new CommonAdapter(mActivity, mListOut, R.layout.item_txrecord, mTxRecordViewHelper);
                break;
            case TYPE_IN:
                mCommonAdapter = new CommonAdapter(mActivity, mListIn, R.layout.item_txrecord, mTxRecordViewHelper);
                break;
        }
        tx_record_lv.setAdapter(mCommonAdapter);
    }

    @Override
    protected void visible() {
        LoggerUtils.i("visible ", "TAG_NAME = " + TAG_NAME + " visible() = " + isVisible());
        getLocalData();
    }

    /**
     * @return 当前页的类型
     */
    protected abstract int getType();

    @Override
    protected void initData() {

        LoggerUtils.i(TAG, "initData ");
        getLocalData();
//        getNetData();
    }

    protected void getLocalData() {

        String address = mWalletBeanNew.getAddress();

        /*
         * 获取本地数据集的缓存
         */

        mListOutTemp.clear();
        mListInTemp.clear();

        mListOutTemp = IONCWalletSDK.getInstance().getAllTxRecordByTxOutAddress(address);
        mListInTemp = IONCWalletSDK.getInstance().getAllTxRecordBeansByTxInAddress(address);
        LoggerUtils.i("local-data", "访问本地数据库.........."); // TODO: 2019-06-21 可优化
        switch (getType()) {
            case TYPE_ALL:
                /*
                 * 获取该地址下的所有的转出和转入记录
                 * 1、比较哈希值
                 * 1.1、如果数据集里已存在该哈希值，则不添加该记录；
                 * 1.2、不存在则添加
                 * 2、如果哈希值为空，则说明本地交易失败，比较交易时间
                 * 2.1 、交易时间存在则，不添加；
                 * 2.2、不存在，则添加
                 */
                if (mListOutTemp.size() == 0 && mListInTemp.size() == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
                    return;
                }
                mListData.clear();
                mListData.addAll(mListOutTemp);
                mListData.addAll(mListInTemp);
                LoggerUtils.i("mListDataTemp.size = " + mListDataTemp.size());
                Collections.sort(mListData);
                break;
            case TYPE_OUT:
                if (mListOutTemp.size() == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
                    return;
                }
                mListOut.clear();
                mListOut.addAll(mListOutTemp);
                Collections.sort(mListOut);
                break;
            case TYPE_IN:
                if (mListInTemp.size() == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
                    return;
                }
                mListIn.clear();
                mListIn.addAll(mListInTemp);
                Collections.sort(mListIn);
                break;
        }
        mCommonAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPause() {
        super.onPause();
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), this);
    }

    /**
     * 网络上，带地址的所有交易记录
     * {@link org.ionchain.wallet.mvp.model.txrecoder.TxRecordModel#getTxRecord(String, String, String, String, String, OnTxRecordBrowserDataCallback)}  }
     *
     * @param beans 浏览器接口中数据
     */
    @Override
    public void onTxRecordBrowserSuccess(TxRecordBeanTemp.DataBean beans) {
        LoggerUtils.i("beannet", "网络请求成功");
        mListNetTemp.clear();
        for (TxRecordBeanTemp.DataBean.ItemBean itemBean :
                beans.getData()) {
            LoggerUtils.i("beannet", "网络请求成功" + beans.toString());
            TxRecordBean bean = new TxRecordBean();
            bean.setHash(itemBean.getHash());
            bean.setTc_in_out(DateUtils.getDateToString(System.currentTimeMillis(), Y4M2D2H2M2S2));
            bean.setTo(itemBean.getTx_to());
            bean.setFrom(itemBean.getTx_from());
            bean.setValue(itemBean.getValue());
            bean.setSuccess(true);
            bean.setGas(itemBean.getGas());
            bean.setNonce(itemBean.getNonce());
            bean.setBlockNumber(String.valueOf(itemBean.getBlockNumber()));//可以作为是否交易成功的展示依据
//            switch (getType()) {
//                case TYPE_ALL:
//                    mListData.add(bean);
//                    break;
//                case TYPE_OUT:
//                    mListOut.add(bean);
//                    break;
//                case TYPE_IN:
//                    mListIn.add(bean);
//                    break;
//            }
            mListNetTemp.add(bean);
        }
        LoggerUtils.i("mListNetTemp", mListNetTemp.size());
        onAfterNetDataSuccess(mListNetTemp);
    }


    /**
     * @param error 网络数据传递错误时，传递本地数据
     */
    @Override
    public void onTxRecordBrowserFailure(String error) {
        LoggerUtils.e("onTxRecordBrowserFailure", error);
    }

    /**
     * 网络数据后
     *
     * @param listNet
     */
    protected abstract void onAfterNetDataSuccess(List<TxRecordBean> listNet);

    @Override
    public void onTxRecordLoadMoreSuccess(TxRecordBeanTemp.DataBean beans) {

    }

    @Override
    public void onLoadStart() {

    }

    @Override
    public void onLoadFinish() {

    }


    /**
     * @param walletBeanNew 1、先网络数据，
     *                      1.1、如果网络数据获取失败{@link #onTxRecordBrowserFailure(String)}，则直接显示本地数据
     *                      1.2、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
     */
    @Override
    public void onPullToDown(WalletBeanNew walletBeanNew) {
        LoggerUtils.i(TAG, "   isVisibleToUser = " + mVisibleToUser);
        if (mTxRecordPresenter == null) {
            mTxRecordPresenter = new TxRecordPresenter();
        }
//        if (mCommonAdapter == null) {
//            return;
//        }
//        LoggerUtils.i("刷新  " + walletBeanNew.getAddress());
//
//        mListDataTemp = IONCWalletSDK.getInstance().getAllTxRecordBeans();
//
//        if (mListDataTemp == null) {
//            LoggerUtils.i("local  mListDataTemp = null");
//            ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
//            return;
//        }
//        if (mListDataTemp.size() == 0) {
//            LoggerUtils.i("local  mListDataTemp.size = 0");
//            ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
//            return;
//        }
//
//        int sizeTempAll = mListDataTemp.size();
//        mListData.clear();
//        mListData.addAll(mListDataTemp);
//        Collections.sort(mListData);
//        mCommonAdapter.notifyDataSetChanged();
//
//        /*
//         * 数据筛选，如果已经交易但是未获取最新的交易记录的
//         * 获取一下最新的交易记录
//         * 交易未发起成功的，只能显示，不做其他处理
//         */
//        LoggerUtils.i("local  sizeTemp = " + sizeTempAll);
//        for (int i = 0; i < sizeTempAll; i++) {
//            String bn = mListDataTemp.get(i).getBlockNumber();
//            Log.i("bn", bn + "sizeTempAll = " + sizeTempAll);
//            if (DEFAULT_TRANSCATION_BLOCK_NUMBER_NULL.equals(bn)) {
//                LoggerUtils.i("local unpacked   " + mListDataTemp.get(i).getHash());
//                if (mTxHashUnpackedTemp.contains(mListDataTemp.get(i))) {
//                    break;
//                }
//                LoggerUtils.i("local add    " + mListDataTemp.get(i).getHash());
//
//                mTxHashUnpackedTemp.add(mListDataTemp.get(i));
//            }
//        }
//        int size = mTxHashUnpackedTemp.size();
//        LoggerUtils.i("local  mTxHashUnpackedTemp  size = " + size);
//        if (size == 0) {
//            LoggerUtils.i("local 无刷新");
//            return;
//        }
//        LoggerUtils.i("local 需更新 " + size);
//        for (int i = 0; i < size; i++) {
//            IONCWalletSDK.getInstance().ethTransaction(mNodeIONC
//                    , mTxHashUnpackedTemp.get(i).getHash()
//                    , mTxHashUnpackedTemp.get(i)
//                    , this);
//        }
    }

    @Override
    public void onPullToUp(WalletBeanNew walletBeanNew) {

    }

    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {

    }

    /**
     * 有新的交易发生
     * 1、先添加到全部的列表
     * 2、再添加到转出列表
     *
     * @param txRecordBean 有新的交易记录的时候
     */
    @Override
    public void onNewTxRecordByTx(TxRecordBean txRecordBean) {
        mCommonAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnTxRecordNodeSuccess(TxRecordBean txRecordBean) {

    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean recordBean) {

    }

    @Override
    public void onTxRecordItemClick(TxRecordBean txRecordBean) {
//        ToastUtil.showLong(txRecordBean.getValue());
        Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
        intent.putExtra(PARCELABLE_TX_RECORD, txRecordBean);
        startActivity(intent);
    }
}
