package org.ionchain.wallet.adapter.txrecoder;

import android.widget.TextView;

import org.ionc.wallet.adapter.IViewHolder;


/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:02
 */
class TxRecorderViewHolder implements IViewHolder {
    TextView block;
    TextView txHash;
    TextView txFee;
    TextView from;
    TextView to;
    TextView value;

    public TextView getBlock() {
        return block;
    }

    public TextView getTxHash() {
        return txHash;
    }

    public TextView getTxFee() {
        return txFee;
    }

    public TextView getFrom() {
        return from;
    }

    public TextView getTo() {
        return to;
    }

    public TextView getValue() {
        return value;
    }
}
