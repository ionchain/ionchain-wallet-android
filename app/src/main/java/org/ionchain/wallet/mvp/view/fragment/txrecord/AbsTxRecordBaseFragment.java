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
import org.ionchain.wallet.mvp.view.base.AbsBaseFragment;
import org.ionchain.wallet.mvp.view.fragment.AssetFragment;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;

/**
 * 1、先网络数据，
 * 2、如果网络数据获取失败，则直接显示本地数据
 * 3、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
 */
public abstract class AbsTxRecordBaseFragment extends AbsBaseFragment implements OnTxRecordNetDataCallback, AssetFragment.OnPullToRefreshCallback {
    protected final String TAG = this.getClass().getSimpleName();
    /**
     * 当前钱包
     */
    protected static WalletBeanNew mWalletBeanNew;
    /**
     * 记录适配器
     */
    protected CommonAdapter adapterLv;
    /**
     * 钱包记录的实际数据集
     */
    protected List<TxRecordBean> mListData = new ArrayList<>();
    /**
     * 转出记录
     */
    protected List<TxRecordBean> mListOut = new ArrayList<>();
    /**
     * 转入记录
     */
    protected List<TxRecordBean> mListIn = new ArrayList<>();

    /**
     * 所有本地记录的缓存
     */
    protected List<TxRecordBean> mListDataTemp = new ArrayList<>();
    /**
     * 未获取交易信息的本地缓存,缓存hash值
     */
    protected List<TxRecordBean> mTxHashUnpackedTemp = new ArrayList<>();
    /**
     * 例子链接点
     */
    protected String mNodeIONC = getHostNode();

    /**
     * listvie辅助
     */
    protected TxRecordViewHelper mTxRecordViewHelper;

    /**
     * 交易记录
     */
    private TxRecordPresenter mTxRecordPresenter;

    protected final char TYPE_ALL = 0;
    protected final char TYPE_OUT = 1;
    protected final char TYPE_IN = 2;


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
        adapterLv = new CommonAdapter(mActivity, mListData, R.layout.item_txrecoder, mTxRecordViewHelper);
        tx_record_lv.setAdapter(adapterLv);
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

    private void getLocalData() {

        String address = mWalletBeanNew.getAddress();

        /*
         * 获取本地数据集的缓存
         */

        mListDataTemp.clear();
        LoggerUtils.i("网络请求失败，后去本地所有该钱包的数据" + TAG + "  地址：" + address);

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
                mListDataTemp.addAll(IONCWalletSDK.getInstance().getAllTxRecordByTxOutAddress(address));
                // 转入记录
                mListDataTemp.addAll(0, IONCWalletSDK.getInstance().getAllTxRecordBeansByTxInAddress(address));
                break;
            case TYPE_OUT:
                mListDataTemp = IONCWalletSDK.getInstance().getAllTxRecordByTxOutAddress(address);
                break;
            case TYPE_IN:
                mListDataTemp = IONCWalletSDK.getInstance().getAllTxRecordBeansByTxInAddress(address);
                break;
        }

        if (mListDataTemp == null || mListDataTemp.size() == 0) {
            ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
            getNetData();
            return;
        }
        int size = mListDataTemp.size();
        LoggerUtils.i("网络请求失败 local  size = " + size);
        Collections.sort(mListDataTemp);
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
        adapterLv.notifyDataSetChanged();
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
    protected void handleShow() {
        if (mTxRecordPresenter == null) {
            mTxRecordPresenter = new TxRecordPresenter();
        }
        LoggerUtils.i("显示 : " + TAG);
    }

    @Override
    protected void handleHidden() {
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), this);
        LoggerUtils.i("隐藏 : " + TAG);
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
     * 数据集指向新的钱包记录
     * @param currentWallet 新钱包
     */
    public void onAddressChanged(WalletBeanNew currentWallet) {
        LoggerUtils.i("地址切换");
        mWalletBeanNew = currentWallet;
        mListData.clear();
        adapterLv.notifyDataSetChanged();
//        getNetData();
    }
    /**
     * @param walletBeanNew 1、先网络数据，
     *                      1.1、如果网络数据获取失败{@link #onTxRecordNetDataFailure(String)}，则直接显示本地数据
     *                      1.2、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
     */
    @Override
    public void onPullToDown(WalletBeanNew walletBeanNew) {
//        getNetData();
//        if (adapterLv == null) {
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
//        adapterLv.notifyDataSetChanged();
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

}
