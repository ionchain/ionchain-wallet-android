package org.ionchain.wallet.adapter.txrecoder;

import android.content.Context;
import android.view.View;

import org.ionc.wallet.sdk.adapter.IViewHolder;
import org.ionc.wallet.sdk.adapter.IViewHolderHelper;
import org.ionc.wallet.sdk.bean.TxRecoderBean;

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

    @Override
    public void bindListDataToView(Context context, List<TxRecoderBean.DataBean.ItemBean> iBaseBeanList, TxRecorderViewHolder viewHolder, int position) {
        viewHolder.txHash.setText("交易哈希 : " + iBaseBeanList.get(position).getHash());
        viewHolder.block.setText("交易区块 : " + iBaseBeanList.get(position).getBlockNumber());
        viewHolder.from.setText("转出地址 : " + iBaseBeanList.get(position).getTx_from());
        viewHolder.to.setText("转入地址 ： " + iBaseBeanList.get(position).getTx_to());
        viewHolder.value.setText("转账金额 : " + Convert.fromWei(iBaseBeanList.get(position).getValue(), Convert.Unit.ETHER) + " IONC");
        viewHolder.txFee.setText("交易费 : " + iBaseBeanList.get(position).getTxFee() + " IONC");
    }


}
