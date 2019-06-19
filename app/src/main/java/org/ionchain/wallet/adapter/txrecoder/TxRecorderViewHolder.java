package org.ionchain.wallet.adapter.txrecoder;

import android.widget.LinearLayout;
import android.widget.TextView;

import org.ionc.wallet.adapter.IViewHolder;


/**
 * AUTHOR binny
 * <p>
 * TIME 2018/11/13 15:02
 */
class TxRecorderViewHolder implements IViewHolder {
    TextView block;
    TextView tx_time;
    TextView txHash;
    TextView txFee;
    TextView from;
    TextView to;
    TextView value;
    LinearLayout tx_record_holder;

}
