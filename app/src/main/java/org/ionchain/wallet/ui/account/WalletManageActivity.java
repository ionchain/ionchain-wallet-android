package org.ionchain.wallet.ui.account;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.OnRefreshLoadmoreListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adpter.WalletManageAdapter;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletActivity;
import org.ionchain.wallet.ui.wallet.ImprotWalletActivity;
import org.ionchain.wallet.ui.wallet.ModifyWalletActivity;

import java.util.List;

import butterknife.BindView;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;

public class WalletManageActivity extends BaseActivity implements BGAOnRVItemClickListener,OnRefreshLoadmoreListener {

    @BindView(R.id.srl)
    SmartRefreshLayout srl;

    @BindView(R.id.dataRv)
    RecyclerView dataRv;

    private WalletManageAdapter walletManageAdapter;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {
            switch (what) {
//                case R.id.navigationBack:
//                    finish();
//                    break;
                case Comm.modify_wallet_refresh_type:
                    srl.autoRefresh();
                    break;
                case R.id.createBtn:
                    transfer(CreateWalletActivity.class,Comm.JUMP_PARM_ISADDMODE,true);
                    break;
                case R.id.importBtn:
                    transfer(ImprotWalletActivity.class,Comm.JUMP_PARM_ISADDMODE,true);

                    break;
                case 0:
                    dismissProgressDialog();
                    if (obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel) obj;
                    if (!verifyStatus(responseModel)) {
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar = false;
        setContentView(R.layout.activity_wallet_manage);
        mImmersionBar.titleBar(getViewById(R.id.back))
                .statusBarDarkFont(true)
                .init();
        dataRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    protected void setListener() {
        setOnClickListener(R.id.createBtn);
        setOnClickListener(R.id.importBtn);
        walletManageAdapter.setOnRVItemClickListener(this);
        srl.setOnRefreshLoadmoreListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        walletManageAdapter = new WalletManageAdapter(dataRv);
        dataRv.setAdapter(walletManageAdapter);
        mImmersionBar.titleBar(R.id.back).init();

//        List<WalletBean> walletlist =  WalletDaoTools.getAllWallet();
//
//        walletManageAdapter.setData(walletlist);
    }

    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.mipmap.arrow_back_blue;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_wallet_manage;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {

        WalletBean wallet = walletManageAdapter.getItem(position);

        transfer(ModifyWalletActivity.class,Comm.SERIALIZABLE_DATA,wallet);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        srl.finishLoadmore();

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        srl.finishRefresh();
        List<WalletBean> walletlist =  WalletDaoTools.getAllWallet();

        walletManageAdapter.setData(walletlist);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<WalletBean> walletlist =  WalletDaoTools.getAllWallet();

        walletManageAdapter.setData(walletlist);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }
}
