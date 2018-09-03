package org.ionchain.wallet.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

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
        View view = LayoutInflater.from(mActivity).inflate(getLayout(), null);
        setContentView(view);
        initView(view);
    }

    /**
     * 实例化视图
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * @return 布局id
     */
    protected abstract int getLayout();
}
