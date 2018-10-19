package org.ionchain.wallet.adapter.morewallet;

import android.content.Context;
import android.view.View;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.iinterface.IViewHolder;
import org.ionchain.wallet.adapter.iinterface.IViewHolderHelper;
import org.ionchain.wallet.bean.WalletBean;

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
        viewHolder.mWalletImg.setImageResource(App.Companion.getSRandomHeaderMore()[iBaseBeanList.get(pos).getMIconIdex()]);
    }
}
