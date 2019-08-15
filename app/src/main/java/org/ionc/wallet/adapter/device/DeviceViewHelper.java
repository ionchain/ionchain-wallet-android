package org.ionc.wallet.adapter.device;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.ionc.wallet.adapter.IViewHolder;
import org.ionc.wallet.adapter.IViewHolderHelper;
import org.ionc.wallet.bean.DeviceBean;
import org.ionchain.wallet.R;

import java.util.List;

/**
 * USER: binny  596928539@qq.com
 * DATE: 2018/9/12
 * 描述: 辅助类 绑定数据
 */
public class DeviceViewHelper implements IViewHolderHelper<DeviceViewHolder, DeviceBean.DataBean> {


    @Override
    public IViewHolder initItemViewHolder(DeviceViewHolder viewHolder, View view) {
        viewHolder = new DeviceViewHolder();
        viewHolder.deviceIcon = (ImageView) view.findViewById(R.id.device_icon);
        viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
        viewHolder.bindDate = (TextView) view.findViewById(R.id.bind_date);
        viewHolder.totalIncome = (TextView) view.findViewById(R.id.total_income);
        viewHolder.unbindDevice = (RelativeLayout) view.findViewById(R.id.unbind_device);
        return viewHolder;
    }

    @Override
    public void bindListDataToView(Context context, List<DeviceBean.DataBean> iBaseBeanList, DeviceViewHolder viewHolder, final int position) {
        final DeviceBean.DataBean bean = iBaseBeanList.get(position);
        Glide.with(context)
                .load(bean.getImage_url())
                .into(viewHolder.deviceIcon);
        viewHolder.deviceName.setText(bean.getName());
        viewHolder.bindDate.setText(bean.getCreated_at());
    }

}
