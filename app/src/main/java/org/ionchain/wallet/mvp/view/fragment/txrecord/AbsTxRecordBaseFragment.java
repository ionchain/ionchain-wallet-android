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

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.TxRecordBeanAllHelper;
import org.ionc.wallet.bean.TxRecordBeanInHelper;
import org.ionc.wallet.bean.TxRecordBeanOutHelper;
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
import java.util.Collections;
import java.util.List;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;
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
    private int mPageNum = 0;
    private RecyclerView mListView;
    /**
     * 每次上拉加载更过的时候，新增的itemd的数量
     */
    private long mPerNewItemNumByPullUp = 10;
    private ObjectAnimator objectAnimator;

    /**
     * 缓存当期那被点击的item的位置
     */
    private int mItemClickedPos;
    private String split = " ： ";
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

                    LoggerUtils.i("binny","initView"+"   AbsTxRecordBaseFragment"+position);
                    objectAnimator = ObjectAnimator.ofFloat(view15, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
                    objectAnimator.setDuration(3000);//设置动画时间
                    objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
                    objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                    objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
                    objectAnimator.start();//动画开始
                    mItemClickedPos = position;
                    IONCWalletSDK.getInstance().ethTransaction(mNode, mListAllData.get(position).getHash(), mListAllData.get(position), AbsTxRecordBaseFragment.this);
                });
                break;
            case TYPE_OUT:
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListOut);
                mTxRecordAdapter.setOnItemClickListener((adapter, view13, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListOut.get(position));
                    startActivity(intent);
                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
                    ToastUtil.showToastLonger("dd333ddd");
                });
                break;
            case TYPE_IN:
                mTxRecordAdapter = new TxRecordAdapter(getActivity(), R.layout.item_txrecord, mListIn);
                mTxRecordAdapter.setOnItemClickListener((adapter, view14, position) -> {
                    Intent intent = new Intent(mActivity, TxRecordDetailActivity.class);
                    intent.putExtra(PARCELABLE_TX_RECORD, mListIn.get(position));
                    startActivity(intent);
                });
                mTxRecordAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
                    ToastUtil.showToastLonger("d3333dddd");
                });
                break;
        }
        mTxRecordAdapter.bindToRecyclerView(mListView);
        mTxRecordAdapter.openLoadAnimation(SCALEIN);
        mListView.setAdapter(mTxRecordAdapter);
    }

    @Override
    protected void visible() {
        LoggerUtils.i(tag, "TAG_NAME = " + TAG_NAME + " visible() = " + isVisible());
        getLocalData();//可见 visible ,左右切换
        mPageNum = 0;
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
                mListAllDataTemp.clear();

                mListAllDataTemp = IONCWalletSDK.getInstance().getTxRecordBeanAllByPublicKey(mWalletBeanNew.getPublic_key());
                if (mListAllDataTemp.size() == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
                    return;
                }
                mListAllData.clear();
                mListAllData.addAll(mListAllDataTemp);
                LoggerUtils.i(tag, "mListDataTemp.size = " + mListDataTemp.size());
                Collections.sort(mListAllData);
                break;
            case TYPE_OUT:
                mListOutTemp.clear();
                mListOutTemp = IONCWalletSDK.getInstance().getTxRecordBeanOutByAddress(mOffset, mWalletBeanNew.getAddress(), 5, 5);
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
                mListInTemp = IONCWalletSDK.getInstance().getTxRecordBeanInByAddress(mOffset, mWalletBeanNew.getAddress(), 5, 5);
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
                break;
            case TYPE_OUT:
                size = mListOut.size();
                LoggerUtils.i(tag, "size = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListOut.add(0, b);
                }
                break;
            case TYPE_IN:
                size = mListIn.size();
                LoggerUtils.i(tag, "size in  = " + size);
                for (TxRecordBean b :
                        txRecordBeansNew) {
                    txRecordHelper(b);
                    mListIn.add(0, b);
                }
                break;
        }
        mTxRecordAdapter.notifyDataSetChanged();
    }

    /**
     * @param txRecordBean 转账的辅助数据库字段更新
     */
    private void txRecordHelper(TxRecordBean txRecordBean) {
        txRecordBean.setPublicKey(mWalletBeanNew.getPublic_key());
        /*
         * 全部
         */
        TxRecordBeanAllHelper txRecordBeanAllHelper = IONCWalletSDK.getInstance().getTxRecordBeanHelperByPublicKey(mWalletBeanNew.getPublic_key());
        if (txRecordBeanAllHelper == null) {
            txRecordBeanAllHelper = new TxRecordBeanAllHelper();
            txRecordBeanAllHelper.setPublicKey(mWalletBeanNew.getPublic_key());
            txRecordBeanAllHelper.setIndexMaxForAll(1L);
            txRecordBean.setIndexForAll(1L);
            IONCWalletSDK.getInstance().saveCurrentWalletTxRecordAllHelper(txRecordBeanAllHelper);
        } else {
            txRecordBeanAllHelper.setIndexMaxForAll(txRecordBeanAllHelper.getIndexMaxForAll() + 1);
            txRecordBean.setIndexForAll(txRecordBeanAllHelper.getIndexMaxForAll());
            IONCWalletSDK.getInstance().updateCurrentWalletTxRecordAllHelper(txRecordBeanAllHelper);
        }
        LoggerUtils.i("method", "txRecordHelper" + "   out = " + txRecordBeanAllHelper.toString());
        /*
         * 转出
         */
        if (txRecordBean.getFrom().equals(mWalletBeanNew.getAddress())) {
            TxRecordBeanOutHelper beanOutHelper = IONCWalletSDK.getInstance().getHelperOutByAddressFrom(mWalletBeanNew.getAddress());

            if (beanOutHelper == null) {
                beanOutHelper = new TxRecordBeanOutHelper();
                beanOutHelper.setFromAddress(mWalletBeanNew.getAddress());
                beanOutHelper.setIndexMaxForOut(1L);
                txRecordBean.setIndexForOut(1L);
                IONCWalletSDK.getInstance().saveCurrentWalletTxRecordBeanOutHelper(beanOutHelper);
            } else {
                beanOutHelper.setIndexMaxForOut(beanOutHelper.getIndexMaxForOut() + 1);
                txRecordBean.setIndexForOut(beanOutHelper.getIndexMaxForOut());
                IONCWalletSDK.getInstance().updateCurrentWalletTxRecordOutHelper(beanOutHelper);
            }
            LoggerUtils.i("method", "txRecordHelper" + "   out = " + beanOutHelper.toString());
        }
        /*
         * 转入
         */
        if (txRecordBean.getTo().equals(mWalletBeanNew.getAddress())) {
            TxRecordBeanInHelper beanInHelper = IONCWalletSDK.getInstance().getTxRecordBeanInHelperByAddressTo(mWalletBeanNew.getAddress());
            if (beanInHelper == null) {
                beanInHelper = new TxRecordBeanInHelper();
                beanInHelper.setToAddress(mWalletBeanNew.getAddress());
                beanInHelper.setIndexMaxForIn(1L);
                txRecordBean.setIndexForIn(1L);
                IONCWalletSDK.getInstance().saveCurrentWalletTxRecordBeanInHelper(beanInHelper);
            } else {
                beanInHelper.setIndexMaxForIn(beanInHelper.getIndexMaxForIn() + 1);
                txRecordBean.setIndexForIn(beanInHelper.getIndexMaxForIn());
                IONCWalletSDK.getInstance().updateCurrentWalletTxRecordInHelper(beanInHelper);
            }
            LoggerUtils.i("method", "txRecordHelper" + "   in = " + beanInHelper.toString());
        }
        IONCWalletSDK.getInstance().saveTxRecordBean(txRecordBean);
    }


    /**
     * @param error 网络数据传递错误时，传递本地数据
     */
    @Override
    public void onTxRecordBrowserFailure(String error) {
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
        /*
         * 先得到当期页的条目数
         */
        long indexMax = IONCWalletSDK.getInstance().getTxRecordBeanHelperByPublicKey(mWalletBeanNew.getPublic_key()).getIndexMaxForAll();
        LoggerUtils.i("method", "onPullToUp" + "   AbsTxRecordBaseFragment " + indexMax);
        long fromId = 0; //包括
        long toId = 0;   //包括
        int size = 0;
        switch (getType()) {
            case TYPE_ALL:
                size = mListAllData.size();
                toId = indexMax - size;
                if (toId == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
                    return;
                }
                fromId = toId - mPerNewItemNumByPullUp + 1;//每页展示 2 个
                mListAllDataTemp.clear();
                mListAllDataTemp = IONCWalletSDK.getInstance().loadMoreTxRecordBeanAllByPublic(mWalletBeanNew.getPublic_key(), fromId, toId);
                for (TxRecordBean t :
                        mListAllDataTemp) {
                    LoggerUtils.i("method", "onPullToUp" + " id =  " + t.getId() + " 哈希 =  " + t.getHash());
                }
                LoggerUtils.i("temp = ", mListAllDataTemp.size());
                mListAllData.addAll(mListAllDataTemp);
                break;
            case TYPE_OUT:
                size = mListOut.size();
                toId = indexMax - size;
                if (toId == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
                    return;
                }
                fromId = toId - mPerNewItemNumByPullUp + 1;//每页展示 2 个
                mListOutTemp.clear();
                mListOutTemp = IONCWalletSDK.getInstance().loadMoreTxRecordBeanOutByAddressFrom(mWalletBeanNew.getAddress(), fromId, toId);
                for (TxRecordBean t :
                        mListOutTemp) {
                    LoggerUtils.i("method", "onPullToUp" + "   " + t.getHash());
                }
                LoggerUtils.i("temp = ", mListOutTemp.size());
                mListOut.addAll(mListOutTemp);
                break;
            case TYPE_IN:
                size = mListIn.size();
                toId = indexMax - size;
                if (toId == 0) {
                    ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
                    return;
                }
                fromId = toId - mPerNewItemNumByPullUp + 1;//每页展示 2 个
                mListInTemp.clear();
                mListInTemp = IONCWalletSDK.getInstance().loadMoreTxRecordBeanInByAddressFrom(mWalletBeanNew.getAddress(), fromId, toId);
                for (TxRecordBean t :
                        mListInTemp) {
                    LoggerUtils.i("method", "onPullToUp" + "   " + t.getHash());
                }
                LoggerUtils.i("temp = ", mListInTemp.size());
                mListIn.addAll(mListInTemp);
                break;
        }
        LoggerUtils.i("method", "   fromId = " + fromId + "   toId = " + toId + "   size = " + size);
        mTxRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddressChanged(WalletBeanNew currentWallet) {
        LoggerUtils.i("method", "onAddressChanged");
        mOffset = 0;
        mPageNum = 0;
        switch (getType()) {
            case TYPE_ALL:
                mWalletBeanNew = currentWallet;
                mListIn.clear();
                mListOut.clear();
                mListAllData.clear();
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

    @SuppressLint("SetTextI18n")
    @Override
    public void OnTxRecordNodeSuccess(TxRecordBean txRecordBean) {
        objectAnimator.end();//动画结束
        TextView v = (TextView) mTxRecordAdapter.getViewByPosition(mItemClickedPos,R.id.tx_block);
        LoggerUtils.i("binny","OnTxRecordNodeSuccess"+"   "+txRecordBean.toString());
        if (v != null) {
            v.setText(getResources().getString(R.string.tx_block) + split +txRecordBean.getBlockNumber());
        }
        IONCWalletSDK.getInstance().updateTxRecordBean(txRecordBean);
    }

    @Override
    public void onTxRecordNodeFailure(String error, TxRecordBean txRecordBean) {
        LoggerUtils.e("binny", error);
        objectAnimator.end();//动画结束
    }
}
