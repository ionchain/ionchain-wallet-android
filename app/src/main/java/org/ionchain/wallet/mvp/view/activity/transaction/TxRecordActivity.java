package org.ionchain.wallet.mvp.view.activity.transaction;

import android.content.Intent;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnTxRecordCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordViewHelper;
import org.ionchain.wallet.bean.NodeBean;
import org.ionchain.wallet.constant.ConstantUrl;
import org.ionchain.wallet.helper.NodeHelper;
import org.ionchain.wallet.mvp.callback.OnIONCNodeCallback;
import org.ionchain.wallet.mvp.callback.OnLoadingView;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ionchain.wallet.constant.ConstantIntentParam.INTENT_PARAM_CURRENT_WALLET;
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_BLOCK_NUMBER;
import static org.ionchain.wallet.constant.ConstantParams.INTENT_PARAME_WALLET_ADDRESS;

public class TxRecordActivity extends AbsBaseActivity implements OnLoadingView, OnRefreshListener, OnLoadMoreListener,
        OnTxRecordCallback, NodeHelper.OnTryTimesCallback, OnIONCNodeCallback {
    /**
     * 记录适配器
     */
    private CommonAdapter adapterLv;
    /**
     * 钱包记录的实际数据集
     */
    private List<TxRecordBean> mTxRecordBeanList = new ArrayList<>();
    /**
     * 钱包地址
     */
    private String mAddress;
    private SmartRefreshLayout mSmartRefreshLayout;

    /**
     * 所有本地记录的缓存
     */
    List<TxRecordBean> mRecordBeanListLocalTemp = new ArrayList<>();
    /**
     * 未获取交易信息的本地缓存
     */
    List<TxRecordBean> mRecordLocalUnpackedTemp = new ArrayList<>();
    /**
     * 例子链接点
     */
    private String mNodeIONC = ConstantUrl.HOST_NODE_MAIN;
    /**
     * 当前钱包
     */
    private WalletBeanNew mWalletBeanNew;
    /**
     * listvie辅助
     */
    private TxRecordViewHelper mTxRecordViewHelper;

    @Override
    protected void initData() {
        LoggerUtils.i("address" + mAddress);
        if (mAddress == null) {
            return;
        }
        mRecordBeanListLocalTemp = IONCWalletSDK.getInstance().getAllTxRecordBeansByAddress(mAddress);
        if (mRecordBeanListLocalTemp == null || mRecordBeanListLocalTemp.size() == 0) {
            ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
            return;
        }
        int size = mRecordBeanListLocalTemp.size();
        Collections.sort(mRecordBeanListLocalTemp);
        for (int i = 0; i < size; i++) {
            LoggerUtils.i("txRecordBean " + mRecordBeanListLocalTemp.get(i).toString());
            mTxRecordBeanList.add(mRecordBeanListLocalTemp.get(i));
            if (DEFAULT_TRANSCATION_BLOCK_NUMBER.equals(mRecordBeanListLocalTemp.get(i).getBlockNumber())) {
                LoggerUtils.i("unpacked" + mRecordBeanListLocalTemp.get(i).toString());
                mRecordLocalUnpackedTemp.add(mRecordBeanListLocalTemp.get(i));
            }
        }
        adapterLv.notifyDataSetChanged();

    }

    @Override
    protected void initView() {
        mImmersionBar.titleView(R.id.toolbarlayout).statusBarDarkFont(true).execute();
        ListView tx_record_lv = findViewById(R.id.tx_recoder_lv);
        mSmartRefreshLayout = findViewById(R.id.refresh_tx_record);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setEnableAutoLoadMore(false);
        mTxRecordViewHelper = new TxRecordViewHelper();
        adapterLv = new CommonAdapter(this, mTxRecordBeanList, R.layout.item_txrecoder, mTxRecordViewHelper);
        tx_record_lv.setAdapter(adapterLv);
        findViewById(R.id.back).setOnClickListener(v -> finish());
    }

    @Override
    protected void handleIntent(Intent intent) {
        mAddress = intent.getStringExtra(INTENT_PARAME_WALLET_ADDRESS);
        mWalletBeanNew = intent.getParcelableExtra(INTENT_PARAM_CURRENT_WALLET);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_recoder;
    }

    @Override
    public void OnTxRecordSuccess(TxRecordBean txRecordBean, int index) {
        if (txRecordBean != null) {
            LoggerUtils.i("txRecordBean " + txRecordBean.toString());
            IONCWalletSDK.getInstance().updateTxRecordBean(mTxRecordBeanList.get(index));
            mRecordLocalUnpackedTemp.remove(index);//移除已打包
        }
        Collections.sort(mTxRecordBeanList);
        adapterLv.notifyDataSetChanged();
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void onTxRecordFailure(String error) {
        ToastUtil.showShortToast(error);
        mSmartRefreshLayout.finishRefresh();
        NodeHelper.getInstance().tryGetNode(this, this);
    }

    @Override
    public void onLoadStart() {
        LoggerUtils.i("正在获取");
    }

    @Override
    public void onLoadFinish() {
        LoggerUtils.i("获取完成");
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        LoggerUtils.i("刷新");
        int size = mRecordLocalUnpackedTemp.size();
        if (size == 0) {
            LoggerUtils.i("无刷新");
            mSmartRefreshLayout.finishRefresh();
            return;
        }
        LoggerUtils.i("需更新 " + size);
        for (int i = 0; i < size; i++) {
            IONCWalletSDK.getInstance().ethTransaction(mNodeIONC
                    , mRecordBeanListLocalTemp.get(i).getHash()
                    , mRecordBeanListLocalTemp.get(i)
                    , this, i);
        }
    }

    /**
     * @param refreshLayout 加载更多
     */
    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
    }

    @Override
    public void onTryTimes(int count) {
        ToastUtil.showToastLonger(getAppString(R.string.try_net_times, count));
    }

    @Override
    public void onTryFinish(int count) {
        mSmartRefreshLayout.finishRefresh();
        ToastUtil.showToastLonger(getAppString(R.string.try_net_times_finish, count));
    }

    @Override
    public void onIONCNodeStart() {

    }

    @Override
    public void onIONCNodeSuccess(List<NodeBean.DataBean> dataBean) {
        mNodeIONC = dataBean.get(0).getIonc_node();
    }

    @Override
    public void onIONCNodeError(String error) {
        NodeHelper.getInstance().tryGetNode(this, this);
    }

    @Override
    public void onIONCNodeFinish() {
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NodeHelper.getInstance().cancelAndReset();//取消自动尝试
    }

    @Override
    protected void onStop() {
        super.onStop();
        NodeHelper.getInstance().cancelAndReset();//取消自动尝试
    }
}
