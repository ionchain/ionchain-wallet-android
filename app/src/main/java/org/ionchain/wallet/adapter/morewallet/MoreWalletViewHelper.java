package org.ionchain.wallet.adapter.morewallet;

import android.content.Context;
import android.view.View;

import com.ionc.wallet.sdk.adapter.IViewHolder;
import com.ionc.wallet.sdk.adapter.IViewHolderHelper;
import com.ionc.wallet.sdk.bean.WalletBean;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;

import java.util.List;

/**
 * USER: binny
 * DATE: 2018/9/12
 * 描述: 辅助类 绑定数据
 */
public class MoreWalletViewHelper implements IViewHolderHelper<MoreWalletViewHolder, WalletBean> {
    @Override
    public IViewHolder initItemViewHolder(MoreWalletViewHolder viewHolder, View convertView) {
        viewHolder = new MoreWalletViewHolder();
        viewHolder.mWalletName = convertView.findViewById(R.id.more_wallet_text);
        viewHolder.mWalletImg = convertView.findViewById(R.id.more_wallet_img);
        return viewHolder;
    }

    @Override
    public void bindListDataToView(Context context, List<WalletBean> iBaseBeanList, MoreWalletViewHolder viewHolder, int pos) {
        viewHolder.mWalletName.setText(iBaseBeanList.get(pos).getName());
        viewHolder.mWalletImg.setImageResource(App.sRandomHeaderMore[iBaseBeanList.get(pos).getMIconIdex()]);
    }
}
