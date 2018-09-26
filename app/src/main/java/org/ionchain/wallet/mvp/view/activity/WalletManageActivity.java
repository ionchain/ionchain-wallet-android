package org.ionchain.wallet.mvp.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.OnRefreshLoadmoreListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adpter.WalletManageAdapter;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.mvp.view.activity.wallet.CreateWalletActivity;
import org.ionchain.wallet.mvp.view.activity.wallet.ImportWalletActivity;
import org.ionchain.wallet.mvp.view.activity.wallet.ModifyWalletActivity;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;

public class WalletManageActivity extends AbsBaseActivity implements BGAOnRVItemClickListener,OnRefreshLoadmoreListener {


    private WalletManageAdapter walletManageAdapter;
    private TextView back;
    private SmartRefreshLayout srl;
    private RecyclerView dataRv;
    private LinearLayout bottomLayout;
    private Button importBtn;
    private Button createBtn;


    private void findViews() {
        back = findViewById( R.id.back );
        srl = (SmartRefreshLayout)findViewById( R.id.srl );
        dataRv = (RecyclerView)findViewById( R.id.dataRv );
        bottomLayout = (LinearLayout)findViewById( R.id.bottom_layout );
        importBtn = (Button)findViewById( R.id.importBtn );
        createBtn = (Button)findViewById( R.id.createBtn );
    }




    @Override
    protected void initData() {
        walletManageAdapter = new WalletManageAdapter(dataRv);
        dataRv.setAdapter(walletManageAdapter);
    }

    @Override
    protected void setListener() {
        walletManageAdapter.setOnRVItemClickListener(this);
        srl.setOnRefreshLoadmoreListener(this);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(CreateWalletActivity.class,Comm.JUMP_PARM_ISADDMODE,true);
            }
        });

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ImportWalletActivity.class,Comm.JUMP_PARM_ISADDMODE,true);
            }
        });

    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleBar(R.id.back)
                .statusBarDarkFont(true)
                .execute();
        dataRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
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
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {

        WalletBean wallet = walletManageAdapter.getItem(position);

        skip(ModifyWalletActivity.class,Comm.SERIALIZABLE_DATA,wallet);
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


}
