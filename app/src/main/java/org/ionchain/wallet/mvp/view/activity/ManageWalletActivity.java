package org.ionchain.wallet.mvp.view.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.adapter.CommonAdapter;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.OnRefreshLoadmoreListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.walletmanager.ManagerWalletHelper;
import org.ionchain.wallet.mvp.view.activity.createwallet.CreateWalletActivity;
import org.ionchain.wallet.mvp.view.activity.importmode.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.activity.modify.ModifyAndExportWalletActivity;
import org.ionchain.wallet.mvp.view.activity.sdk.SDKCreateActivity;
import org.ionchain.wallet.mvp.view.activity.sdk.SDKSelectCreateModeWalletActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import java.util.ArrayList;
import java.util.List;

import static org.ionchain.wallet.constant.ConstantParams.JUMP_PARM_ISADDMODE;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ManageWalletActivity extends AbsBaseActivity implements OnRefreshLoadmoreListener, ManagerWalletHelper.OnWalletManagerItemClickedListener {


    private SmartRefreshLayout srl;
    private ListView listview;
    private Button importBtn;
    private Button createBtn;
    private CommonAdapter mAdapter;
    private List<WalletBean> mWalletBeans = new ArrayList<>();

    private void findViews() {
        srl = (SmartRefreshLayout) findViewById(R.id.srl);
        listview = (ListView) findViewById(R.id.listview);
        importBtn = (Button) findViewById(R.id.importBtn);
        createBtn = (Button) findViewById(R.id.createBtn);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void initData() {
        mWalletBeans = IONCWalletSDK.getInstance().getAllWallet();
        mAdapter = new CommonAdapter(this, mWalletBeans, R.layout.item_wallet_manager_layout, new ManagerWalletHelper(this));
        listview.setAdapter(mAdapter);
    }

    @Override
    protected void setListener() {
        srl.setOnRefreshLoadmoreListener(this);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewAlphaAnimation(createBtn);
                if (App.SDK_Debug) {
                    skip(SDKCreateActivity.class, JUMP_PARM_ISADDMODE, true);//
                } else {
                    skip(CreateWalletActivity.class, JUMP_PARM_ISADDMODE, true);
                }
            }
        });

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewAlphaAnimation(importBtn);
                if (App.SDK_Debug) {
                    skip(SDKSelectCreateModeWalletActivity.class);
                }else {
                    skip(SelectImportModeActivity.class);//
                }
            }
        });

    }

    @Override
    protected void initView() {
        findViews();
        getMImmersionBar().titleView(R.id.toolbarlayout)
                .statusBarDarkFont(true)
                .execute();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_manage;
    }


    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        srl.finishLoadmore();

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        srl.finishRefresh();
        mWalletBeans.clear();
        mWalletBeans.addAll(IONCWalletSDK.getInstance().getAllWallet());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mWalletBeans.clear();
//        mWalletBeans = WalletDaoTools.getAllWallet();
//        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClicked(int position) {
        WalletBean wallet = (WalletBean) mAdapter.getItem(position);
        skip(ModifyAndExportWalletActivity.class, SERIALIZABLE_DATA, wallet);
    }
}
