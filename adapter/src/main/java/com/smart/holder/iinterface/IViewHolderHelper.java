package com.smart.holder.iinterface;

import android.content.Context;
import android.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * @param <VH> 泛型参数 （实现 IViewHolder 接口）
 * @param <BEAN> 泛型参数 （实现 Serializable 接口）
 */
public interface IViewHolderHelper<VH extends IViewHolder, BEAN extends Serializable>  {
        /** 用于初始化ViewHolder
         * @param viewHolder  item
         * @param convertView item View
         * @return IViewHolder
         */
        IViewHolder initItemViewHolder(VH viewHolder, View convertView);

        /**用于将集合中的数据设置 item中 的每一个控件
         * @param context  上下文
         * @param iBaseBeanList 数据集
         * @param viewHolder 控件容器
         * @param position 数据集中的位置
         */
        void bindListDataToView(Context context, List<BEAN> iBaseBeanList, VH viewHolder, int position);
    }
