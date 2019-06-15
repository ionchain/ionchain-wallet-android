package org.ionchain.wallet.adapter.txrecoder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import org.ionc.wallet.adapter.IViewHolder;
import org.ionc.wallet.adapter.IViewHolderHelper;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.transaction.TxRecordActivity;

import java.util.List;

/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:30
 */
public class TxRecordViewHelper implements IViewHolderHelper<TxRecorderViewHolder, TxRecordBean>, TxRecordActivity.OnDataChangedCallback {
    private TxRecorderViewHolder mTxRecorderViewHolder;

    @Override
    public IViewHolder initItemViewHolder(TxRecorderViewHolder viewHolder, View convertView) {
        viewHolder = new TxRecorderViewHolder();
        viewHolder.block = convertView.findViewById(R.id.tx_block);
        viewHolder.from = convertView.findViewById(R.id.tx_from);
        viewHolder.to = convertView.findViewById(R.id.tx_to);
        viewHolder.txFee = convertView.findViewById(R.id.tx_fee);
        viewHolder.value = convertView.findViewById(R.id.tx_value);
        viewHolder.txHash = convertView.findViewById(R.id.tx_hash);
        mTxRecorderViewHolder = viewHolder;
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindListDataToView(Context context, List<TxRecordBean> iBaseBeanList, TxRecorderViewHolder viewHolder, int position) {
        viewHolder.txHash.setText(context.getResources().getString(R.string.tx_hash) + iBaseBeanList.get(position).getHash());
        viewHolder.block.setText(context.getResources().getString(R.string.tx_block) + iBaseBeanList.get(position).getBlockNumber());
        viewHolder.from.setText(context.getResources().getString(R.string.tx_out_addr) + iBaseBeanList.get(position).getFrom());
        viewHolder.to.setText(context.getResources().getString(R.string.tx_in_addr) + iBaseBeanList.get(position).getTo());
        viewHolder.value.setText(context.getResources().getString(R.string.tx_amount) + iBaseBeanList.get(position).getValue() + " IONC");
        viewHolder.txFee.setText(context.getResources().getString(R.string.tx_fee) + iBaseBeanList.get(position).getGas() + " IONC");
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onDataChange(Context context, TxRecordBean txRecordBean) {
        LoggerUtils.i("contexttxRecordBean", txRecordBean.toString());
        mTxRecorderViewHolder.txHash.setText(context.getResources().getString(R.string.tx_hash) + txRecordBean.getHash());
        mTxRecorderViewHolder.block.setText(context.getResources().getString(R.string.tx_block) + txRecordBean.getBlockNumber());
        mTxRecorderViewHolder.from.setText(context.getResources().getString(R.string.tx_out_addr) + txRecordBean.getFrom());
        mTxRecorderViewHolder.to.setText(context.getResources().getString(R.string.tx_in_addr) + txRecordBean.getTo());
        mTxRecorderViewHolder.value.setText(context.getResources().getString(R.string.tx_amount) + txRecordBean.getValue() + " IONC");
        mTxRecorderViewHolder.txFee.setText(context.getResources().getString(R.string.tx_fee) + txRecordBean.getGas() + " IONC");
    }
}
