package org.ionchain.wallet.mvp.view.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.ionc.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.helper.ActivityHelper;
import org.ionchain.wallet.immersionbar.ImmersionBar;
import org.ionchain.wallet.mvp.view.activity.WebActivity;

import java.io.Serializable;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static org.ionchain.wallet.constant.ConstantParams.SERVER_PROTOCOL_KEY;

public abstract class AbsBaseActivity extends AppCompatActivity {
    private final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    protected AbsBaseActivity mActivity = this;
    protected ImmersionBar mImmersionBar;
    protected String TAG = this.getClass().getSimpleName();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());

        ActivityHelper.getHelper().addActivity(this);
        mImmersionBar = ImmersionBar.with(this);
        Intent intent = getIntent();
        if (intent != null) {
            handleIntent(intent);
        }
        initView();
        initData();
        setListener();
    }

    protected void setListener() {
    }


    protected abstract void initData();

    protected abstract void initView();

    protected void handleIntent(Intent intent) {
    }

    protected abstract int getLayoutId();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    protected void hideKeyboard() {
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }

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

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    protected void requestCodeQRCodePermissions() {
        String[] perms = new String[]{Manifest.permission.CAMERA, Manifest.permission.VIBRATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和闪光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    protected void skip(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /*
     * 跳转到 web 页面
     * */
    protected void skipWeb(String value) {
        Intent intent = new Intent(mActivity, WebActivity.class);
        intent.putExtra(SERVER_PROTOCOL_KEY, value);
        startActivity(intent);
    }

    protected void skip(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }


    protected void skip(Class<?> clazz, String params, Serializable obj) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        startActivity(intent);
    }

    protected void skip(Class<?> clazz, String params, Serializable obj, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        startActivityForResult(intent, requestCode);
    }

    protected void skip(Class<?> clazz, String params, Serializable obj, String params1, Serializable obj1) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        startActivityForResult(intent, 0);
    }

    protected void skip(Class<?> clazz, String params, String obj, String params1, Serializable obj1, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        startActivityForResult(intent, requestCode);
    }


    protected void skip(Class<?> clazz, String params, Serializable obj, String params1, Serializable obj1, String params2,
                        Serializable obj2) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        intent.putExtra(params2, obj2);
        startActivityForResult(intent, 0);
    }

    protected void skip(Class<?> clazz, String params, Serializable obj, String params1, Serializable obj1, String params2,
                        Serializable obj2, int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(params, obj);
        intent.putExtra(params1, obj1);
        intent.putExtra(params2, obj2);
        startActivityForResult(intent, requestCode);
    }
}
