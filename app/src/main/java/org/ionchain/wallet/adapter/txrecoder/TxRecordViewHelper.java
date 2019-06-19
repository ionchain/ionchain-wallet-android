package org.ionchain.wallet.adapter.txrecoder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import org.ionc.wallet.adapter.IViewHolder;
import org.ionc.wallet.adapter.IViewHolderHelper;
import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.utils.DateUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;

import java.util.List;

import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_BLOCK_NUMBER;

/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:30
 */
public class TxRecordViewHelper implements IViewHolderHelper<TxRecorderViewHolder, TxRecordBean> {
    private OnTxRecordItemClickedListener mClickedListener;

    public TxRecordViewHelper(OnTxRecordItemClickedListener clickedListener) {
        mClickedListener = clickedListener;
    }

    @Override
    public IViewHolder initItemViewHolder(TxRecorderViewHolder viewHolder, View convertView) {
        viewHolder = new TxRecorderViewHolder();
        viewHolder.block = convertView.findViewById(R.id.tx_block);
        viewHolder.tx_record_holder = convertView.findViewById(R.id.tx_record_holder);
        viewHolder.tx_time = convertView.findViewById(R.id.tx_time);
        viewHolder.from = convertView.findViewById(R.id.tx_from);
        viewHolder.to = convertView.findViewById(R.id.tx_to);
        viewHolder.txFee = convertView.findViewById(R.id.tx_fee);
        viewHolder.value = convertView.findViewById(R.id.tx_value);
        viewHolder.txHash = convertView.findViewById(R.id.tx_hash);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindListDataToView(Context context, List<TxRecordBean> iBaseBeanList, TxRecorderViewHolder viewHolder, int position) {
        viewHolder.txHash.setText(context.getResources().getString(R.string.tx_hash) + iBaseBeanList.get(position).getHash());
        String time = DateUtils.getDateToString(Long.parseLong(iBaseBeanList.get(position).getTc_in_out()), Y4M2D2H2M2S2);
        LoggerUtils.i("time = " + time);
        viewHolder.tx_time.setText(context.getResources().getString(R.string.tx_time) + time);
        if (DEFAULT_TRANSCATION_BLOCK_NUMBER.equals(iBaseBeanList.get(position).getBlockNumber())) {
            viewHolder.block.setText(context.getResources().getString(R.string.tx_block_unpacked));
        } else {
            viewHolder.block.setText(context.getResources().getString(R.string.tx_block) + iBaseBeanList.get(position).getBlockNumber());
        }
        viewHolder.from.setText(context.getResources().getString(R.string.tx_out_addr) + iBaseBeanList.get(position).getFrom());
        viewHolder.to.setText(context.getResources().getString(R.string.tx_in_addr) + iBaseBeanList.get(position).getTo());
        viewHolder.value.setText(context.getResources().getString(R.string.tx_amount) + iBaseBeanList.get(position).getValue() + " IONC");
        viewHolder.txFee.setText(context.getResources().getString(R.string.tx_fee) + iBaseBeanList.get(position).getGas() + " IONC");
        viewHolder.tx_record_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickedListener.onTxRecordItemClick(iBaseBeanList.get(position));
            }
        });
    }

    public interface OnTxRecordItemClickedListener {
        void onTxRecordItemClick(TxRecordBean txRecordBean);
    }
}
