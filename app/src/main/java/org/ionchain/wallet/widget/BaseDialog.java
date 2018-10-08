package org.ionchain.wallet.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import org.ionchain.wallet.R;

public abstract class BaseDialog extends Dialog {
    protected Activity mActivity;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.base_dialog);
        mActivity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initView();
    }

    /**
     * 实例化视图
     */
    protected abstract void initView();

    /**
     * @return 布局id
     */
    protected abstract int getLayout();

    protected void initDialog() {
        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = mActivity.getResources().getDisplayMetrics();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = dm.widthPixels*6/7;
            params.height = dm.widthPixels*3/5;
            window.setAttributes(params);
        }
    }
}
