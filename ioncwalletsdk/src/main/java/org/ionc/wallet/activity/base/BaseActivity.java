package org.ionc.wallet.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.ionc.wallet.activity.OnCreateCallback;

/**
 * user: binny
 * date:2019/1/8
 * description：
 */
public abstract class BaseActivity extends AppCompatActivity implements OnCreateCallback {
    protected Activity mActivity = this;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getIntent() != null) {
            handleIntent(getIntent());
        }
        initView();
        initData();
        setListener();
    }

    protected void setListener() {

    }

    /**
     * 实例化数据
     */
    protected abstract void initData();

    /**
     * 实例化视图
     */
    protected abstract void initView();

    /**
     * 处理传过来的值
     */
    protected void handleIntent(Intent intent) {
        if (intent == null) {

        }
    }

    /**
     * @return 布局id
     */
    protected abstract int getLayoutId();
    /**
     * 显示进度提示窗
     *
     * @param msg 显示信息
     */
    protected void showProgress(String msg) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setMessage(msg);
        dialog.show();

    }
    /**
     * 隐藏进度弹窗
     */
    protected void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    protected void skip(Class<?> clz){
        Intent intent = new Intent(this,clz);
        startActivity(intent);
    }
    /**
     * @param id 多语言环境中的
     * @return 字符串
     */
    public String getAppString(int id) {
        return getResources().getString(id);
    }
    /**
     * @param id 多语言环境中的
     * @return color
     */
    public int getAppColor(int id) {
        return getResources().getColor(id);
    }

    /**
     * @param id  多语言环境中的
     * @param obj
     * @return 字符串
     */
    public String getAppString(int id, Object obj) {
        return getResources().getString(id, obj);
    }
}
