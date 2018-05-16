package org.ionchain.wallet.comm.utils;

/**
 * Created by siberiawolf on 17/7/24.
 */

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;


public class AndroidBug5497Workaround {

    protected OnSizeChangedListenner onSizeChangedListenner;

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity, OnSizeChangedListenner onSizeChangedListenner) {
        new AndroidBug5497Workaround(activity,onSizeChangedListenner);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Activity activity;

    private AndroidBug5497Workaround(Activity activity, OnSizeChangedListenner onSizeChangedListenner){
        this.activity = activity;
        this.onSizeChangedListenner = onSizeChangedListenner;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();

    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                // keyboard probably just became visible
//                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//                frameLayoutParams.height = (int)activity.getResources().getDimension(R.dimen.h110);
                if(onSizeChangedListenner != null)
                    onSizeChangedListenner.onSizeChange(true,0,heightDifference);
            } else {
                // keyboard probably just became hidden
//                frameLayoutParams.height = usableHeightSansKeyboard;
                if(onSizeChangedListenner != null)
                    onSizeChangedListenner.onSizeChange(false,0,heightDifference);
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    /**
     * 设置监听事件
     * @param paramonSizeChangedListenner
     */
    public void setOnSizeChangedListenner(
            OnSizeChangedListenner paramonSizeChangedListenner) {
        this.onSizeChangedListenner = paramonSizeChangedListenner;
    }
    /**
     * 大小改变的内部接口
     */
    public abstract interface OnSizeChangedListenner {
        public abstract void onSizeChange(boolean paramBoolean, int w, int h);
    }

}
