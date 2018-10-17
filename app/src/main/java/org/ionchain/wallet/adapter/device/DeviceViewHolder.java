package org.ionchain.wallet.adapter.device;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.ionchain.wallet.adapter.iinterface.IViewHolder;


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
    public Button unbindDevice;
}
