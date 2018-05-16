package com.fast.lib.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fast.lib.listener.OverScrollListener;


/**
 * Created by shichaohui on 2015/8/3 0003.
 * <br/>
 * 增加了{@link OverScrollListener}的GridLayoutManage
 */
public class AnimGridLayoutManager extends GridLayoutManager {

    private OverScrollListener mListener;


    public AnimGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public AnimGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
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
