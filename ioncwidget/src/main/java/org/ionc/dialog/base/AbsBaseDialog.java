package org.ionc.dialog.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ionc.dialog.R;


public abstract class AbsBaseDialog extends Dialog {
    protected Activity mActivity;

    public AbsBaseDialog(@NonNull Context context) {
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
        initData();
    }

    /**
     * @return 布局id
     */
    protected abstract int getLayout();

    /**
     * 初始化 弹窗数据
     */
    protected abstract void initDialog();

    /**
     * 实例化视图
     */
    protected abstract void initView();

    /**
     * 初始化 数据
     */
    protected abstract void initData();

}
