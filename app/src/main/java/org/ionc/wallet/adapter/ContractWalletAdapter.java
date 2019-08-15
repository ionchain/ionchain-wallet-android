package org.ionc.wallet.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ionc.wallet.App;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;

import java.util.List;

public class ContractWalletAdapter extends BaseQuickAdapter<WalletBeanNew, BaseViewHolder> {
    public ContractWalletAdapter(int layoutResId, @Nullable List<WalletBeanNew> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WalletBeanNew item) {
        LoggerUtils.e("contract = " + item.getAddress());
        String balance = item.getContracBalance();
        if (TextUtils.isEmpty(balance)) {
            balance = "0";
        }
        helper
                .setText(R.id.item_ex_wallet_name, item.getName())
                .setText(R.id.item_ex_wallet_balance, balance)
                .setImageResource(R.id.item_ex_wallet_img, App.sRandomHeaderMore[item.getMIconIndex()]);
    }
}
