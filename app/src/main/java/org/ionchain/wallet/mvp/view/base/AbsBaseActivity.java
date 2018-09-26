package org.ionchain.wallet.mvp.view.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.ionchain.wallet.immersionbar.ImmersionBar;
import org.ionchain.wallet.manager.ActivityHelper;
import org.ionchain.wallet.utils.SoftKeyboardUtil;

import java.io.Serializable;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者: binny
 * 时间: 5/24
 * 描述:
 */
public abstract class AbsBaseActivity extends AppCompatActivity {
    protected AbsBaseActivity mActivity;
    protected ImmersionBar mImmersionBar;
    protected Intent mIntent;

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_IMPORT_PERMISSIONS = 2;

    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        ActivityHelper.getHelper().addActivity(this);
        setContentView(getLayoutId());

        mImmersionBar = ImmersionBar.with(this);

        mActivity = this;
        handleIntent();
        initView();
        initData();
        setListener();
    }

    protected abstract void initData();


    protected void setListener() {

    }

    protected void handleIntent() {
    }

    protected abstract void initView();

    protected abstract int getLayoutId();


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mImmersionBar != null)
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

    protected void skip(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected void skip(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }


    public void skip(Class<?> clazz, String params, Serializable obj) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        startActivity(intent);
    }

    public void skip(Class<?> clazz, String params, Serializable obj, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        startActivityForResult(intent, requestCode);
    }

    public void skip(Class<?> clazz, String params, Serializable obj, String params1, Serializable obj1) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        startActivityForResult(intent, 0);
    }

    public void skip(Class<?> clazz, String params, String obj, String params1, Serializable obj1, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        startActivityForResult(intent, requestCode);
    }


    public void skip(Class<?> clazz, String params, Serializable obj, String params1, Serializable obj1, String params2, Serializable obj2) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        intent.putExtra(params2, obj2);
        startActivityForResult(intent, 0);
    }

    public void skip(Class<?> clazz, String params, Serializable obj, String params1, Serializable obj1, String params2, Serializable obj2, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        intent.putExtra(params2, obj2);
        startActivityForResult(intent, requestCode);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    protected void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.VIBRATE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }


    protected void hideKeyboard() {
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }

    private ProgressDialog dialog;

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
}
