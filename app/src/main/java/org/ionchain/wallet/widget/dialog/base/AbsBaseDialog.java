package org.ionchain.wallet.widget.dialog.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import org.ionchain.wallet.R;
import org.ionchain.wallet.widget.dialog.LogUtils;


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
    protected void initDialogDefault(){
        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置,
         * 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = getWindow();

        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        WindowManager m = mActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p; // 获取对话框当前的参数值
        if (dialogWindow != null) {
            p = dialogWindow.getAttributes();
//            p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
            p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
            dialogWindow.setAttributes(p);
        } else {
            LogUtils.e("设置对话框带下大小失败");
        }
    }

}
