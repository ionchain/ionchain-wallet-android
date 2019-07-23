package org.ionchain.wallet.view.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopupWindowBuilder {
    protected Activity context;
    protected View contentView;
    protected PopupWindow mInstance;


    private int width;
    private int height;
    private int style;//进出动画
    private View parent;
    private int gravity;
    private int offsetX;
    private int offsetY;

    private OnItemBuilder mOnItemBuilder;


    public PopupWindowBuilder(Context c, int layoutRes, OnItemBuilder itemBuilder) {
        context = (Activity) c;
        contentView = LayoutInflater.from(context).inflate(layoutRes, null, false);
        mInstance = new PopupWindow(contentView);
        mOnItemBuilder = itemBuilder;
        mOnItemBuilder.initMoreItems(mInstance, contentView);
    }


    public PopupWindowBuilder setWidth(int width) {
        this.width = width;
        return this;
    }


    public PopupWindowBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public PopupWindowBuilder build() {
        mInstance.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mInstance.setOutsideTouchable(true);
        mInstance.setTouchable(true);
        mInstance.setWidth(width);
        mInstance.setHeight(height);
        mInstance.setAnimationStyle(style);
        mInstance.setClippingEnabled(false);//解决全屏问题
        setBackgroundAlpha(0.5f);//设置屏幕透明度
        mInstance.setBackgroundDrawable(new BitmapDrawable());
        mInstance.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1.0f;
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                context.getWindow().setAttributes(lp);
            }
        });
        return this;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().setAttributes(lp);
    }

    /**
     * 显示 弹窗
     */
    public PopupWindowBuilder show() {
        mInstance.showAtLocation(parent, gravity, offsetX, offsetY);
        return this;
    }

    public void release() {
        if (mInstance != null) {
            mInstance.dismiss();
        }
    }

    public interface OnItemBuilder {
        void initMoreItems(PopupWindow instance, View contentView);
    }


    public PopupWindowBuilder setLocation(View parent, int gravity, int offsetX, int offsetY) {
        this.parent = parent;
        this.gravity = gravity;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        return this;
    }

    public PopupWindowBuilder setAnimationStyle(int style) {
        this.style = style;
        return this;
    }

}
