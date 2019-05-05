package org.ionchain.wallet.mvp.view.activity.transaction;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import org.ionc.wallet.adapter.CommonAdapter;
import org.ionc.wallet.bean.TxRecoderBean;
import org.ionc.wallet.callback.OnTxRecoderCallback;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecoderViewHelper;
import org.ionchain.wallet.mvp.callback.OnLoadingView;
import org.ionchain.wallet.mvp.presenter.Presenter;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class TxRecordActivity extends AbsBaseActivity implements OnTxRecoderCallback, OnLoadingView {
    private ListView tx_recoder_lv;
    private CommonAdapter adapterLv;
    private List<TxRecoderBean.DataBean.ItemBean> itemBeans;
    private String address;
    private Presenter presenter;

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        presenter = new Presenter();
        presenter.getTxRecord("3", address, "1", "10", this);
    }

    @Override
    protected void initView() {
        mImmersionBar.titleView(R.id.toolbarlayout).statusBarDarkFont(true).execute();
        tx_recoder_lv = findViewById(R.id.tx_recoder_lv);
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
    public void onTxRecordSuccess(ArrayList<TxRecoderBean.DataBean.ItemBean> beans) {
        itemBeans.addAll(beans);
        adapterLv.notifyDataSetChanged();
    }

    @Override
    public void onTxRecordFailure(String error) {
        ToastUtil.showShortToast(error);
    }

    @Override
    public void onLoadStart() {
        showProgress("正在加载...");
    }

    @Override
    public void onLoadFinish() {
        hideProgress();
    }
}
