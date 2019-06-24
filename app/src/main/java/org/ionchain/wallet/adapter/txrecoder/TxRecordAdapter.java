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

import static org.ionc.wallet.utils.DateUtils.Y4M2D2H2M2S2;
import static org.ionchain.wallet.constant.ConstantParams.DEFAULT_TRANSCATION_BLOCK_NUMBER_NULL;

public class TxRecordAdapter extends BaseQuickAdapter<TxRecordBean, BaseViewHolder> {
    private Context context;

    public TxRecordAdapter(Context context, int layoutResId, @Nullable List<TxRecordBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, TxRecordBean item) {
       

        viewHolder.setText(R.id.tx_hash,context.getResources().getString(R.string.tx_hash) + item.getHash());
        try {
            if (!TextUtils.isEmpty(item.getTc_in_out())) {
                String time = DateUtils.getDateToString(Long.parseLong(item.getTc_in_out()), Y4M2D2H2M2S2);
                LoggerUtils.i("time = " + time);
                viewHolder.setText(R.id.tx_time,context.getResources().getString(R.string.tx_time) + time);
            } else {
                viewHolder.setText(R.id.tx_time,"来自网络");
            }
        } catch (NumberFormatException r) {
            viewHolder.setText(R.id.tx_time,"来自网络");
        }


        if (DEFAULT_TRANSCATION_BLOCK_NUMBER_NULL.equals(item.getBlockNumber())) {
            viewHolder.setText(R.id.tx_block,context.getResources().getString(R.string.tx_block_unpacked));
        } else {
            viewHolder.setText(R.id.tx_block,context.getResources().getString(R.string.tx_block) + item.getBlockNumber());
        }
        viewHolder.setText(R.id.tx_from,context.getResources().getString(R.string.tx_out_addr) + item.getFrom());
        viewHolder.setText(R.id.tx_to,context.getResources().getString(R.string.tx_in_addr) + item.getTo());
        viewHolder.setText(R.id.tx_value,context.getResources().getString(R.string.tx_amount) + item.getValue() + " IONC");
        viewHolder.setText(R.id.tx_fee,context.getResources().getString(R.string.tx_fee) + item.getGas() + " IONC");
        viewHolder.addOnClickListener(R.id.tx_record_holder);
    }
}
