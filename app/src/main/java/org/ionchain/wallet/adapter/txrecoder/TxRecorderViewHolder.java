package org.ionchain.wallet.adapter.txrecoder;

import android.widget.TextView;

import org.ionc.wallet.sdk.adapter.IViewHolder;


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
}
