package org.ionchain.wallet.adpter;

import android.support.v7.widget.RecyclerView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.model.Wallet;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

public class WalletManageAdapter extends BGARecyclerViewAdapter<Wallet>{

    public WalletManageAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_wallet_manage);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, Wallet wallet) {
        helper.setText(R.id.walletNameTv,wallet.getName());
    }
}
