package com.smart.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.smart.holder.iinterface.IViewHolder;
import com.smart.holder.iinterface.IViewHolderHelper;

import java.io.Serializable;
import java.util.List;



/**
 * Created by xubinbin on 2017/4/24.
 * function 封装adapter
 */

public class CommonAdapter<BEAN extends Serializable> extends BaseAdapter {
    private final int mItemViewLayout;//item布局文件
    private Context mContext;
    private IViewHolder mBaseViewHolder;
    private IViewHolderHelper mHolderHelper;
    private List<BEAN> mIBaseBeanList;

    /**
     * param context
     * param iBaseBeanList
     * param itemViewLayout
     * param iViewHolderHelper
     * @param context  上下文
     * @param iBaseBeanList 数据集 list 的形式传递过来
     * @param itemViewLayout  item的布局文件
     * @param iViewHolderHelper viewholder的接口
     */
    public CommonAdapter(Context context, List<BEAN> iBaseBeanList, int itemViewLayout, IViewHolderHelper iViewHolderHelper) {
        mContext = context;
        mIBaseBeanList = iBaseBeanList;
        mItemViewLayout = itemViewLayout;
        mHolderHelper = iViewHolderHelper;
    }

    @Override
    public int getCount() {
        return mIBaseBeanList==null?0:mIBaseBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
//    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mItemViewLayout,parent,false);
            mBaseViewHolder =  mHolderHelper.initItemViewHolder(mBaseViewHolder,convertView);
            convertView.setTag(mBaseViewHolder);
        }else {
            mBaseViewHolder = (IViewHolder)convertView.getTag();
        }
        mHolderHelper.bindListDataToView(mContext, mIBaseBeanList,mBaseViewHolder,position);
        return convertView;
    }

}
