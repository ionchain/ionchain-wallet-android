package org.ionchain.wallet.helper;

import android.content.Context;
import android.view.View;

import com.smart.holder.iinterface.IViewHolder;
import com.smart.holder.iinterface.IViewHolderHelper;

import org.ionchain.wallet.model.DeviceBean;

import java.util.List;

/**
 * USER: binny
 * DATE: 2018/9/12
 * 描述: 辅助类 绑定数据
 */
public class DeviceViewHelper implements IViewHolderHelper<DeviceViewHolder, DeviceBean> {
    @Override
    public IViewHolder initItemViewHolder(DeviceViewHolder viewHolder, View convertView) {
        return null;
    }

    @Override
    public void bindListDataToView(Context context, List<DeviceBean> iBaseBeanList, DeviceViewHolder viewHolder, int position) {

    }
}
