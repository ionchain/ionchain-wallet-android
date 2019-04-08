package org.ionc.wallet.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ionc.wallet.sdk.R;


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
        setCanceledOnTouchOutside(false);
        initDialog();
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

    }
}
