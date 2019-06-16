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
import static org.ionchain.wallet.utils.UrlUtils.getHostNode;

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
    List<TxRecordBean> mRecordBeanListTemp = new ArrayList<>();
    /**
     * 未获取交易信息的本地缓存,缓存hash值
     */
    List<TxRecordBean> mTxHashUnpackedTemp = new ArrayList<>();
    /**
     * 例子链接点
     */
    private String mNodeIONC = getHostNode();
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
        mRecordBeanListTemp = IONCWalletSDK.getInstance().getAllTxRecordBeansByAddress(mAddress);
        if (mRecordBeanListTemp == null || mRecordBeanListTemp.size() == 0) {
            ToastUtil.showToastLonger(getAppString(R.string.tx_record_none));
            return;
        }
        int size = mRecordBeanListTemp.size();
        Collections.sort(mRecordBeanListTemp);
        for (int i = 0; i < size; i++) {
            LoggerUtils.i("txRecordBean " + mRecordBeanListTemp.get(i).toString());
            mTxRecordBeanList.add(mRecordBeanListTemp.get(i));
            if (DEFAULT_TRANSCATION_BLOCK_NUMBER.equals(mRecordBeanListTemp.get(i).getBlockNumber())) {
                LoggerUtils.i("unpacked" + mRecordBeanListTemp.get(i).toString());
                mTxHashUnpackedTemp.add(mRecordBeanListTemp.get(i));
            }
        }
        int tempHashSize = mTxHashUnpackedTemp.size();
//        if (tempHashSize > 0) {
//            for (int i = 0; i < tempHashSize; i++) {
//                IONCWalletSDK.getInstance().ethTransaction(mNodeIONC
//                        , mTxHashUnpackedTemp.get(i)
//                        , mRecordBeanListTemp.get(i)
//                        , this, i);
//            }
//        }
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
    public void OnTxRecordSuccess(TxRecordBean txRecordBean) {
        LoggerUtils.i("执行完成 " + txRecordBean.toString());
        mSmartRefreshLayout.finishRefresh();

        if (mTxHashUnpackedTemp.contains(txRecordBean)) {
            mTxHashUnpackedTemp.remove(txRecordBean);//移除已打包
            LoggerUtils.i("txRecordBean " + txRecordBean.toString());
            IONCWalletSDK.getInstance().updateTxRecordBean(txRecordBean);
            Collections.sort(mTxRecordBeanList);
            adapterLv.notifyDataSetChanged();
        }


    }

    @Override
    public void onTxRecordFailure(String error, TxRecordBean recordBean) {
        LoggerUtils.i("执行失败 " + recordBean);
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
        int size = mTxHashUnpackedTemp.size();
        if (size == 0) {
            LoggerUtils.i("无刷新");
            mSmartRefreshLayout.finishRefresh();
            return;
        }
        LoggerUtils.i("需更新 " + size);
        for (int i = 0; i < size; i++) {
            IONCWalletSDK.getInstance().ethTransaction(mNodeIONC
                    , mRecordBeanListTemp.get(i).getHash()
                    , mRecordBeanListTemp.get(i)
                    , this);
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
