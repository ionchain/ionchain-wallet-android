package org.ionc.wallet.view.activity.exchange;

import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.ionc.wallet.adapter.ContractWalletAdapter;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.constant.ConstantParams;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.ionc.wallet.view.base.AbsBaseActivityTitleTwo;
import org.ionc.wallet.web3j.IONCWallet;
import org.ionchain.wallet.R;

import java.util.ArrayList;
import java.util.List;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;

public class ExChangeActivity extends AbsBaseActivityTitleTwo {
    private ContractWalletAdapter mContractWalletAdapter;
    private List<WalletBeanNew> mWalletBeanNewList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ex_change;
    }

    @Override
    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.contract_wallet_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        mContractWalletAdapter = new ContractWalletAdapter(R.layout.item_ex_wallet_list, mWalletBeanNewList);
        mContractWalletAdapter.openLoadAnimation(SCALEIN);
        mContractWalletAdapter.bindToRecyclerView(recyclerView);
        recyclerView.setAdapter(mContractWalletAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        mContractWalletAdapter.setOnItemClickListener((adapter, view, position) -> {
            //跳转到 切换界面
            Intent intent = new Intent(ExChangeActivity.this, ContractWalletDetailActivity.class);
            intent.putExtra(ConstantParams.PARCELABLE_WALLET_BEAN, mWalletBeanNewList.get(position));
            intent.putExtra("pos", position);

            startActivityForResult(intent, 0);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoggerUtils.e("resultCode = " + resultCode);
        if ((resultCode == 10)) {
            if (data != null) {
                if (data.getParcelableExtra(ConstantParams.PARCELABLE_WALLET_BEAN) != null) {
                    int pos = data.getIntExtra("pos", 0);
                    TextView textView = (TextView) mContractWalletAdapter.getViewByPosition(pos, R.id.item_ex_wallet_balance);
                    WalletBeanNew walletBeanNew = data.getParcelableExtra(ConstantParams.PARCELABLE_WALLET_BEAN);
                    textView.setText(walletBeanNew.getContracBalance());
                } else {
                    LoggerUtils.e("wallet = null");
                }
            } else {
                LoggerUtils.e("data==nill");
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mWalletBeanNewList.addAll(IONCWallet.getAllWalletNew());
        if (mWalletBeanNewList.size() > 0) {
            mContractWalletAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showLong("没有钱包");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LoggerUtils.e(" onPause    " + System.currentTimeMillis());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoggerUtils.e("  onStop   " + System.currentTimeMillis());
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.mine_change_main_chain);
    }

}
