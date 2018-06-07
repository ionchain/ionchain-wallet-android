package org.ionchain.wallet.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.model.ModelLoader;
import com.fast.lib.immersionbar.ImmersionBar;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.fast.lib.widget.recyclerview.LibraryRecyclerView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adpter.WalletManageAdapter;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletActivity;
import org.ionchain.wallet.ui.wallet.ImprotWalletActivity;
import org.ionchain.wallet.ui.wallet.ModifyWalletActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;

public class WalletManageActivity extends BaseActivity implements BGAOnRVItemClickListener {

    @BindView(R.id.recyclerView)
    LibraryRecyclerView recyclerView;

    private WalletManageAdapter walletManageAdapter;
    private List<Wallet> walletList = new ArrayList<>();

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {
            Intent intent =null;
            switch (what) {
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.createBtn:
                    intent = new Intent();
                    intent.putExtra(Comm.JUMP_PARM_ISADDMODE, true);
                    intent.setClass(WalletManageActivity.this, CreateWalletActivity.class);//从哪里跳到哪里
                    WalletManageActivity.this.startActivity(intent);

                    break;
                case R.id.importBtn:
                    intent = new Intent();
                    intent.putExtra(Comm.JUMP_PARM_ISADDMODE, true);
                    intent.setClass(WalletManageActivity.this, ImprotWalletActivity.class);//从哪里跳到哪里
                    WalletManageActivity.this.startActivity(intent);

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
        setContentView(R.layout.activity_wallet_manage);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        walletManageAdapter = new WalletManageAdapter(recyclerView);

        loadData();
        walletManageAdapter.addNewData(walletList);
        recyclerView.setAdapter(walletManageAdapter);
        walletManageAdapter.setOnRVItemClickListener(this);
    }

    private void loadData() {
        Wallet wallet = new Wallet();
        wallet.setName("钱包管理测试数据");

        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
        walletList.add(wallet);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.createBtn);
        setOnClickListener(R.id.importBtn);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_wallet_manage;
    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        transfer(ModifyWalletActivity.class);
    }
}
