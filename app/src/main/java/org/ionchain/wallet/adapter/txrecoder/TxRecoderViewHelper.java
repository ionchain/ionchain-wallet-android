package org.ionchain.wallet.adapter.txrecoder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import org.ionc.wallet.adapter.IViewHolder;
import org.ionc.wallet.adapter.IViewHolderHelper;
import org.ionc.wallet.bean.TxRecoderBean;

import org.ionchain.wallet.R;
import org.web3j.utils.Convert;

import java.util.List;

/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:30
 */
public class TxRecoderViewHelper implements IViewHolderHelper<TxRecorderViewHolder, TxRecoderBean.DataBean.ItemBean> {
    @Override
    public IViewHolder initItemViewHolder(TxRecorderViewHolder viewHolder, View convertView) {
        viewHolder = new TxRecorderViewHolder();
        viewHolder.block = convertView.findViewById(R.id.tx_block);
        viewHolder.from = convertView.findViewById(R.id.tx_from);
        viewHolder.to = convertView.findViewById(R.id.tx_to);
        viewHolder.txFee = convertView.findViewById(R.id.tx_fee);
        viewHolder.value = convertView.findViewById(R.id.tx_value);
        viewHolder.txHash = convertView.findViewById(R.id.tx_hash);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindListDataToView(Context context, List<TxRecoderBean.DataBean.ItemBean> iBaseBeanList, TxRecorderViewHolder viewHolder, int position) {
        viewHolder.txHash.setText(context.getResources().getString(R.string.tx_hash) + iBaseBeanList.get(position).getHash());
        viewHolder.block.setText(context.getResources().getString(R.string.tx_block) + iBaseBeanList.get(position).getBlockNumber());
        viewHolder.from.setText(context.getResources().getString(R.string.tx_out_addr) + iBaseBeanList.get(position).getTx_from());
        viewHolder.to.setText(context.getResources().getString(R.string.tx_in_addr) + iBaseBeanList.get(position).getTx_to());
        viewHolder.value.setText(context.getResources().getString(R.string.tx_amount) + Convert.fromWei(iBaseBeanList.get(position).getValue(), Convert.Unit.ETHER) + " IONC");
        viewHolder.txFee.setText(context.getResources().getString(R.string.tx_fee) + iBaseBeanList.get(position).getTxFee() + " IONC");
    }


}
