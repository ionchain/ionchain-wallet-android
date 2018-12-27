package com.ionc.wallet.sdk.adapterhelper;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.ionc.wallet.sdk.R;
import com.ionc.wallet.sdk.adapter.IViewHolder;
import com.ionc.wallet.sdk.adapter.IViewHolderHelper;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.utils.Logger;

import java.util.List;

/**
 * user: binny
 * date:2018/12/24
 */
public class AllWalletViewHepler implements IViewHolderHelper<AllWalletViewHolder, WalletBean> {

    private OnAllWalletItemClickedListener mListener;

    public AllWalletViewHepler(OnAllWalletItemClickedListener listener) {
        mListener = listener;
    }

    @Override
    public IViewHolder initItemViewHolder(AllWalletViewHolder viewHolder, View convertView) {
        viewHolder = new AllWalletViewHolder();
        viewHolder.mName = convertView.findViewById(R.id.name);
        viewHolder.count = convertView.findViewById(R.id.count);
        viewHolder.mAddress = convertView.findViewById(R.id.address);
        viewHolder.all_wallet_ll = convertView.findViewById(R.id.all_wallet_ll);
        return viewHolder;
    }

    @Override
    public void bindListDataToView(Context context, final List<WalletBean> iBaseBeanList, AllWalletViewHolder viewHolder, final int position) {
        Logger.i(iBaseBeanList.get(position).getName() + "ffffffff");
        viewHolder.mName.setText(iBaseBeanList.get(position).getName());
        if (!TextUtils.isEmpty(iBaseBeanList.get(position).getBalance())) {
            viewHolder.count.setText(iBaseBeanList.get(position).getBalance());

        }
        viewHolder.mAddress.setText(iBaseBeanList.get(position).getAddress());
        viewHolder.all_wallet_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResult(iBaseBeanList.get(position).getName(), iBaseBeanList.get(position).getKeystore(),iBaseBeanList.get(position).getBalance());
            }
        });

    }

    public interface OnAllWalletItemClickedListener {
        void onResult(String address, String ketstroy,String sum);
    }
}
