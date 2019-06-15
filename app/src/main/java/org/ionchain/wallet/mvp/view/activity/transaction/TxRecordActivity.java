package org.ionchain.wallet.mvp.view.activity.transaction;

import android.content.Context;
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
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_NIMBER;
import static org.ionchain.wallet.constant.ConstantParams.INTENT_PARAME_WALLET_ADDRESS;

public class TxRecordActivity extends AbsBaseActivity implements OnLoadingView, OnRefreshListener, OnLoadMoreListener,
        OnTxRecordCallback, NodeHelper.OnTryTimesCallback, OnIONCNodeCallback {
    private CommonAdapter adapterLv;
    private List<TxRecordBean> mTxRecordBeanList = new ArrayList<>();
    private String mAddress;
    private SmartRefreshLayout mSmartRefreshLayout;
    private int totalCount = 0;
    private int pageNum = 2;
    List<TxRecordBean> mRecordBeanListLocalTemp = new ArrayList<>();
    private String mNodeIONC = ConstantUrl.HOST_NODE_MAIN;
    private WalletBeanNew mWalletBeanNew;
    private TxRecordViewHelper mTxRecordViewHelper;

    @Override
    protected void initData() {
        LoggerUtils.i("address" + mAddress);
        if (mAddress == null) {
            return;
        }
        mRecordBeanListLocalTemp = IONCWalletSDK.getInstance().getAllTxRecordBeansByAddress(mAddress);
        if (mRecordBeanListLocalTemp == null) {
            return;
        }
        int size = mRecordBeanListLocalTemp.size();
        if (size > 0) {
            Collections.sort(mRecordBeanListLocalTemp);
            for (int i = 0; i < size; i++) {
                LoggerUtils.i("txRecordBean " + mRecordBeanListLocalTemp.get(i).toString());
                mTxRecordBeanList.add(mRecordBeanListLocalTemp.get(i));
            }
            adapterLv.notifyDataSetChanged();
        }

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
    public void OnTxRecordSuccess(TxRecordBean txRecordBean) {
        if (txRecordBean != null) {
            int count = mTxRecordBeanList.size();
            for (int i = 0; i < count; i++) {
                LoggerUtils.i("txRecordBean " + txRecordBean.toString());
                IONCWalletSDK.getInstance().updateTxRecordBean(mTxRecordBeanList.get(i));
            }
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
        int size = mRecordBeanListLocalTemp.size();
        int refresh = 0;
        for (int i = 0; i < size; i++) {
            if (DEFAULT_TRANSCATION_NIMBER.equals(mRecordBeanListLocalTemp.get(i).getBlockNumber())) {
                LoggerUtils.i("iiii" + i);
                IONCWalletSDK.getInstance().ethTransaction(mNodeIONC
                        , mRecordBeanListLocalTemp.get(i).getHash()
                        , mRecordBeanListLocalTemp.get(i)
                        , this);
                refresh++;
            }
        }
        if (refresh == 0) {
            LoggerUtils.i("无刷新");
            mSmartRefreshLayout.finishRefresh();
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


    public interface OnDataChangedCallback {
        void onDataChange(Context context, TxRecordBean txRecordBean);
    }
}
