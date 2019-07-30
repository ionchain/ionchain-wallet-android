package org.ionchain.wallet.view.fragment.txrecord;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnTxRecordFromNodeCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordAdapter;
import org.ionchain.wallet.bean.TxRecordBeanTemp;
import org.ionchain.wallet.callback.OnTxRecordBrowserDataCallback;
import org.ionchain.wallet.presenter.transcation.TxRecordPresenter;
import org.ionchain.wallet.view.activity.transaction.TxRecordDetailActivity;
import org.ionchain.wallet.view.base.AbsBaseViewPagerFragment;
import org.ionchain.wallet.view.fragment.AssetFragment;
import org.ionchain.wallet.utils.ToastUtil;
import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.List;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;
import static org.ionc.wallet.sdk.IONCWalletSDK.TX_FAILURE;
import static org.ionc.wallet.sdk.IONCWalletSDK.TX_SUSPENDED;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_TX_RECORD;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_WALLET_BEAN;
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;

/**
 * 1、先网络数据，
 * 2、如果网络数据获取失败，则直接显示本地数据
 * 3、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
 */
public abstract class AbsTxRecordBaseFragment extends AbsBaseViewPagerFragment implements OnTxRecordBrowserDataCallback, AssetFragment.OnPullToRefreshCallback, OnTxRecordFromNodeCallback {
    private SmartRefreshLayout mRefresh;
    private List<TxRecordBean> mListDoneTemp = new ArrayList<>();
    private List<TxRecordBean> mListInTemp = new ArrayList<>();
    private List<TxRecordBean> mListFailureTemp = new ArrayList<>();
    private String mNode = getHostNode();
    private String tag = "beannet";
    private RecyclerView mListView;
    /**
     * 每次上拉加载更过的时候，新增的itemd的数量
     */
    private long mPerPageAddNum = 5;
    private ObjectAnimator objectAnimator;

    /**
     * 缓存当期那被点击的item的位置
     */
    private int mItemClickedPos;
    private String split = " ： ";
    private View mSyncImageView;
    private String mPageSizeAll = String.valueOf(5);
    private String mPageSizeFrom = String.valueOf(5);
    private String mPageSizeTo = String.valueOf(5);

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
        mListView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        switch (getType()) {
            case TYPE_ALL:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_ALL, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListAllData);
                mTxRecordAdapter.setOnItemClickListener((adapter, view12, position) -> {
                    skipToDetail(mListAllData.get(position));
                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view15, position) -> {
                    syncBrowser(view15, position);
                });
                break;
            case TYPE_DONE:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_DONE, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListDone);
                mTxRecordAdapter.setOnItemClickListener((adapter, view13, position) -> {
                    skipToDetail(mListDone.get(position));

                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view15, position) -> {
                    syncBrowser(view15, position);
                });
                break;
            case TYPE_DOING:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_DOING, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListIn);
                mTxRecordAdapter.setOnItemClickListener((adapter, view14, position) -> {
                    skipToDetail(mListIn.get(position));
                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view15, position) -> {
                    syncBrowser(view15, position);
                });
                break;
            case TYPE_FAILURE:
                mTxRecordAdapter = new TxRecordAdapter(TYPE_FAILURE, mWalletBeanNew, mActivity, R.layout.item_txrecord, mListFailure);
                mTxRecordAdapter.setOnItemClickListener((adapter, view14, position) -> {
                    skipToDetail(mListFailure.get(position));
                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view15, position) -> {
                    syncBrowser(view15, position);
                });
                break;
        }
        mTxRecordAdapter.bindToRecyclerView(mListView);
        mTxRecordAdapter.openLoadAnimation(SCALEIN);
        mListView.setAdapter(mTxRecordAdapter);
    }

    private void skipToDetail(TxRecordBean txRecordBean) {
        Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
        intent.putExtra(PARCELABLE_TX_RECORD, txRecordBean);
        intent.putExtra(PARCELABLE_WALLET_BEAN, mWalletBeanNew);
        startActivity(intent);
    }

    private void syncBrowser(View view15, int position) {
        mSyncImageView = view15;
        mSyncImageView.setClickable(false);
        LoggerUtils.i("binny", "initView" + "   AbsTxRecordBaseFragment" + position);
        objectAnimator = ObjectAnimator.ofFloat(view15, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(2000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.start();//动画开始
        mItemClickedPos = position;
        TxRecordBean txRecordBean = mListAllData.get(position);
        LoggerUtils.i("syncBrowser", "txRecordBean = " + txRecordBean.toString());
        switch (getType()) {
            case TYPE_ALL:
                IONCWalletSDK.getInstance().
                        ethTransaction(mNode,
                                txRecordBean.getHash(),
                                txRecordBean,
                                AbsTxRecordBaseFragment.this);
                break;
            case TYPE_DONE:
                IONCWalletSDK.getInstance().
                        ethTransaction(mNode,
                                txRecordBean.getHash(),
                                txRecordBean,
                                AbsTxRecordBaseFragment.this);
                break;
            case TYPE_DOING:
                IONCWalletSDK.getInstance().
                        ethTransaction(mNode,
                                txRecordBean.getHash(),
                                txRecordBean,
                                AbsTxRecordBaseFragment.this);
                break;
        }
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
    private void txRecordHelper(TxRecordBean txRecordBean) {
        txRecordBean.setPublicKey(mWalletBeanNew.getPublic_key());

        IONCWalletSDK.getInstance().saveTxRecordBean(txRecordBean);
    }


    @Override
    public void onLoadStart() {

    }

    @Override
    public void onLoadFinish() {

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
        mListNetTemp.clear();
        LoggerUtils.i("method", "onTxRecordBrowserSuccess" + "   " + browserData.toString());
        List<TxRecordBean> txRecordBeansNew = new ArrayList<>();
        List<TxRecordBeanTemp.DataBean.ItemBean> data = browserData.getData();
        int oldSize = 0;
        //数据转换
        switch (getType()) {
            case TYPE_ALL:
                oldSize = mListAllData.size();
                mListNetTemp.addAll(mListAllData);
                LoggerUtils.i(tag, "网络请求成功 size = " + data.size());
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    LoggerUtils.i("oldhash", "new 哈希" + "   " + itemBeanBrowser.getHash());
                    if (IONCWalletSDK.getInstance().notExist(itemBeanBrowser.getHash())) {
                        LoggerUtils.i("newhash", "新增哈希" + "   " + itemBeanBrowser.getHash());
                        TxRecordBean bean = new TxRecordBean();
                        bean.setHash(itemBeanBrowser.getHash());
                        bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
                        bean.setTo(itemBeanBrowser.getTx_to());
                        bean.setFrom(itemBeanBrowser.getTx_from());
                        LoggerUtils.i("valueeee", itemBeanBrowser.getValue());
                        bean.setValue(String.valueOf(itemBeanBrowser.getEtherValue()));
                        bean.setBlockHash(itemBeanBrowser.getBlockHash());
                        bean.setPublicKey(mWalletBeanNew.getPublic_key());
                        bean.setGas(itemBeanBrowser.getGas());
                        bean.setGasPrice(itemBeanBrowser.getGasPrice());
                        bean.setNonce(itemBeanBrowser.getNonce());
                        bean.setBlockNumber(String.valueOf(itemBeanBrowser.getBlockNumber()));//可以作为是否交易成功的展示依据
                        txRecordBeansNew.add(0, bean);
                    } else {
                        LoggerUtils.i("oldhash", "old 哈希" + "   " + itemBeanBrowser.getHash());
                    }
                }
                LoggerUtils.i(tag, "all size 3  = " + txRecordBeansNew.size());
                break;
            case TYPE_DONE:
                oldSize = mListDone.size();
                mListNetTemp.addAll(mListDone);
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    LoggerUtils.i(tag, "网络请求成功" + itemBeanBrowser.toString());
                    if (IONCWalletSDK.getInstance().notExist(itemBeanBrowser.getHash())) {
                        insertTxRecordBean(txRecordBeansNew, itemBeanBrowser);
                    }

                    LoggerUtils.i(tag, "out size  = " + txRecordBeansNew.size());
                }
                break;
            case TYPE_DOING:
                oldSize = mListIn.size();
                mListNetTemp.addAll(mListIn);
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    LoggerUtils.i("beannet", "hash" + "   " + itemBeanBrowser.getHash());
                    if (IONCWalletSDK.getInstance().notExist(itemBeanBrowser.getHash())) {
                        LoggerUtils.i("beannet", "onTxRecordBrowserSuccess" + "   新增 = " + itemBeanBrowser.getHash());
                        insertTxRecordBean(txRecordBeansNew, itemBeanBrowser);
                    }

                }
                LoggerUtils.i(tag, "in size  = " + txRecordBeansNew.size());
                break;
            case TYPE_FAILURE:
                oldSize = mListFailure.size();
                mListNetTemp.addAll(mListFailure);
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        data) {
                    LoggerUtils.i("beannet", "hash" + "   " + itemBeanBrowser.getHash());
                    if (IONCWalletSDK.getInstance().notExist(itemBeanBrowser.getHash())) {
                        LoggerUtils.i("beannet", "onTxRecordBrowserSuccess" + "   新增 = " + itemBeanBrowser.getHash());
                        insertTxRecordBean(txRecordBeansNew, itemBeanBrowser);
                    }

                }
                LoggerUtils.i(tag, "in size  = " + txRecordBeansNew.size());
                break;

        }

        if (txRecordBeansNew.size() == 0) {
            LoggerUtils.i(tag, "size = 0,无新数据");
            ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_no_new_data));
            return;
        }

        //数据比对
        int size = 0;

        switch (getType()) {
            case TYPE_ALL:
                LoggerUtils.i(tag, "size = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListAllData.add(0, b);
                }
                size = mListAllData.size();
                break;
            case TYPE_DONE:

                LoggerUtils.i(tag, "size = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListDone.add(0, b);
                }
                size = mListDone.size();
                break;
            case TYPE_DOING:
                LoggerUtils.i(tag, "size in  = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListIn.add(0, b);
                }
                size = mListIn.size();
                break;
            case TYPE_FAILURE:
                LoggerUtils.i(tag, "size in  = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListFailure.add(0, b);
                }
                size = mListFailure.size();
                break;
        }
        ToastUtil.showToastLonger(getAppString(R.string.new_tx_record) + " " + (size - oldSize));
        mTxRecordAdapter.notifyDataSetChanged();
    }

    private void insertTxRecordBean(List<TxRecordBean> txRecordBeansNew, TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser) {
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

    private void finishRefresh() {
        if (mRefresh != null) {
            mRefresh.finishRefresh();
            mRefresh.finishLoadMore();
        }
    }

    /**
     * @param error 网络数据传递错误时，传递本地数据
     */
    @Override
    public void onTxRecordBrowserFailure(String error) {
        finishRefresh();
        ToastUtil.showToastLonger(error);
        LoggerUtils.e("onTxRecordBrowserFailure", error);
    }


    @Override
    public void onTxRecordSuccessDataNUll() {
        finishRefresh();
        ToastUtil.showToastLonger(getAppString(R.string.no_more_rexord));
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
                    mTxRecordPresenter.getTxRecordTo("3", mWalletBeanNew.getAddress(), "1", mPageSizeTo, this);
                    break;
            }
        }
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
        switch (getType()) {
            case TYPE_ALL:
                long txRecordAllCount = IONCWalletSDK.getInstance().txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListAllData.size();
                if (currentCount == txRecordAllCount) {
                    getNetData();
                    return;
                }
                offset = txRecordAllCount - currentCount - mPerPageAddNum;

                if (mListAllDataTemp != null) {
                    mListAllDataTemp.clear();
                }
                mListAllDataTemp = IONCWalletSDK.getInstance().txRecordAllByAddress(mWalletBeanNew.getAddress(), offset, mPerPageAddNum);

                for (TxRecordBean t :
                        mListAllDataTemp) {
                    if (!mListAllData.contains(t)) {
                        mListAllData.add(t);
                    }
                }
                break;
            case TYPE_DONE:
                long txRecordOutCount = IONCWalletSDK.getInstance().txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListDone.size();
                if (currentCount == txRecordOutCount) {
                    getNetData();
                    return;
                }
                offset = txRecordOutCount - currentCount - mPerPageAddNum;

                LoggerUtils.i("method", "onPullToUp" + "   txRecordOutCount " + txRecordOutCount);

                if (mListDoneTemp != null) {
                    mListDoneTemp.clear();
                }
                mListDoneTemp = IONCWalletSDK.getInstance().txRecordAllByAddress(mWalletBeanNew.getAddress(), offset, mPerPageAddNum);
                for (TxRecordBean t :
                        mListDoneTemp) {
                    //不存在，并且不是挂在交易和失败交易
                    if (!mListDone.contains(t) && !TX_SUSPENDED.equals(t.getBlockNumber()) && !TX_FAILURE.equals(t.getBlockNumber())) {
                        LoggerUtils.i("doing",t.toString());
                        mListDone.add(t);
                    }
                }
                break;
            case TYPE_DOING:
                long txRecordInCount = IONCWalletSDK.getInstance().txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListIn.size();
                if (currentCount == txRecordInCount) {
                    getNetData();
                    return;
                }
                offset = txRecordInCount - currentCount - mPerPageAddNum;

                LoggerUtils.i("method", "onPullToUp" + "   txRecordOutCount " + txRecordInCount);
                LoggerUtils.i("method", "onPullToUp" + "   currentCount " + currentCount);
                LoggerUtils.i("method", "onPullToUp" + "   offset " + offset);
                if (mListInTemp != null) {
                    mListInTemp.clear();
                }
                mListInTemp = IONCWalletSDK.getInstance().txRecordAllByBlockNumber(TX_SUSPENDED,mWalletBeanNew.getAddress(), offset, mPerPageAddNum);
                LoggerUtils.i("mmmmm",mListInTemp.size());
                for (TxRecordBean t :
                        mListInTemp) {
                    if (!mListIn.contains(t)) {
                        mListIn.add(t);
                    }
                }
                break;
            case TYPE_FAILURE:
                long txRecordFailureCount = IONCWalletSDK.getInstance().txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListFailure.size();
                if (currentCount == txRecordFailureCount) {
                    getNetData();
                    return;
                }
                offset = txRecordFailureCount - currentCount - mPerPageAddNum;

                LoggerUtils.i("method", "onPullToUp" + "   txRecordFailureCount " + txRecordFailureCount);
                LoggerUtils.i("method", "onPullToUp" + "   currentCount " + currentCount);
                LoggerUtils.i("method", "onPullToUp" + "   offset " + offset);
                if (mListFailureTemp != null) {
                    mListFailureTemp.clear();
                }
                mListFailureTemp = IONCWalletSDK.getInstance().txRecordAllByBlockNumber(TX_FAILURE, mWalletBeanNew.getAddress(), offset, mPerPageAddNum);
                LoggerUtils.i("mmmmm", mListFailureTemp.size());
                for (TxRecordBean t :
                        mListFailureTemp) {
                    if (!mListFailure.contains(t)) {
                        mListFailure.add(t);
                    }
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
                    size = mListDone.size();
                    pageNum = size / 5;
                    pageNum++;
                    mTxRecordPresenter.getTxRecordFrom("3", mWalletBeanNew.getAddress(), String.valueOf(pageNum), mPageSizeFrom, this);
                    break;
                case TYPE_DOING:
                    size = mListIn.size();
                    pageNum = size / 5;
                    pageNum++;
                    mTxRecordPresenter.getTxRecordTo("3", mWalletBeanNew.getAddress(), String.valueOf(pageNum), mPageSizeTo, this);
                    break;
            }
        }
    }

    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {

        LoggerUtils.i("method", "onAddressChanged");
        switch (getType()) {
            case TYPE_ALL:
                mWalletBeanNew = currentWallet;
                mListIn.clear();
                mListDone.clear();
                mListFailure.clear();
                mListAllData.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                mTxRecordAdapter.setCurrentWalletBean(mWalletBeanNew);
                mTxRecordAdapter.notifyDataSetChanged();
                getLocalData();// 全部需要显示
                break;
            case TYPE_DONE:
                mListDone.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                mTxRecordAdapter.setCurrentWalletBean(mWalletBeanNew);
                mTxRecordAdapter.notifyDataSetChanged();
                break;
            case TYPE_DOING:
                mListIn.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                mTxRecordAdapter.setCurrentWalletBean(mWalletBeanNew);
                mTxRecordAdapter.notifyDataSetChanged();
                break;
            case TYPE_FAILURE:
                mListFailure.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                mTxRecordAdapter.setCurrentWalletBean(mWalletBeanNew);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void OnTxRecordNodeSuccess(TxRecordBean txRecordBean) {
        if (objectAnimator != null) {
            objectAnimator.end();//动画结束
        }
        mSyncImageView.setClickable(true);
        TextView v = (TextView) mTxRecordAdapter.getViewByPosition(mItemClickedPos, R.id.tx_datetime_tv);
        LoggerUtils.i("ethTransaction", "OnTxRecordNodeSuccess" + "   " + txRecordBean.toString());
        if (TX_SUSPENDED.equals(txRecordBean.getBlockNumber())) {
            ToastUtil.showToastLonger(getAppString(R.string.tx_block_suspended));
            return;
        }
        if (v != null) {
            v.setText(getResources().getString(R.string.tx_block) + split + txRecordBean.getBlockNumber());
        }
        LoggerUtils.i("ethTransaction", "OnTxRecordNodeSuccess4" + "   " + txRecordBean.toString());
        mSyncImageView.setVisibility(View.GONE);
        IONCWalletSDK.getInstance().updateTxRecordBean(txRecordBean);
    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean txRecordBean) {
        LoggerUtils.e("ethTransaction", error);
        mSyncImageView.setClickable(true);
        txRecordBean.setBlockNumber(TX_FAILURE);
        IONCWalletSDK.getInstance().updateTxRecordBean(txRecordBean);
        if (objectAnimator != null) {
            objectAnimator.end();//动画结束
        }
    }

    @Override
    public void onTxRecordNodeStart() {

    }
}
