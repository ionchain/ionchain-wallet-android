package org.ionchain.wallet.mvp.view.activity.transaction;

import android.content.Intent;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecoderViewHelper;
import org.ionchain.wallet.bean.TxRecoderBean;
import org.ionchain.wallet.mvp.callback.OnLoadingView;
import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;
import org.ionchain.wallet.mvp.presenter.transcation.TxRecordPresenter;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class TxRecordActivity extends AbsBaseActivity implements OnTxRecordCallback, OnLoadingView, OnRefreshListener, OnLoadMoreListener {
    private CommonAdapter adapterLv;
    private List<TxRecoderBean.DataBean.ItemBean> itemBeans = new ArrayList<>();
    private String address;
    private TxRecordPresenter presenter;
    private SmartRefreshLayout mSmartRefreshLayout;
    private int totalCount = 0;
    private int pageNum = 2;

    @Override
    protected void initData() {
        presenter = new TxRecordPresenter();
        presenter.getTxRecord(false, "3", address, "1", "10", this);
    }

    @Override
    protected void initView() {
        mImmersionBar.titleView(R.id.toolbarlayout).statusBarDarkFont(true).execute();
        ListView tx_record_lv = findViewById(R.id.tx_recoder_lv);
        mSmartRefreshLayout = findViewById(R.id.refresh_tx_record);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        mSmartRefreshLayout.setEnableLoadMore(true);
        mSmartRefreshLayout.setEnableAutoLoadMore(false);
        adapterLv = new CommonAdapter(this, itemBeans, R.layout.item_txrecoder, new TxRecoderViewHelper());
        tx_record_lv.setAdapter(adapterLv);
        findViewById(R.id.back).setOnClickListener(v -> finish());
    }

    @Override
    protected void handleIntent(Intent intent) {
        address = intent.getStringExtra("address");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_recoder;
    }


    /**
     * @param beans 处理刷新或加载更多的数据
     */
    @Override
    public void onTxRecordRefreshSuccess(TxRecoderBean.DataBean beans) {
        if (beans.getTotalCount() == totalCount) {
            ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_no_new_data));
            return;
        }
        if (totalCount == 0) {
            itemBeans.addAll(beans.getData());
        } else {
            int newDataCount = beans.getTotalCount() - totalCount;
            for (int i = 0; i < newDataCount; i++) {
                itemBeans.add(beans.getData().get(i));//把新数据加载列表的前面
            }
        }
        totalCount = beans.getTotalCount();
        adapterLv.notifyDataSetChanged();
    }

    @Override
    public void onTxRecordLoadMoreSuccess(List<TxRecoderBean.DataBean.ItemBean> beans) {
        if (beans.size() <= 0) {
            ToastUtil.showToastLonger(getAppString(R.string.smart_refresh_footer_no_more));
        } else {
            pageNum++;
            int count = beans.size();
            LoggerUtils.i("加载更多：size = " + count);
            for (int i = 0; i < count; i++) {
                itemBeans.add(itemBeans.size(), beans.get(i));
            }
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
        presenter.getTxRecord(false, "3", address, "1", "10", this);
    }

    /**
     * @param refreshLayout 加载更多
     */
    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        presenter.getTxRecord(true, "3", address, String.valueOf(pageNum), "10", this);
    }
}
