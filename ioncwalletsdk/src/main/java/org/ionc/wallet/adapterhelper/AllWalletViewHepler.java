package org.ionc.wallet.adapterhelper;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.ionc.wallet.sdk.R;
import org.ionc.wallet.adapter.IViewHolder;
import org.ionc.wallet.adapter.IViewHolderHelper;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.utils.LoggerUtils;

import java.util.List;

/**
 * user: binny
 * date:2018/12/24
 */
public class AllWalletViewHepler implements IViewHolderHelper<AllWalletViewHolder, WalletBeanNew> {

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
    public void bindListDataToView(Context context, final List<WalletBeanNew> iBaseBeanList, AllWalletViewHolder viewHolder, final int position) {
        LoggerUtils.i("position" + iBaseBeanList.get(position).getName() + "ffffffff");
        viewHolder.mName.setText(iBaseBeanList.get(position).getName());
        if (!TextUtils.isEmpty(iBaseBeanList.get(position).getBalance())) {
            viewHolder.count.setText(iBaseBeanList.get(position).getBalance());

        }
        if (iBaseBeanList.get(position).getChosen()) {
            LoggerUtils.i("position 0 = " + position);
            viewHolder.all_wallet_ll.setBackgroundColor(context.getResources().getColor(R.color.chosen_wallet_color));
        } else {
            LoggerUtils.i("position 1 = " + position);
            viewHolder.all_wallet_ll.setBackgroundColor(Color.WHITE);
        }
        viewHolder.mAddress.setText(iBaseBeanList.get(position).getAddress());
        viewHolder.all_wallet_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = iBaseBeanList.size();
                for (int i = 0; i < size; i++) {
                    if (i == position) {
                        LoggerUtils.i("position 2 = " + position);
                        iBaseBeanList.get(i).setChosen(true);

                    } else {
                        LoggerUtils.i("position 3 = " + position);

                        iBaseBeanList.get(i).setChosen(false);
                    }
                }
                mListener.onItemClick(iBaseBeanList.get(position));
            }
        });

    }

    public interface OnAllWalletItemClickedListener {
        void onItemClick(WalletBeanNew walletBean);
    }
}
