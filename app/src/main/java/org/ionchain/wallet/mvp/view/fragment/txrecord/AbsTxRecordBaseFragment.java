package org.ionchain.wallet.mvp.view.fragment.txrecord;

import android.view.View;
import android.widget.ListView;

import com.lzy.okgo.OkGo;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.DateUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordViewHelper;
import org.ionchain.wallet.bean.TxRecordBeanTemp;
import org.ionchain.wallet.mvp.callback.OnTxRecordNetDataCallback;
import org.ionchain.wallet.mvp.presenter.transcation.TxRecordPresenter;
import org.ionchain.wallet.mvp.view.base.AbsBaseViewPagerFragment;
import org.ionchain.wallet.mvp.view.fragment.AssetFragment;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Collections;

import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;

/**
 * 1、先网络数据，
 * 2、如果网络数据获取失败，则直接显示本地数据
 * 3、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
 */
public abstract class AbsTxRecordBaseFragment extends AbsBaseViewPagerFragment implements OnTxRecordNetDataCallback, AssetFragment.OnPullToRefreshCallback {

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
        mTxRecordViewHelper = new TxRecordViewHelper();
        mCommonAdapter = new CommonAdapter(mActivity, mListData, R.layout.item_txrecoder, mTxRecordViewHelper);
        tx_record_lv.setAdapter(mCommonAdapter);
    }

    @Override
    protected void loadData() {
        LoggerUtils.i("loadData  mWalletBeanNew.getAddress() = " + mWalletBeanNew.getAddress());
        getLocalData();
    }

    /**
     * @return 当前页的类型
     */
    protected abstract int getType();

    @Override
    protected void initData() {
        mTxRecordPresenter = new TxRecordPresenter();
        LoggerUtils.i("initData " + TAG);
        getLocalData();
//        getNetData();
    }

    protected void getLocalData() {

        String address = mWalletBeanNew.getAddress();

        /*
         * 获取本地数据集的缓存
         */

        mListDataTemp.clear();
        LoggerUtils.i("本地所有该钱包的数据" + TAG + "  地址：" + address);
        mListOut = IONCWalletSDK.getInstance().getAllTxRecordByTxOutAddress(address);
        mListIn = IONCWalletSDK.getInstance().getAllTxRecordBeansByTxInAddress(address);
        LoggerUtils.i("mListDataTemp.size0 = " + mListDataTemp.size());

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
                // 转出记录
                LoggerUtils.i("本地所有该钱包的数据 0" + TAG + "  地址：" + address + "mListOut.size = " + mListOut.size());
                LoggerUtils.i("本地所有该钱包的数据 0" + TAG + "  地址：" + address + "mListIn.size = " + mListIn.size());
                mListDataTemp.addAll(mListOut);
                // 转入记录
                mListDataTemp.addAll(mListIn);
                break;
            case TYPE_OUT:
                LoggerUtils.i("本地所有该钱包的数据 1" + TAG + "  地址：" + address + "mListOut.size = " + mListOut.size());
                LoggerUtils.i("本地所有该钱包的数据 1" + TAG + "  地址：" + address + "mListIn.size = " + mListIn.size());
                // 转出记录

                mListDataTemp.addAll(mListOut);
                break;
            case TYPE_IN:
                // 转入记录
                LoggerUtils.i("本地所有该钱包的数据 3" + TAG + "  地址：" + address + "mListOut.size = " + mListOut.size());
                LoggerUtils.i("本地所有该钱包的数据 3" + TAG + "  地址：" + address + "mListIn.size = " + mListIn.size());
                mListDataTemp.addAll(mListIn);
                break;
        }
        LoggerUtils.i("mListDataTemp.size = " + mListDataTemp.size());
        if (mListDataTemp.size() == 0 ) {
            ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
        }
        LoggerUtils.i("mListDataTemp.size = " + mListDataTemp.size());

//        for (int i = 0; i < size; i++) {
//            LoggerUtils.i("txRecordBean " + mListDataTemp.get(i).toString());
//            mListData.add(mListDataTemp.get(i));
////            if (DEFAULT_TRANSCATION_BLOCK_NUMBER.equals(mListDataTemp.get(i).getBlockNumber())) {
////                LoggerUtils.i("unpacked" + mListDataTemp.get(i).toString());
////                mTxHashUnpackedTemp.add(mListDataTemp.get(i));
////            }
//        }
        mListData.clear();
        mListData.addAll(mListDataTemp);
        Collections.sort(mListData);
        mCommonAdapter.notifyDataSetChanged();
    }


    void getNetData() {
        LoggerUtils.i("获取网络数据 " + TAG);
        if (mTxRecordPresenter == null) {
            LoggerUtils.i("获取网络数据 null" + TAG);
            return;
        }
        mTxRecordPresenter.getTxRecord(false, "3", mWalletBeanNew.getAddress(), "1", "10", this);
    }


    @Override
    public void onPause() {
        super.onPause();
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), this);
    }

    /**
     * 网络上，带地址的所有交易记录
     *
     * @param beans 浏览器接口中数据
     */
    @Override
    public void onTxRecordRefreshNetDataSuccess(TxRecordBeanTemp.DataBean beans) {
        LoggerUtils.i("网络请求成功");
        for (TxRecordBeanTemp.DataBean.ItemBean itemBean :
                beans.getData()) {
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
            mListData.add(bean);
        }
        onAfterNetDataSuccess();
    }


    /**
     * @param error 网络数据传递错误时，传递本地数据
     */
    @Override
    public void onTxRecordNetDataFailure(String error) {
    }

    /**
     * 网络数据后
     */
    protected abstract void onAfterNetDataSuccess();

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
     *                      1.1、如果网络数据获取失败{@link #onTxRecordNetDataFailure(String)}，则直接显示本地数据
     *                      1.2、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
     */
    @Override
    public void onPullToDown(WalletBeanNew walletBeanNew) {
//        getNetData();
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
//            if (DEFAULT_TRANSCATION_BLOCK_NUMBER.equals(bn)) {
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
    public void onNewRecord(TxRecordBean txRecordBean) {
        mListData.add(0,txRecordBean);
        mCommonAdapter.notifyDataSetChanged();
    }
}
