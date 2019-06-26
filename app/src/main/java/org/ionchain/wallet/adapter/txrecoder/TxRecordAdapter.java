package org.ionchain.wallet.adapter.txrecoder;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.utils.DateUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;

import java.util.List;

import static org.ionc.wallet.sdk.IONCWalletSDK.TX_SUSPENDED;
import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;
import static org.ionchain.wallet.constant.ConstantParams.TRANSCATION_ERROR;

public class TxRecordAdapter extends BaseQuickAdapter<TxRecordBean, BaseViewHolder> {
    private Context context;
    private String split = " ： ";

    public TxRecordAdapter(Context context, int layoutResId, @Nullable List<TxRecordBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, TxRecordBean item) {


        viewHolder.setText(R.id.tx_hash, context.getResources().getString(R.string.tx_hash) + split + item.getHash());
        try {
            if (!TextUtils.isEmpty(item.getTc_in_out())) {
                String time = DateUtils.getDateToString(Long.parseLong(item.getTc_in_out()), Y4M2D2H2M2S2);
                LoggerUtils.i("time = " + time);
                viewHolder.setText(R.id.tx_time, context.getResources().getString(R.string.tx_time) + split + time);
            } else {
                viewHolder.setText(R.id.tx_time, context.getResources().getString(R.string.tx_time) + split + "来自网络");
            }
        } catch (NumberFormatException r) {
            viewHolder.setText(R.id.tx_time, context.getResources().getString(R.string.tx_time) + split + "来自网络");
        }


        if (TX_SUSPENDED.equals(item.getBlockNumber())) {
            viewHolder.setText(R.id.tx_block, context.getResources().getString(R.string.tx_block) + split + context.getResources().getString(R.string.tx_block_suspended));
            viewHolder.setVisible(R.id.sync_node,true);
        } else if (TRANSCATION_ERROR.equals(item.getBlockNumber())) {
            viewHolder.setText(R.id.tx_block, context.getResources().getString(R.string.tx_block) + split + context.getResources().getString(R.string.tx_block_failure));
        } else {
            viewHolder.setText(R.id.tx_block, context.getResources().getString(R.string.tx_block) + split + item.getBlockNumber());
        }
        viewHolder.setText(R.id.tx_from, context.getResources().getString(R.string.tx_out_addr) + split + item.getFrom());
        viewHolder.setText(R.id.tx_to, context.getResources().getString(R.string.tx_in_addr) + split + item.getTo());
        viewHolder.setText(R.id.tx_value, context.getResources().getString(R.string.tx_amount) + split + item.getValue() + " IONC");
        viewHolder.setText(R.id.tx_fee, context.getResources().getString(R.string.tx_fee) + split + item.getGas() + " IONC");
        viewHolder.addOnClickListener(R.id.sync_node);
    }
}
