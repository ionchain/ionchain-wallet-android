package org.ionchain.wallet.adpter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

public class WalletManageAdapter extends BGARecyclerViewAdapter<WalletBean>{

    public WalletManageAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.layout_wallet_manager_item);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, WalletBean model) {

        if(!TextUtils.isEmpty(model.getName())){
            helper.setText(R.id.walletNameTv,model.getName());
        }else{
            helper.setText(R.id.walletNameTv,"");
        }

        helper.getImageView(R.id.manager_wallet_img).setImageResource(App.sRandomHeaderMore[model.getIconIdex()]);
    }
}
