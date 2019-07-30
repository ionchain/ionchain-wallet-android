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

import static org.ionc.wallet.sdk.IONCWalletSDK.TX_FAILURE;
import static org.ionc.wallet.sdk.IONCWalletSDK.TX_SUSPENDED;
import static org.ionc.wallet.utils.DateUtils.YYYY_MM_DD_HH_MM_SS;

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

        if (mWalletBeanNew.getAddress().equals(item.getFrom())) {
            //转出
            viewHolder.setText(R.id.tx_item_state_tv, context.getResources().getString(R.string.tx_out));
            viewHolder.setText(R.id.tx_item_value, "- " + item.getValue() + " IONC");
            if (TX_SUSPENDED.equals(item.getBlockNumber())) {
                //进行中
                viewHolder.setTextColor(R.id.tx_item_datetime_tv,context.getResources().getColor(R.color.tx_color_out_doing));
                viewHolder.setTextColor(R.id.tx_item_value,context.getResources().getColor(R.color.tx_color_out_doing));
                viewHolder.setTextColor(R.id.tx_item_state_tv,context.getResources().getColor(R.color.tx_color_out_doing));
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out_doing);
            } else if (TX_FAILURE.equals(item.getBlockNumber())) {
                //失败
                viewHolder.setTextColor(R.id.tx_item_datetime_tv,ColorUtils.getTxColorFailure());
                viewHolder.setTextColor(R.id.tx_item_value,ColorUtils.getTxColorFailure());
                viewHolder.setTextColor(R.id.tx_item_state_tv, ColorUtils.getTxColorFailure());
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out_failure);
            } else {
                //已完成
                viewHolder.setTextColor(R.id.tx_item_datetime_tv,context.getResources().getColor(R.color.tx_color_out_done));
                viewHolder.setTextColor(R.id.tx_item_value,context.getResources().getColor(R.color.tx_color_out_done));
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out);
                viewHolder.setTextColor(R.id.tx_item_state_tv, context.getResources().getColor(R.color.tx_color_out_done));
                LoggerUtils.i("hashcolor",item.getHash());
            }
        } else {
            //转入 进行中
            viewHolder.setText(R.id.tx_item_state_tv, context.getResources().getString(R.string.tx_in));
            viewHolder.setText(R.id.tx_item_value, "+ " + item.getValue() + " IONC");

            if (TX_SUSPENDED.equals(item.getBlockNumber())) {
                //进行中
                viewHolder.setTextColor(R.id.tx_item_datetime_tv, context.getResources().getColor(R.color.tx_color_in_doing));
                viewHolder.setTextColor(R.id.tx_item_state_tv, context.getResources().getColor(R.color.tx_color_in_doing));
                viewHolder.setTextColor(R.id.tx_item_value,context.getResources().getColor(R.color.tx_color_in_doing));
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_in_doing);
            } else if (TX_FAILURE.equals(item.getBlockNumber())) {
                //失败
                viewHolder.setTextColor(R.id.tx_item_datetime_tv, ColorUtils.getTxColorFailure());
                viewHolder.setTextColor(R.id.tx_item_state_tv, ColorUtils.getTxColorFailure());
                viewHolder.setTextColor(R.id.tx_item_state_tv, ColorUtils.getTxColorFailure());
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_out_failure);

            } else {
                //已完成
                viewHolder.setTextColor(R.id.tx_item_datetime_tv, context.getResources().getColor(R.color.tx_color_in_done));
                viewHolder.setTextColor(R.id.tx_item_state_tv, context.getResources().getColor(R.color.tx_color_in_done));
                viewHolder.setTextColor(R.id.tx_item_value,context.getResources().getColor(R.color.tx_color_in_done));
                viewHolder.setImageResource(R.id.tx_item_state_img, R.mipmap.tx_done_icon_in);
            }
        }

//        viewHolder.addOnClickListener(R.id.tx_state_img);
    }

    public void setCurrentWalletBean(WalletBeanNew walletBeanNew) {
        mWalletBeanNew = walletBeanNew;
    }
}
