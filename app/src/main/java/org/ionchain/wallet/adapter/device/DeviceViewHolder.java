package org.ionchain.wallet.adapter.device;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionc.wallet.sdk.adapter.IViewHolder;


/**
 * USER: binny
 * DATE: 2018/9/12
 * 描述:
 */
public class DeviceViewHolder implements IViewHolder {
    public ImageView deviceIcon;
    public TextView deviceName;
    public TextView bindDate;
    public TextView totalIncome;
    public RelativeLayout unbindDevice;
}
