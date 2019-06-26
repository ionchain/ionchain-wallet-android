package org.ionchain.wallet.mvp.view.fragment.txrecord;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.okgo.OkGo;

import org.ionc.wallet.bean.CurrentPageNum;
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
import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.List;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;
import static org.ionc.wallet.sdk.IONCWalletSDK.TX_SUSPENDED;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_TX_RECORD;
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;

/**
 * 1、先网络数据，
 * 2、如果网络数据获取失败，则直接显示本地数据
 * 3、如果网络数据获取成功，则对比本地缓存的未记录的数据，并更新本地数据
 */
public abstract class AbsTxRecordBaseFragment extends AbsBaseViewPagerFragment implements OnTxRecordBrowserDataCallback, AssetFragment.OnPullToRefreshCallback, OnTxRecordFromNodeCallback {

    private List<TxRecordBean> mListOutTemp = new ArrayList<>();
    private List<TxRecordBean> mListInTemp = new ArrayList<>();
    private String mNode = getHostNode();
    private String tag = "beannet";
    private int mOffset = 0;
    protected int mPageNum = 0;
    protected static CurrentPageNum mCurrentPageNumBean;
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
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListAllData);
                mTxRecordAdapter.setOnItemClickListener((adapter, view12, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListAllData.get(position));
                    startActivity(intent);
                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view15, position) -> {
                    syncBrowser(view15, position);
                });
                break;
            case TYPE_OUT:
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListOut);
                mTxRecordAdapter.setOnItemClickListener((adapter, view13, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListOut.get(position));
                    startActivity(intent);
                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view15, position) -> {
                    syncBrowser(view15, position);
                });
                break;
            case TYPE_IN:
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListIn);
                mTxRecordAdapter.setOnItemClickListener((adapter, view14, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListIn.get(position));
                    startActivity(intent);
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

    private void syncBrowser(View view15, int position) {
        mSyncImageView = view15;
        LoggerUtils.i("binny", "initView" + "   AbsTxRecordBaseFragment" + position);
        objectAnimator = ObjectAnimator.ofFloat(view15, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(3000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.start();//动画开始
        mItemClickedPos = position;
        IONCWalletSDK.getInstance().ethTransaction(mNode, mListAllData.get(position).getHash(), mListAllData.get(position), AbsTxRecordBaseFragment.this);
    }

    @Override
    protected void visible() {
        LoggerUtils.i(tag, "TAG_NAME = " + TAG_NAME + " visible() = " + isVisible());
        getPageNum();
        getLocalData();//可见 visible ,左右切换
    }

    private void getPageNum() {
        switch (getType()) {
            case TYPE_ALL:
                mCurrentPageNumBean = IONCWalletSDK.getInstance().txAllCurrentPageNumBean(mWalletBeanNew.getAddress());
                LoggerUtils.i("method1","getPageNum"+"   all "+ mCurrentPageNumBean);
                if (mCurrentPageNumBean == null) {
                    mCurrentPageNumBean = new CurrentPageNum();
                    mPageNum = 1;
                    mCurrentPageNumBean.setAddress(mWalletBeanNew.getAddress());
                    mCurrentPageNumBean.setNumAll(String.valueOf(1));
                    IONCWalletSDK.getInstance().saveCurrentPageNum(mCurrentPageNumBean);
                } else {
                    if (mCurrentPageNumBean.getNumAll() == null) {
                        mPageNum = 1;
                        mCurrentPageNumBean.setNumAll(String.valueOf(1));
                        IONCWalletSDK.getInstance().saveCurrentPageNum(mCurrentPageNumBean);
                    }else {
                        mPageNum = Integer.parseInt(mCurrentPageNumBean.getNumAll());
                    }
                }
                break;
            case TYPE_OUT:
                mCurrentPageNumBean = IONCWalletSDK.getInstance().txAllCurrentPageNumBean(mWalletBeanNew.getAddress());
                LoggerUtils.i("method1","getPageNum"+"   out "+ mCurrentPageNumBean);
                if (mCurrentPageNumBean == null) {
                    mCurrentPageNumBean = new CurrentPageNum();
                    mPageNum = 1;
                    mCurrentPageNumBean.setAddress(mWalletBeanNew.getAddress());
                    mCurrentPageNumBean.setNumOut(String.valueOf(1));
                    IONCWalletSDK.getInstance().saveCurrentPageNum(mCurrentPageNumBean);
                } else {
                    if (mCurrentPageNumBean.getNumOut() == null) {
                        mPageNum = 1;
                        mCurrentPageNumBean.setNumOut(String.valueOf(1));
                        IONCWalletSDK.getInstance().saveCurrentPageNum(mCurrentPageNumBean);
                    }else {
                        mPageNum = Integer.parseInt(mCurrentPageNumBean.getNumOut());
                    }
                }
                break;
            case TYPE_IN:
                mCurrentPageNumBean = IONCWalletSDK.getInstance().txAllCurrentPageNumBean(mWalletBeanNew.getAddress());
                LoggerUtils.i("method1","getPageNum"+"   in "+ mCurrentPageNumBean);
                if (mCurrentPageNumBean == null) {
                    mCurrentPageNumBean = new CurrentPageNum();
                    mPageNum = 1;
                    mCurrentPageNumBean.setAddress(mWalletBeanNew.getAddress());
                    mCurrentPageNumBean.setNumIn(String.valueOf(1));
                    IONCWalletSDK.getInstance().saveCurrentPageNum(mCurrentPageNumBean);
                } else {
                    if (mCurrentPageNumBean.getNumIn() == null) {
                        mPageNum = 1;
                        mCurrentPageNumBean.setNumIn(String.valueOf(1));
                        IONCWalletSDK.getInstance().saveCurrentPageNum(mCurrentPageNumBean);
                    }else {
                        mPageNum = Integer.parseInt(mCurrentPageNumBean.getNumIn());
                    }
                }
                break;
        }
    }

    /**
     * @return 当前页的类型
     */
    protected abstract int getType();

    @Override
    protected void initData() {
        LoggerUtils.i(tag, "initData ");
        getPageNum();
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
     * 网络上，带地址的所有交易记录
     * {@link org.ionchain.wallet.mvp.model.txrecoder.TxRecordModel#getTxRecord(String, String, String, String, String, OnTxRecordBrowserDataCallback)}  }
     *
     * @param beans 浏览器接口中数据
     */
    @Override
    public void onTxRecordBrowserSuccess(TxRecordBeanTemp.DataBean beans) {
        mListNetTemp.clear();
        int num = 0;
        List<TxRecordBean> txRecordBeansNew = new ArrayList<>();
        switch (getType()) {
            case TYPE_ALL:
                mListNetTemp.addAll(mListAllData);

                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        beans.getData()) {
                    LoggerUtils.i(tag, "网络请求成功" + itemBeanBrowser.toString());
                    if (IONCWalletSDK.getInstance().notExist(itemBeanBrowser.getHash())) {
                        LoggerUtils.i("method", "新增哈希" + "   " + itemBeanBrowser.getHash());
                        TxRecordBean bean = new TxRecordBean();
                        bean.setHash(itemBeanBrowser.getHash());
                        bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
                        bean.setTo(itemBeanBrowser.getTx_to());
                        bean.setFrom(itemBeanBrowser.getTx_from());
                        bean.setValue(String.valueOf(Convert.fromWei(itemBeanBrowser.getValue(), Convert.Unit.ETHER)));
                        bean.setSuccess(true);
                        bean.setPublicKey(mWalletBeanNew.getPublic_key());
                        bean.setGas(itemBeanBrowser.getGas());
                        bean.setNonce(itemBeanBrowser.getNonce());
                        bean.setBlockNumber(String.valueOf(itemBeanBrowser.getBlockNumber()));//可以作为是否交易成功的展示依据

                        txRecordBeansNew.add(0, bean);
                    }
                }
                LoggerUtils.i(tag, "all size  = " + txRecordBeansNew.size());
                break;
            case TYPE_OUT:
                mListNetTemp.addAll(mListOut);
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        beans.getData()) {
                    LoggerUtils.i(tag, "网络请求成功" + itemBeanBrowser.toString());
                    if (IONCWalletSDK.getInstance().notExist(itemBeanBrowser.getHash())) {
                        TxRecordBean bean = new TxRecordBean();
                        bean.setHash(itemBeanBrowser.getHash());
                        bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
                        bean.setTo(itemBeanBrowser.getTx_to());
                        bean.setFrom(itemBeanBrowser.getTx_from());
                        bean.setValue(String.valueOf(Convert.fromWei(itemBeanBrowser.getValue(), Convert.Unit.ETHER)));
                        bean.setSuccess(true);
                        bean.setPublicKey(mWalletBeanNew.getPublic_key());
                        bean.setGas(itemBeanBrowser.getGas());
                        bean.setNonce(itemBeanBrowser.getNonce());
                        bean.setBlockNumber(String.valueOf(itemBeanBrowser.getBlockNumber()));//可以作为是否交易成功的展示依据
                        txRecordBeansNew.add(0, bean);
                    }

                    LoggerUtils.i(tag, "out size  = " + txRecordBeansNew.size());
                }
                break;
            case TYPE_IN:
                mListNetTemp.addAll(mListIn);
                for (TxRecordBeanTemp.DataBean.ItemBean itemBeanBrowser :
                        beans.getData()) {
                    LoggerUtils.i("beannet", "hash" + "   " + itemBeanBrowser.getHash());
                    if (IONCWalletSDK.getInstance().notExist(itemBeanBrowser.getHash())) {
                        LoggerUtils.i("beannet", "onTxRecordBrowserSuccess" + "   新增 = " + itemBeanBrowser.getHash());
                        TxRecordBean bean = new TxRecordBean();
                        bean.setHash(itemBeanBrowser.getHash());
                        bean.setTc_in_out(String.valueOf(System.currentTimeMillis()));
                        bean.setTo(itemBeanBrowser.getTx_to());
                        bean.setFrom(itemBeanBrowser.getTx_from());
                        bean.setValue(String.valueOf(Convert.fromWei(itemBeanBrowser.getValue(), Convert.Unit.ETHER)));
                        bean.setSuccess(true);
                        bean.setPublicKey(mWalletBeanNew.getPublic_key());
                        bean.setGas(itemBeanBrowser.getGas());
                        bean.setNonce(itemBeanBrowser.getNonce());
                        bean.setBlockNumber(String.valueOf(itemBeanBrowser.getBlockNumber()));//可以作为是否交易成功的展示依据
                        txRecordBeansNew.add(0, bean);
                    }

                }
                LoggerUtils.i(tag, "in size  = " + txRecordBeansNew.size());
                break;

        }

        if (txRecordBeansNew.size() == 0) {
            LoggerUtils.i(tag, "size = 0,无新数据");
            mPageNum--;
            ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_no_new_data));
            return;
        }

        int size;
        switch (getType()) {
            case TYPE_ALL:
                size = mListAllData.size();
                LoggerUtils.i(tag, "size = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListAllData.add(0, b);
                }
                mCurrentPageNumBean.setNumAll(String.valueOf(mPageNum));
                IONCWalletSDK.getInstance().updateCurrentPageNum(mCurrentPageNumBean);
                break;
            case TYPE_OUT:
                size = mListOut.size();
                LoggerUtils.i(tag, "size = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListOut.add(0, b);
                }
                mCurrentPageNumBean.setNumOut(String.valueOf(mPageNum));
                IONCWalletSDK.getInstance().updateCurrentPageNum(mCurrentPageNumBean);
                break;
            case TYPE_IN:
                size = mListIn.size();
                LoggerUtils.i(tag, "size in  = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListIn.add(0, b);
                }
                mCurrentPageNumBean.setNumIn(String.valueOf(mPageNum));
                IONCWalletSDK.getInstance().updateCurrentPageNum(mCurrentPageNumBean);
                break;
        }
        mTxRecordAdapter.notifyDataSetChanged();
    }

    /**
     * @param txRecordBean 转账的辅助数据库字段更新
     */
    private void txRecordHelper(TxRecordBean txRecordBean) {
        txRecordBean.setPublicKey(mWalletBeanNew.getPublic_key());

        IONCWalletSDK.getInstance().saveTxRecordBean(txRecordBean);
    }


    /**
     * @param error 网络数据传递错误时，传递本地数据
     */
    @Override
    public void onTxRecordBrowserFailure(String error) {
        mPageNum--;
        LoggerUtils.e("onTxRecordBrowserFailure", error);
    }


    @Override
    public void onTxRecordSuccessDataNUll() {
        LoggerUtils.i("method", "onTxRecordSuccessDataNUll" + "   AbsTxRecordBaseFragment");
        mPageNum--;
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
            switch (getType()) {
                case TYPE_ALL:
                    mPageNum++;

                    LoggerUtils.i("beannet", "all  mPageNum = " + mPageNum);
                    mTxRecordPresenter.getTxRecordAll("3", mWalletBeanNew.getAddress(), String.valueOf(mPageNum), "10", this);
                    break;
                case TYPE_OUT:
                    mPageNum++;

                    LoggerUtils.i("beannet", "out  mPageNum = " + mPageNum);
                    mTxRecordPresenter.getTxRecordFrom("3", mWalletBeanNew.getAddress(), String.valueOf(mPageNum), "5", this);
                    break;
                case TYPE_IN:
                    mPageNum++;

                    LoggerUtils.i("beannet", "in  mPageNum = " + mPageNum);
                    mTxRecordPresenter.getTxRecordTo("3", mWalletBeanNew.getAddress(), String.valueOf(mPageNum), "5", this);
                    break;
            }
        }
    }

    /**
     * 下来加载更多
     *
     * @param walletBeanNew
     */
    @Override
    public void onPullToUp(WalletBeanNew walletBeanNew) {
        LoggerUtils.i("method", "onPullToUp" + "   AbsTxRecordBaseFragment");
        if (!mVisibleToUser) {
            return;
        }
        localData();
    }

    private void localData() {
        /*
         * 先获数据库中取当前钱包相关的记录的总数，即转出和转入
         */
        long offset = 0;
        int currentCount = 0;
        switch (getType()) {
            case TYPE_ALL:
                long txRecordAllCount = IONCWalletSDK.getInstance().txRecordItemCountAllByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListAllData.size();
                if (currentCount == txRecordAllCount) {
                    ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
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
            case TYPE_OUT:
                long txRecordOutCount = IONCWalletSDK.getInstance().txRecordItemCountOutByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListOut.size();
                if (currentCount == txRecordOutCount) {
                    ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
                    return;
                }
                offset = txRecordOutCount - currentCount - mPerPageAddNum;

                LoggerUtils.i("method", "onPullToUp" + "   txRecordOutCount " + txRecordOutCount);

                if (mListOutTemp != null) {
                    mListOutTemp.clear();
                }
                mListOutTemp = IONCWalletSDK.getInstance().txRecordOutByAddress(mWalletBeanNew.getAddress(), offset, mPerPageAddNum);
                for (TxRecordBean t :
                        mListOutTemp) {
                    if (!mListOut.contains(t)) {
                        mListOut.add(t);
                    }
                }
                break;
            case TYPE_IN:
                long txRecordInCount = IONCWalletSDK.getInstance().txRecordCountItemInByAddress(mWalletBeanNew.getAddress());

                /*
                 * 展示中的数量
                 */
                currentCount = mListIn.size();
                if (currentCount == txRecordInCount) {
                    ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
                    return;
                }
                offset = txRecordInCount - currentCount - mPerPageAddNum;

                LoggerUtils.i("method", "onPullToUp" + "   txRecordOutCount " + txRecordInCount);
                LoggerUtils.i("method", "onPullToUp" + "   currentCount " + currentCount);
                LoggerUtils.i("method", "onPullToUp" + "   offset " + offset);
                if (mListInTemp != null) {
                    mListInTemp.clear();
                }
                mListInTemp = IONCWalletSDK.getInstance().txRecordInByAddress(mWalletBeanNew.getAddress(), offset, mPerPageAddNum);

                for (TxRecordBean t :
                        mListInTemp) {
                    if (!mListIn.contains(t)) {
                        mListIn.add(t);
                    }
                }
                break;
        }

        mTxRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {
        LoggerUtils.i("method", "onAddressChanged");

        resetData();
        switch (getType()) {
            case TYPE_ALL:
                mWalletBeanNew = currentWallet;
                mListIn.clear();
                mListOut.clear();
                mListAllData.clear();
                if (mTxRecordAdapter == null) {
                    return;
                }
                mTxRecordAdapter.notifyDataSetChanged();
                getLocalData();// 全部需要显示
                break;
            case TYPE_OUT:
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

    private void resetData() {
        mOffset = 0;
        mPageNum = 0;
        mCurrentPageNumBean = null;
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
        objectAnimator.end();//动画结束
        TextView v = (TextView) mTxRecordAdapter.getViewByPosition(mItemClickedPos, R.id.tx_block);
        LoggerUtils.i("binny", "OnTxRecordNodeSuccess" + "   " + txRecordBean.toString());
        if (TX_SUSPENDED.equals(txRecordBean.getBlockNumber())) {
            ToastUtil.showToastLonger(getAppString(R.string.tx_mount));
            return;
        }
        if (v != null) {
            v.setText(getResources().getString(R.string.tx_block) + split + txRecordBean.getBlockNumber());
        }
        mSyncImageView.setVisibility(View.GONE);
        IONCWalletSDK.getInstance().updateTxRecordBean(txRecordBean);
    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean txRecordBean) {
        LoggerUtils.e("binny", error);
        objectAnimator.end();//动画结束
    }
}
