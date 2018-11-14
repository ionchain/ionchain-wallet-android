package org.ionchain.wallet.adapter.txrecoder;

import android.content.Context;
import android.view.View;

import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.iinterface.IViewHolder;
import org.ionchain.wallet.adapter.iinterface.IViewHolderHelper;
import org.ionchain.wallet.bean.TxRecoderBean;

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

    @Override
    public void bindListDataToView(Context context, List<TxRecoderBean.DataBean.ItemBean> iBaseBeanList, TxRecorderViewHolder viewHolder, int position) {
        viewHolder.txHash.setText("TxHash : " + iBaseBeanList.get(position).getHash());
        viewHolder.block.setText("Block : " + iBaseBeanList.get(position).getBlockNumber());
        viewHolder.from.setText("From : " + iBaseBeanList.get(position).getTx_from());
        viewHolder.to.setText("To ： " + iBaseBeanList.get(position).getTx_to());
        viewHolder.value.setText("Value : " + iBaseBeanList.get(position).getValue() + " IONC");
        viewHolder.txFee.setText("TxFee : " + iBaseBeanList.get(position).getTxFee() + " IONC");
    }


}
