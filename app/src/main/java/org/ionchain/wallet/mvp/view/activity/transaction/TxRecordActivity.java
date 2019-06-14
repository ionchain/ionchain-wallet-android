package org.ionchain.wallet.mvp.view.activity.transaction;

import android.content.Intent;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordViewHelper;
import org.ionchain.wallet.mvp.callback.OnLoadingView;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TxRecordActivity extends AbsBaseActivity implements OnLoadingView, OnRefreshListener, OnLoadMoreListener,
        org.ionc.wallet.callback.OnTxRecordCallback {
    private CommonAdapter adapterLv;
    private List<TxRecordBean> mTxRecordBeanList = new ArrayList<>();
    private String mAddress;
    private SmartRefreshLayout mSmartRefreshLayout;
    private int totalCount = 0;
    private int pageNum = 2;
    List<TxRecordBean> mRecordBeanListLocalTemp = new ArrayList<>();

    @Override
    protected void initData() {
        mRecordBeanListLocalTemp = IONCWalletSDK.getInstance().getAllTxRecordBeans();
        if (mRecordBeanListLocalTemp == null) {
            return;
        }
        int size = mRecordBeanListLocalTemp.size();
        if (size > 0) {
            Collections.sort(mRecordBeanListLocalTemp);
            for (int i = 0; i < size; i++) {
                if ("-1".equals(mRecordBeanListLocalTemp.get(i).getBlockNumber())) {
                    IONCWalletSDK.getInstance().ethTransaction("http://192.168.0.104:7545"
                            , mRecordBeanListLocalTemp.get(i).getHash()
                            , mRecordBeanListLocalTemp.get(i)
                            , this);
                } else {
                    mTxRecordBeanList.add(mRecordBeanListLocalTemp.get(i));
                    IONCWalletSDK.getInstance().updateTxRecordBean(mRecordBeanListLocalTemp.get(i));
                }
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
        adapterLv = new CommonAdapter(this, mTxRecordBeanList, R.layout.item_txrecoder, new TxRecordViewHelper());
        tx_record_lv.setAdapter(adapterLv);
        findViewById(R.id.back).setOnClickListener(v -> finish());
    }

    @Override
    protected void handleIntent(Intent intent) {
        mAddress = intent.getStringExtra("mAddress");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_recoder;
    }

    @Override
    public void OnTxRecordSuccess(TxRecordBean txRecordBean) {
        if (txRecordBean != null) {
            mTxRecordBeanList.add(txRecordBean);
            adapterLv.notifyDataSetChanged();
        }
    }

    @Override
    public void onTxRecordFailure(String error) {
        ToastUtil.showShortToast(error);
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
        mRecordBeanListLocalTemp = IONCWalletSDK.getInstance().getAllTxRecordBeans();
        int size = mRecordBeanListLocalTemp.size();
        for (int i = 0; i < size; i++) {
            IONCWalletSDK.getInstance().ethTransaction("http://192.168.0.104:7545"
                    , mRecordBeanListLocalTemp.get(i).getHash()
                    , mRecordBeanListLocalTemp.get(i)
                    , this);
        }
    }

    /**
     * @param refreshLayout 加载更多
     */
    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
    }
//
//    @Override
//    public void onBackPressed() {
//        OkGo.cancelTag(OkGo.getInstance().getOkHttpClient(), this);
//    }
}
