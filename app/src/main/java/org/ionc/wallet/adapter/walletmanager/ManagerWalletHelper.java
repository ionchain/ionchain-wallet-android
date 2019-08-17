package org.ionc.wallet.adapter.walletmanager;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.ionc.ionclib.bean.WalletBeanNew;
import org.ionc.wallet.App;
import org.ionc.wallet.adapter.IViewHolder;
import org.ionc.wallet.adapter.IViewHolderHelper;
import org.ionchain.wallet.R;

import java.util.List;

public class ManagerWalletHelper implements IViewHolderHelper<ManagerWalletHolder, WalletBeanNew> {
    private OnWalletManagerItemClickedListener mOnClickListener;

    public ManagerWalletHelper(OnWalletManagerItemClickedListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public IViewHolder initItemViewHolder(ManagerWalletHolder viewHolder, View convertView) {
        viewHolder = new ManagerWalletHolder();
        viewHolder.mWalletImg = convertView.findViewById(R.id.manager_wallet_img);
        viewHolder.mManagerWalletRl = convertView.findViewById(R.id.manager_wallet_rl);
        viewHolder.mWalletName = convertView.findViewById(R.id.manager_wallet_name);
        return viewHolder;
    }

    @Override
    public void bindListDataToView(Context context, List<WalletBeanNew> iBaseBeanList, ManagerWalletHolder viewHolder, final int position) {
        viewHolder.mWalletName.setText(iBaseBeanList.get(position).getName());
        Log.i("dddd", "bindListDataToView: " + iBaseBeanList.get(position).getName());
        viewHolder.mWalletImg.setImageResource(App.sRandomHeader[iBaseBeanList.get(position).getMIconIndex()]);
        viewHolder.mManagerWalletRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onItemClicked(position);
            }
        });
    }

    public interface OnWalletManagerItemClickedListener {
        /**
         * item 事件处理
         *
         * @param position 位置
         */
        void onItemClicked(int position);
    }
}
