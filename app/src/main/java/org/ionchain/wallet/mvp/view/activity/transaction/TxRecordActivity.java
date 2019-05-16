package org.ionchain.wallet.mvp.view.activity.transaction;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.utils.Logger;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecoderViewHelper;
import org.ionchain.wallet.bean.TxRecoderBean;
import org.ionchain.wallet.mvp.callback.OnLoadingView;
import org.ionchain.wallet.mvp.callback.OnTxRecordCallback;
import org.ionchain.wallet.mvp.presenter.transcation.TxRecordPresenter;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.List;

public class TxRecordActivity extends AbsBaseActivity implements OnTxRecordCallback, OnLoadingView, OnRefreshListener {
    private CommonAdapter adapterLv;
    private List<TxRecoderBean.DataBean.ItemBean> itemBeans;
    private String address;
    private TxRecordPresenter presenter;
    private SmartRefreshLayout mSmartRefreshLayout;

    @Override
    protected void initData() {
        presenter = new TxRecordPresenter();
        presenter.getTxRecord("3", address, "1", "10", this);
    }

    @Override
    protected void initView() {
        mImmersionBar.titleView(R.id.toolbarlayout).statusBarDarkFont(true).execute();
        ListView tx_recoder_lv = findViewById(R.id.tx_recoder_lv);
        mSmartRefreshLayout = findViewById(R.id.refresh_tx_record);
        mSmartRefreshLayout.setOnRefreshListener(this);
        adapterLv = new CommonAdapter(this, itemBeans, R.layout.item_txrecoder, new TxRecoderViewHelper());
        tx_recoder_lv.setAdapter(adapterLv);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void handleIntent(Intent intent) {
        address = intent.getStringExtra("address");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_recoder;
    }



    @Override
    public void onTxRecordSuccess(List<TxRecoderBean.DataBean.ItemBean> beans) {
        itemBeans.addAll(beans);
        adapterLv.notifyDataSetChanged();
    }

    @Override
    public void onTxRecordFailure(String error) {
        ToastUtil.showShortToast(error);
    }

    @Override
    public void onLoadStart() {
        Logger.i("正在获取");
    }

    @Override
    public void onLoadFinish() {
        Logger.i("获取完成");
        mSmartRefreshLayout.finishRefresh();
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        Logger.i("刷新");
        presenter.getTxRecord("3", address, "1", "10", this);
    }
}
