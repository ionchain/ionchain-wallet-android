package org.ionchain.wallet.ui.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.ionchain.wallet.immersionbar.ImmersionBar;
import org.ionchain.wallet.manager.ActivityHelper;
import org.ionchain.wallet.utils.SoftKeyboardUtil;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
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
        requestCodeQRCodePermissions();
    }

    protected abstract void initData();


    protected void setListener() {

    }

    protected abstract void handleIntent();

    protected abstract void initView();

    protected abstract int getLayoutId();

    protected void intoActivity(Class<?> c){
        startActivity(new Intent(this,c));
        finish();
    }
    public void intoActivityWithAnimotion(Class<?> c){
        startActivity(new Intent(this,c));
        finish();
    }
  public void intoActivityWithAnimotion(Intent intent){
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mImmersionBar != null)
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }

    protected void transfer(Class<?> clazz) {
        Intent intent = new Intent( this, clazz );
        startActivity( intent );
    }
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.VIBRATE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
    @AfterPermissionGranted(REQUEST_CODE_IMPORT_PERMISSIONS)
    private void requestCodeImprotPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "导入钱包需要的权限", REQUEST_CODE_IMPORT_PERMISSIONS, perms);
        }
    }
    protected void hideKeyboard(){
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }

}
