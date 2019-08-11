package org.ionchain.wallet.view.fragment.txrecord;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.sdk.wallet.bean.TxRecordBean;
import org.sdk.wallet.bean.WalletBeanNew;
import org.sdk.wallet.callback.OnTxRecordFromNodeCallback;
import org.sdk.wallet.sdk.IONCTransfers;
import org.sdk.wallet.sdk.IONCTxRecords;
import org.sdk.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordAdapter;
import org.ionchain.wallet.bean.TxRecordBeanTemp;
import org.ionchain.wallet.callback.OnTxRecordBrowserDataCallback;
import org.ionchain.wallet.presenter.transcation.TxRecordPresenter;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.activity.transaction.TxRecordDetailActivity;
import org.ionchain.wallet.view.base.AbsBaseFragment;
import org.ionchain.wallet.view.fragment.AssetFragment;
import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.List;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;
import static org.sdk.wallet.sdk.IONCTxRecords.txRecordAllByAddress;
import static org.sdk.wallet.sdk.IONCTxRecords.txRecordAllByBlockNumber;
import static org.sdk.wallet.sdk.IONCTxRecords.txRecordItemCountAllByAddress;
import static org.sdk.wallet.sdk.IONCTxRecords.updateTxRecordBean;
import static org.sdk.wallet.sdk.IONCWallet.TX_FAILURE;
import static org.sdk.wallet.sdk.IONCWallet.TX_SUSPENDED;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_TX_RECORD;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_WALLET_BEAN;
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;

/**
 * 1、先网络数据，
 * 2、如果网络数据获取失败，则直接显示本地数据
 * 3、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
 */
public abstract class AbsTxRecordBaseFragment extends AbsBaseFragment implements OnTxRecordBrowserDataCallback, AssetFragment.OnPullToRefreshCallback, OnTxRecordFromNodeCallback {
    private SmartRefreshLayout mRefresh;
    private String mNode = getHostNode();
    private String tag = "beannet";
    private RecyclerView mRecyclerView;
    private ImageView mNoTxRecordImg;


    private String mPageSizeAll = String.valueOf(5);
    private String mPageSizeFrom = String.valueOf(5);
    private String mPageSizeTo = String.valueOf(5);

    /**
     * 当前钱包
     */
    private static WalletBeanNew mWalletBeanNew;
    /**
     * 记录适配器
     */

    private TxRecordAdapter mTxRecordAdapter;
    /**
     * 钱包记录的实际数据集
     */
    List<TxRecordBean> mListAllData = new ArrayList<>();
    /**
     * 转出记录
     */
    protected List<TxRecordBean> mListDoneData = new ArrayList<>();
    /**
     * 转入记录
     */
    private List<TxRecordBean> mListDoingData = new ArrayList<>();
    private List<TxRecordBean> mListFailureData = new ArrayList<>();


    /**
     * 交易记录
     */
    private TxRecordPresenter mTxRecordPresenter;

    static final char TYPE_ALL = 0;
    static final char TYPE_DONE = 1;
    static final char TYPE_DOING = 2;
    static final char TYPE_FAILURE = 3;

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
        mRecyclerView = view.findViewById(R.id.tx_record_lv);
        mNoTxRecordImg = view.findViewById(R.id.no_tx_record);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        switch (getType()) {
            case TYPE_ALL:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_ALL, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListAllData);
                mTxRecordAdapter.setOnItemClickListener((adapter, view12, position) -> {
                    skipToDetail(mListAllData.get(position));
                });
                break;
            case TYPE_DONE:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_DONE, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListDoneData);
                mTxRecordAdapter.setOnItemClickListener((adapter, view13, position) -> {
                    skipToDetail(mListDoneData.get(position));

                });
                break;
            case TYPE_DOING:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_DOING, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListDoingData);
                mTxRecordAdapter.setOnItemClickListener((adapter, view14, position) -> {
                    skipToDetail(mListDoingData.get(position));
                });
                break;
            case TYPE_FAILURE:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_FAILURE, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListFailureData);
                mTxRecordAdapter.setOnItemClickListener((adapter, view14, position) -> {
                    skipToDetail(mListFailureData.get(position));
                });
                break;
        }
        mTxRecordAdapter.bindToRecyclerView(mRecyclerView);
        mTxRecordAdapter.openLoadAnimation(SCALEIN);
        mRecyclerView.setAdapter(mTxRecordAdapter);
    }

    private void skipToDetail(TxRecordBean txRecordBean) {
        Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
        intent.putExtra(PARCELABLE_TX_RECORD, txRecordBean);
        intent.putExtra(PARCELABLE_WALLET_BEAN, mWalletBeanNew);
        startActivity(intent);
    }


    @Override
    protected void visible() {
        LoggerUtils.i(tag, "TAG_NAME = " + TAG + " visible() = " + isVisible());
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
    }

    private void getLocalData() {
        localData();
    }


    @Override
    public void onPause() {
        super.onPause();
        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), this);
    }

    /**
     * @param txRecordBean 转账的辅助数据库字段更新
     */
    private void updateTxRecordPubliceKey(TxRecordBean txRecordBean) {
        txRecordBean.setPublicKey(mWalletBeanNew.getPublic_key());

        IONCTxRecords.saveTxRecordBean(txRecordBean);
    }


    @Override
    public void onLoadStart() {

    }

    @Override
    public void onLoadFinish() {

    }


    private void insertTxRecordBean(List<TxRecordBean> txRecordBeansNew, TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser) {
        if (IONCTxRecords.notExist(itemBeanBrowser.getHash())) {
            TxRecordBean bean = new TxRecordBean();
            bean.setHash(itemBeanBrowser.getHash());
            bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
            bean.setTo(itemBeanBrowser.getTx_to());
            bean.setFrom(itemBeanBrowser.getTx_from());
            bean.setValue(String.valueOf(Convert.fromWei(itemBeanBrowser.getValue(), Convert.Unit.ETHER)));
            bean.setBlockHash(itemBeanBrowser.getBlockHash());
            bean.setPublicKey(mWalletBeanNew.getPublic_key());
            bean.setGas(itemBeanBrowser.getGas());
            bean.setGasPrice(itemBeanBrowser.getGasPrice());
            bean.setNonce(itemBeanBrowser.getNonce());
//        bean.setV(Integer.valueOf(itemBeanBrowser.getV()));
            bean.setR(itemBeanBrowser.getR());
            bean.setS(itemBeanBrowser.getS());
            bean.setBlockNumber(String.valueOf(itemBeanBrowser.getBlockNumber()));//可以作为是否交易成功的展示依据
            txRecordBeansNew.add(0, bean);
        }
    }

    private void finishRefresh() {
        if (mRefresh != null) {
            mRefresh.finishRefresh();
            mRefresh.finishLoadMore();
        }
    }


    /**
     * @param walletBeanNew 1、先网络数据，
     *                      1.1、如果网络数据获取失败{@link #onTxRecordBrowserFailure(String)}，则直接显示本地数据
     *                      1.2、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
     * @param refresh
     */
    @Override
    public void onPullToDown(WalletBeanNew walletBeanNew, SmartRefreshLayout refresh) {
        mRefresh = refresh;
        mWalletBeanNew = walletBeanNew;
        LoggerUtils.i(TAG, "   isVisibleToUser = " + mVisibleToUser);
        if (mVisibleToUser) {
            if (mTxRecordPresenter == null) {
                mTxRecordPresenter = new TxRecordPresenter();
            }
            switch (getType()) {
                case TYPE_ALL:
                    mTxRecordPresenter.getTxRecordAll("3", mWalletBeanNew.getAddress(), "1", mPageSizeAll, this);
                    break;
                case TYPE_DONE:
                    mTxRecordPresenter.getTxRecordFrom("3", mWalletBeanNew.getAddress(), "1", mPageSizeFrom, this);
                    break;
                case TYPE_DOING:
//                    mTxRecordPresenter.getTxRecordTo("3", mWalletBeanNew.getAddress(), "1", mPageSizeTo, this);
                    //更新状态
                    int count = mListDoingData.size();
                    for (int i = 0; i < count; i++) {
                        TxRecordBean t = mListDoingData.get(i);
                        IONCTransfers.ethTransaction(mNode
                                , t.getHash()
                                , t
                                , this);
                    }
                    break;
            }
        }
    }

    protected void pullToDownSuccess(TxRecordBean txRecordBean) {
        LoggerUtils.i("pullToDownSuccess", txRecordBean.toString());
    }

    /**
     * 下来加载更多
     *
     * @param walletBeanNew
     * @param refresh
     */
    @Override
    public void onPullToUp(WalletBeanNew walletBeanNew, SmartRefreshLayout refresh) {

        LoggerUtils.i("method", "onPullToUp" + "   AbsTxRecordBaseFragment");
        if (!mVisibleToUser) {
            return;
        }
        mRefresh = refresh;
        localData();
    }

    private void localData() {
        /*
         * 先获数据库中取当前钱包相关的记录的总数，即转出和转入
         */
        long offset;
        int currentCount;
        /**
         * 每次上拉加载更过的时候，新增的itemd的数量
         */
        long perPageAddNum = 5;
        switch (getType()) {
            case TYPE_ALL:

                /*
                 * 通过地址从数据库查找钱包的钱包的转入和转出的记录
                 */
                long txRecordAllCount = txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());
                /*
                 * 展示中的数量
                 */
                currentCount = mListAllData.size();
                if (currentCount == txRecordAllCount) {
                    getNetData();
                    return;
                }
                offset = txRecordAllCount - currentCount - perPageAddNum;
                /*
                 * 取出 5个数据
                 */
                List<TxRecordBean> listAllDataTemp = txRecordAllByAddress(mWalletBeanNew.getAddress(), offset, perPageAddNum);
                mListAllData.addAll(listAllDataTemp);
//                for (TxRecordBean t :
//                        mListAllDataTemp) {
//                    if (!mListAllData.contains(t)) {
//                        mListAllData.add(t);
//                    }
//                }
                if (mListAllData.size() == 0) {
                    showNoTxRecord();
                } else {
                    hideNoTxRecord();
                }
                break;
            case TYPE_DONE:
                long txDoneCount = txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListDoneData.size();
                if (currentCount == txDoneCount) {
                    getNetData();
                    return;
                }
                offset = txDoneCount - currentCount - perPageAddNum;

                LoggerUtils.i("method", "onPullToUp" + "   txDoneCount " + txDoneCount);
                List<TxRecordBean> listDoneTemp = txRecordAllByAddress(mWalletBeanNew.getAddress(), offset, perPageAddNum);
                for (TxRecordBean t :
                        listDoneTemp) {
                    //不存在，并且不是挂在交易和失败交易
                    if (!mListDoneData.contains(t) && !TX_SUSPENDED.equals(t.getBlockNumber()) && !TX_FAILURE.equals(t.getBlockNumber())) {
                        LoggerUtils.i("doing", t.toString());
                        mListDoneData.add(t);
                    }
                }
                if (mListDoneData.size() == 0) {
                    showNoTxRecord();
                } else {
                    hideNoTxRecord();
                }
                break;
            case TYPE_DOING:
                long txRecordInCount = txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListDoingData.size();
                if (currentCount == txRecordInCount) {
                    getNetData();
                    return;
                }
                offset = txRecordInCount - currentCount - perPageAddNum;
                List<TxRecordBean> listDoingDataTemp = txRecordAllByBlockNumber(TX_SUSPENDED, mWalletBeanNew.getAddress(), offset, perPageAddNum);
                LoggerUtils.i("mmmmm", listDoingDataTemp.size());
                for (TxRecordBean t :
                        listDoingDataTemp) {
                    if (!mListDoingData.contains(t)) {
                        mListDoingData.add(t);
                    }
                }
                if (mListDoingData.size() == 0) {
                    showNoTxRecord();
                } else {
                    hideNoTxRecord();
                }
                break;
            case TYPE_FAILURE:
                long txRecordFailureCount = txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListFailureData.size();
                if (currentCount == txRecordFailureCount) {
                    getNetData();
                    return;
                }
                offset = txRecordFailureCount - currentCount - perPageAddNum;

                LoggerUtils.i("method", "onPullToUp" + "   txRecordFailureCount " + txRecordFailureCount);
                LoggerUtils.i("method", "onPullToUp" + "   currentCount " + currentCount);
                LoggerUtils.i("method", "onPullToUp" + "   offset " + offset);
                List<TxRecordBean> listFailureTemp = txRecordAllByBlockNumber(TX_FAILURE, mWalletBeanNew.getAddress(), offset, perPageAddNum);
                LoggerUtils.i("mmmmm", listFailureTemp.size());
                for (TxRecordBean t :
                        listFailureTemp) {
                    if (!mListFailureData.contains(t)) {
                        mListFailureData.add(t);
                    }
                }
                if (mListFailureData.size() == 0) {
                    showNoTxRecord();
                } else {
                    hideNoTxRecord();
                }
                break;
        }

        mTxRecordAdapter.notifyDataSetChanged();
    }

    private void getNetData() {
        if (mVisibleToUser) {
            if (mTxRecordPresenter == null) {
                mTxRecordPresenter = new TxRecordPresenter();
            }
            int size;
            int pageNum;

            switch (getType()) {
                case TYPE_ALL:
                    size = mListAllData.size();
                    pageNum = size / 5;
                    pageNum++;
                    mTxRecordPresenter.getTxRecordAll("3", mWalletBeanNew.getAddress(), String.valueOf(pageNum), mPageSizeAll, this);
                    break;
                case TYPE_DONE:
                    size = mListDoneData.size();
                    pageNum = size / 5;
                    pageNum++;
                    mTxRecordPresenter.getTxRecordFrom("3", mWalletBeanNew.getAddress(), String.valueOf(pageNum), mPageSizeFrom, this);
                    break;
                case TYPE_DOING:
                    size = mListDoingData.size();
                    pageNum = size / 5;
                    pageNum++;
                    mTxRecordPresenter.getTxRecordTo("3", mWalletBeanNew.getAddress(), String.valueOf(pageNum), mPageSizeTo, this);
                    break;
            }
        }
    }

    /**
     * 网络上，带地址的所有交易记录
     * {@link org.ionchain.wallet.model.txrecoder.TxRecordModel#getTxRecord(String, String, String, String, String, OnTxRecordBrowserDataCallback)}  }
     *
     * @param browserData 浏览器接口中数据
     */
    @Override
    public void onTxRecordBrowserSuccess(TxRecordBeanTemp.DataBean browserData) {
        finishRefresh();
        LoggerUtils.i("method", "onTxRecordBrowserSuccess" + "   " + browserData.toString());
        List<TxRecordBean> txRecordBeansNew = new ArrayList<>();
        List<TxRecordBeanTemp.DataBean.ItemBean> data = browserData.getData();
        LoggerUtils.i(tag, "网络请求成功 size = " + data.size());
        int oldSize = 0;
        //数据转换
        switch (getType()) {
            case TYPE_ALL:
                oldSize = mListAllData.size();
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    insertTxRecordBean(txRecordBeansNew, itemBeanBrowser);
                }
                break;
            case TYPE_DONE:
                oldSize = mListDoneData.size();
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    insertTxRecordBean(txRecordBeansNew, itemBeanBrowser);
                }
                break;
            case TYPE_DOING:
                oldSize = mListDoingData.size();
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    insertTxRecordBean(txRecordBeansNew, itemBeanBrowser);
                }
                break;
            case TYPE_FAILURE:
                oldSize = mListFailureData.size();
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    insertTxRecordBean(txRecordBeansNew, itemBeanBrowser);
                }
                break;
        }

        if (txRecordBeansNew.size() == 0) {
            //没有新的交易记录进来
            if (oldSize == 0) {
                showNoTxRecord();
            } else {
                hideNoTxRecord();
            }
            ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_no_new_data));
            return;
        }

        //数据比对
        int size = 0;

        switch (getType()) {
            case TYPE_ALL:
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    updateTxRecordPubliceKey(b);
                    mListAllData.add(0, b);
                }
                size = mListAllData.size();
                break;
            case TYPE_DONE:
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    updateTxRecordPubliceKey(b);
                    mListDoneData.add(0, b);
                }
                size = mListDoneData.size();
                break;
            case TYPE_DOING:
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    updateTxRecordPubliceKey(b);
                    mListDoingData.add(0, b);
                }
                size = mListDoingData.size();
                break;
            case TYPE_FAILURE:
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    updateTxRecordPubliceKey(b);
                    mListFailureData.add(0, b);
                }
                size = mListFailureData.size();
                break;
        }
        ToastUtil.showToastLonger(getAppString(R.string.new_tx_record) + " " + (size - oldSize));
        mTxRecordAdapter.notifyDataSetChanged();
    }

    private void hideNoTxRecord() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoTxRecordImg.setVisibility(View.GONE);
    }

    /**
     * 显示无交易记录图片
     */
    private void showNoTxRecord() {
        mRecyclerView.setVisibility(View.GONE);
        mNoTxRecordImg.setVisibility(View.VISIBLE);
    }

    /**
     * @param error 网络数据传递错误时，传递本地数据
     */
    @Override
    public void onTxRecordBrowserFailure(String error) {
        finishRefresh();
        ToastUtil.showToastLonger(error);
        if ((mListAllData != null && mListAllData.size() == 0)
                || (mListDoneData != null && mListDoneData.size() == 0)
                || (mListDoingData != null && mListDoingData.size() == 0)
                || (mListFailureData != null && mListFailureData.size() == 0)) {
            showNoTxRecord();
        }
    }

    @Override
    public void onTxRecordSuccessDataNUll() {
        finishRefresh();
        if ((mListAllData != null && mListAllData.size() == 0)
                || (mListDoneData != null && mListDoneData.size() == 0)
                || (mListDoingData != null && mListDoingData.size() == 0)
                || (mListFailureData != null && mListFailureData.size() == 0)) {
            showNoTxRecord();
        }
        ToastUtil.showToastLonger(getAppString(R.string.no_more_rexord));
    }

    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {

        LoggerUtils.i("method", "onAddressChanged");
        switch (getType()) {
            case TYPE_ALL:
                mWalletBeanNew = currentWallet;
                mListDoingData.clear();
                mListDoneData.clear();
                mListFailureData.clear();
                mListAllData.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                getLocalData();// 全部需要显示，其他的左右切换才显示
                break;
            case TYPE_DONE:
                mListDoneData.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                break;
            case TYPE_DOING:
                mListDoingData.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                break;
            case TYPE_FAILURE:
                mListFailureData.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                break;
        }
        mTxRecordAdapter.setCurrentWalletBean(mWalletBeanNew);
        mTxRecordAdapter.notifyDataSetChanged();
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

    @SuppressLint("SetTextI18n")
    @Override
    public void OnTxRecordNodeSuccess(TxRecordBean txRecordBean) {

        LoggerUtils.i("ethTransaction", "OnTxRecordNodeSuccess" + "   " + txRecordBean.toString());
        if (TX_SUSPENDED.equals(txRecordBean.getBlockNumber())) {
            ToastUtil.showToastLonger(getAppString(R.string.tx_block_suspended));
            return;
        }

        LoggerUtils.i("ethTransaction", "OnTxRecordNodeSuccess4" + "   " + txRecordBean.toString());
        mListDoingData.remove(txRecordBean);
        mTxRecordAdapter.notifyDataSetChanged();
        updateTxRecordBean(txRecordBean);
        pullToDownSuccess(txRecordBean);
    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean txRecordBean) {
        LoggerUtils.e("ethTransaction", error);
        updateTxRecordBean(txRecordBean);
    }

    @Override
    public void onTxRecordNodeStart() {

    }
}
