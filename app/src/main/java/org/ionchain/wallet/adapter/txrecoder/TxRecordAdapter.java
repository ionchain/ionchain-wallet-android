package org.ionchain.wallet.adapter.txrecoder;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.utils.DateUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.ColorUtils;

import java.util.List;

import static org.ionc.wallet.sdk.IONCWalletSDK.TX_SUSPENDED;
import static org.ionc.wallet.utils.DateUtils.YYYY_MM_DD_HH_MM_SS;
import static org.ionchain.wallet.view.base.AbsBaseViewPagerFragment.TYPE_ALL;
import static org.ionchain.wallet.view.base.AbsBaseViewPagerFragment.TYPE_IN;
import static org.ionchain.wallet.view.base.AbsBaseViewPagerFragment.TYPE_OUT;

public class TxRecordAdapter extends BaseQuickAdapter<TxRecordBean, BaseViewHolder> {
    private Context context;
    private WalletBeanNew mWalletBeanNew;
    private char mType;

    public TxRecordAdapter(char type, WalletBeanNew walletBeanNew, Context context, int layoutResId, @Nullable List<TxRecordBean> data) {
        super(layoutResId, data);
        this.context = context;
        mWalletBeanNew = walletBeanNew;
        mType = type;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, TxRecordBean item) {

        try {
            if (!TextUtils.isEmpty(item.getTc_in_out())) {
                String time = DateUtils.getDateToString(Long.parseLong(item.getTc_in_out()), YYYY_MM_DD_HH_MM_SS);
                LoggerUtils.i("time = " + time);
                viewHolder.setText(R.id.tx_item_datetime_tv, time);
            } else {
                viewHolder.setText(R.id.tx_item_datetime_tv, "来自网络");
            }
        } catch (NumberFormatException r) {
            viewHolder.setText(R.id.tx_item_datetime_tv, "来自网络");
        }
        if (TX_SUSPENDED.equals(item.getBlockNumber())) {
            viewHolder.setText(R.id.tx_item_state_tv, context.getResources().getString(R.string.tx_block_suspended));
            if (mWalletBeanNew.getAddress().equals(item.getFrom())) {
                //转出 进行中
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out_doing);
            } else {
                //转入 进行中
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_in_doing);
            }

        } else if (context.getResources().getString(R.string.tx_failure).equals(item.getBlockNumber())) {
            //交易失败
            viewHolder.setText(R.id.tx_item_state_tv, context.getResources().getString(R.string.tx_block_failure));
            viewHolder.setTextColor(R.id.tx_item_state_tv, ColorUtils.getTxColorFailure());
            if (mWalletBeanNew.getAddress().equals(item.getFrom())) {
                //转出  交易失败
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out_failure);
            } else {
                //转入 交易失败
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_in_failure);
            }
        } else {
            //已完成
            viewHolder.setText(R.id.tx_item_state_tv, context.getResources().getString(R.string.tx_done));
            if (item.getFrom().equals(item.getTo())) {
                switch (mType) {
                    case TYPE_ALL:
                        viewHolder.setText(R.id.tx_item_value, item.getValue() + " IONC");
                        viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out);
                        break;
                    case TYPE_OUT:
                        viewHolder.setText(R.id.tx_item_value, "- " + item.getValue() + " IONC");
                        viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out);
                        break;
                    case TYPE_IN:
                        viewHolder.setText(R.id.tx_item_value, "+ " + item.getValue() + " IONC");
                        viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_in);
                        break;
                }
            } else if (mWalletBeanNew.getAddress().equals(item.getFrom())) {
                //转出已完成
                viewHolder.setText(R.id.tx_item_value, "- " + item.getValue() + " IONC");
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out);
            } else {
                //转入已完成
                viewHolder.setText(R.id.tx_item_value, "+ " + item.getValue() + " IONC");
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_in);
            }
        }


//        viewHolder.addOnClickListener(R.id.tx_state_img);
    }
}
