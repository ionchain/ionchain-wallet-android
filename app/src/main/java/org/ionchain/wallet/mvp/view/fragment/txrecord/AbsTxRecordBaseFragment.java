package org.ionchain.wallet.mvp.view.fragment.txrecord;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.okgo.OkGo;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordAdapter;
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

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_TX_RECORD;

/**
 * 1、先网络数据，
 * 2、如果网络数据获取失败，则直接显示本地数据
 * 3、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
 */
public abstract class AbsTxRecordBaseFragment extends AbsBaseViewPagerFragment implements OnTxRecordBrowserDataCallback, AssetFragment.OnPullToRefreshCallback, OnTxRecordFromNodeCallback {

    private List<TxRecordBean> mListOutTemp = new ArrayList<>();
    private List<TxRecordBean> mListInTemp = new ArrayList<>();

    private String tag = "binny";
    private int mOffset = 0;
    private int mPageNum = 1;
    private RecyclerView mListView;

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
        mListView = view.findViewById(R.id.tx_record_lv);
        mListView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        switch (getType()) {
            case TYPE_ALL:
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListOutAndInData);
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListOutAndInData.get(position));
                    startActivity(intent);
                });
                break;
            case TYPE_OUT:
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListOut);
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListOut.get(position));
                    startActivity(intent);
                });
                break;
            case TYPE_IN:
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListIn);
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListIn.get(position));
                    startActivity(intent);
                });
                break;
        }
        mTxRecordAdapter.openLoadAnimation(SCALEIN);
        mListView.setAdapter(mTxRecordAdapter);
    }

    @Override
    protected void visible() {
        LoggerUtils.i(tag, "TAG_NAME = " + TAG_NAME + " visible() = " + isVisible());
        getLocalData();//可见 visible ,左右切换
    }

    /**
     * @return 当前页的类型
     */
    protected abstract int getType();

    @Override
    protected void initData() {

        LoggerUtils.i(tag, "initData ");
        getLocalData(); //初始化
//        getNetData();
    }

    protected void getLocalData() {
        /*
         * 获取本地数据集的缓存
         */
        LoggerUtils.i(tag, "访问本地数据库.........." + " mListOutTemp.s  = " + mListOutTemp.size() + " mListInTemp.s  = " + mListInTemp.size()); // TODO: 2019-06-21 可优化
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
                mListOutTemp.clear();
                mListInTemp.clear();
                mListOutTemp = IONCWalletSDK.getInstance().getTxRecordBeanOutByPage(mOffset, mWalletBeanNew.getAddress(), 5, 5);
                mListInTemp = IONCWalletSDK.getInstance().getTxRecordBeanInByPage(mOffset, mWalletBeanNew.getAddress(), 5, 5);
                if (mListOutTemp.size() == 0 && mListInTemp.size() == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
                    return;
                }
                mListOutAndInData.clear();
                mListOutAndInData.addAll(mListOutTemp);
                mListOutAndInData.addAll(mListInTemp);
                LoggerUtils.i(tag, "mListDataTemp.size = " + mListDataTemp.size());
                Collections.sort(mListOutAndInData);
                break;
            case TYPE_OUT:
                mListOutTemp.clear();
                mListOutTemp = IONCWalletSDK.getInstance().getTxRecordBeanOutByPage(mOffset, mWalletBeanNew.getAddress(), 5, 5);
                if (mListOutTemp.size() == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
                    return;
                }
                mListOut.clear();
                mListOut.addAll(mListOutTemp);
                LoggerUtils.i(tag, "mListDataOutTemp.size = " + mListDataTemp.size());
                Collections.sort(mListOut);
                break;
            case TYPE_IN:
                mListInTemp.clear();
                mListInTemp = IONCWalletSDK.getInstance().getTxRecordBeanInByPage(mOffset, mWalletBeanNew.getAddress(), 5, 5);
                if (mListInTemp.size() == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
                    return;
                }
                mListIn.clear();
                mListIn.addAll(mListInTemp);
                LoggerUtils.i(tag, "mListDataInTemp.size = " + mListDataTemp.size());
                Collections.sort(mListIn);
                break;
        }
        mTxRecordAdapter.notifyDataSetChanged();
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
        mListNetTemp.clear();
        boolean exist = false;
        List<TxRecordBean> txRecordBeans = new ArrayList<>();
        switch (getType()) {
            case TYPE_ALL:

                mListNetTemp.addAll(mListOutAndInData);

                for (TxRecordBeanTemp.DataBean.ItemBean itemBean :
                        beans.getData()) {
                    LoggerUtils.i(tag, "网络请求成功" + beans.toString());
                    for (TxRecordBean t :
                            mListNetTemp) {
                        if (itemBean.getHash().equals(t.getHash())) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        TxRecordBean bean = new TxRecordBean();
                        bean.setHash(itemBean.getHash());
                        bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
                        bean.setTo(itemBean.getTx_to());
                        bean.setFrom(itemBean.getTx_from());
                        bean.setValue(itemBean.getValue());
                        bean.setSuccess(true);
                        bean.setGas(itemBean.getGas());
                        bean.setNonce(itemBean.getNonce());
                        bean.setBlockNumber(String.valueOf(itemBean.getBlockNumber()));//可以作为是否交易成功的展示依据
                        txRecordBeans.add(bean);
                    } else {
                        break;
                    }
                }
                LoggerUtils.i(tag, "all size  = " + txRecordBeans.size());
                break;
            case TYPE_OUT:
                mListNetTemp.addAll(mListOut);
                for (TxRecordBeanTemp.DataBean.ItemBean itemBean :
                        beans.getData()) {
                    LoggerUtils.i(tag, "网络请求成功" + beans.toString());
                    if (!itemBean.getTx_from().equals(mWalletBeanNew.getAddress())) {
                        continue;
                    }
                    for (TxRecordBean t :
                            mListNetTemp) {
                        if (itemBean.getHash().equals(t.getHash())) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        TxRecordBean bean = new TxRecordBean();
                        bean.setHash(itemBean.getHash());
                        bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
                        bean.setTo(itemBean.getTx_to());
                        bean.setFrom(itemBean.getTx_from());
                        bean.setValue(itemBean.getValue());
                        bean.setSuccess(true);
                        bean.setGas(itemBean.getGas());
                        bean.setNonce(itemBean.getNonce());
                        bean.setBlockNumber(String.valueOf(itemBean.getBlockNumber()));//可以作为是否交易成功的展示依据
                        txRecordBeans.add(bean);
                    } else {
                        break;
                    }
                    LoggerUtils.i(tag, "out size  = " + txRecordBeans.size());
                }
                break;
            case TYPE_IN:
                mListNetTemp.addAll(mListIn);
                for (TxRecordBeanTemp.DataBean.ItemBean itemBean :
                        beans.getData()) {
                    if (!itemBean.getTx_to().equals(mWalletBeanNew.getAddress())) {
                        LoggerUtils.i(tag, "to = " + itemBean.getTx_to() + ", address = " + mWalletBeanNew.getAddress());
                        continue;
                    }
                    LoggerUtils.i(tag, "网络请求成功" + beans.toString());
                    for (TxRecordBean t :
                            mListNetTemp) {
                        if (itemBean.getHash().equals(t.getHash())) {
                            exist = true;
                            LoggerUtils.i(tag, "存在 哈希 = " + t.getHash());
                            break;
                        }
                    }
                    if (!exist) {
                        LoggerUtils.i(tag, "不存在 哈希 = ");
                        TxRecordBean bean = new TxRecordBean();
                        bean.setHash(itemBean.getHash());
                        bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
                        bean.setTo(itemBean.getTx_to());
                        bean.setFrom(itemBean.getTx_from());
                        bean.setValue(itemBean.getValue());
                        bean.setSuccess(true);
                        bean.setGas(itemBean.getGas());
                        bean.setNonce(itemBean.getNonce());
                        bean.setBlockNumber(String.valueOf(itemBean.getBlockNumber()));//可以作为是否交易成功的展示依据
                        txRecordBeans.add(bean);
                    } else {
                        break;
                    }
                }
                LoggerUtils.i(tag, "in size  = " + txRecordBeans.size());
                break;

        }


//        onAfterNetDataSuccess(txRecordBeans);
        if (txRecordBeans.size() == 0) {
            LoggerUtils.i(tag, "size = 0,无新数据");
            return;
        }
        int size = 0;
        switch (getType()) {
            case TYPE_ALL:
                size = mListOutAndInData.size();
                LoggerUtils.i(tag, "size = " + size);
                for (TxRecordBean b :
                        txRecordBeans) {
                    mListOutAndInData.add(0, b);
                    IONCWalletSDK.getInstance().saveTxRecordBean(b);
                }
                break;
            case TYPE_OUT:
                size = mListOut.size();

                for (TxRecordBean b :
                        txRecordBeans) {
                    mListOut.add(0, b);
                    IONCWalletSDK.getInstance().saveTxRecordBean(b);
                    LoggerUtils.i(tag, "save-out-size = " + size);
                }
                break;
            case TYPE_IN:
                size = mListIn.size();
                LoggerUtils.i(tag, "size = " + size);
                for (TxRecordBean b :
                        txRecordBeans) {
                    mListIn.add(0, b);
                    IONCWalletSDK.getInstance().saveTxRecordBean(b);
                }
                break;
        }
        mTxRecordAdapter.notifyDataSetChanged();
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
        if (mVisibleToUser) {
            if (mTxRecordPresenter == null) {
                mTxRecordPresenter = new TxRecordPresenter();
            }
            mPageNum++;
            switch (getType()) {
                case TYPE_ALL:
                    LoggerUtils.i("beannet", "all");
                    mTxRecordPresenter.getTxRecordAll("3", mWalletBeanNew.getAddress(), String.valueOf(mPageNum), "10", this);
                    break;
                case TYPE_OUT:
                    LoggerUtils.i("beannet", "out");
                    mTxRecordPresenter.getTxRecordFrom("3", mWalletBeanNew.getAddress(), String.valueOf(mPageNum), "5", this);
                    break;
                case TYPE_IN:
                    LoggerUtils.i("beannet", "in");
                    mTxRecordPresenter.getTxRecordTo("3", mWalletBeanNew.getAddress(), String.valueOf(mPageNum), "5", this);
                    break;
            }
        }
    }

    @Override
    public void onPullToUp(WalletBeanNew walletBeanNew) {
        if (!mVisibleToUser) {
            return;
        }
        LoggerUtils.i("up");
        /*
         * 先得到当期页的条目数
         *
         *
         */

        mOffset++;
        mListOutTemp.clear();
        mListInTemp.clear();
        mListOutTemp = IONCWalletSDK.getInstance().getTxRecordBeanOutByPage(mOffset, mWalletBeanNew.getAddress(), 5, 5);
        mListInTemp = IONCWalletSDK.getInstance().getTxRecordBeanInByPage(mOffset, mWalletBeanNew.getAddress(), 5, 5);
        mListOutAndInData.addAll(mListOutTemp);
        LoggerUtils.i("temp = ", mListInTemp.size());
        LoggerUtils.i("temp = ", mListOutTemp.size());
        LoggerUtils.i("temp = ", mOffset);
        mListOutAndInData.addAll(mListInTemp);
        mTxRecordAdapter.notifyDataSetChanged();

    }

    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {
        LoggerUtils.i("method","onAddressChanged");
        mOffset = 0;
        mPageNum = 1;
        switch (getType()) {
            case TYPE_ALL:
                mWalletBeanNew = currentWallet;
                mListIn.clear();
                mListOut.clear();
                mListOutAndInData.clear();
                if (mTxRecordAdapter == null) {
                    LoggerUtils.i("地址切换 = mCommonAdapter999 =null");
                    return;
                }
                mTxRecordAdapter.notifyDataSetChanged();
                getLocalData();// 全部需要显示
                break;
            case TYPE_OUT:
                LoggerUtils.i("地址切换，清空缓存 " + TAG + "mCommonAdapter = " + mTxRecordAdapter);
                mListOut.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                mTxRecordAdapter.notifyDataSetChanged();
                break;
            case TYPE_IN:
                mListIn.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                mTxRecordAdapter.notifyDataSetChanged();
                break;
        }
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
        mTxRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnTxRecordNodeSuccess(TxRecordBean txRecordBean) {

    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean recordBean) {

    }

}
