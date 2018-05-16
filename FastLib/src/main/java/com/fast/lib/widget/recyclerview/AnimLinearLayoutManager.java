package com.fast.lib.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fast.lib.listener.OverScrollListener;


/**
 * Created by shichaohui on 2015/8/3 0003.
 * <br/>
 * 增加了{@link OverScrollListener}的LinearLayoutManager
 */
public class AnimLinearLayoutManager extends LinearLayoutManager {

    private OverScrollListener mListener;

    public AnimLinearLayoutManager(Context context) {
        super(context);
    }

    public AnimLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);

        mListener.overScrollBy(dy - scrollRange);

        return scrollRange;
    }

    /**
     * 设置滑动过度监听
     *
     * @param listener
     */
    public void setOverScrollListener(OverScrollListener listener) {
        mListener = listener;
    }

}
