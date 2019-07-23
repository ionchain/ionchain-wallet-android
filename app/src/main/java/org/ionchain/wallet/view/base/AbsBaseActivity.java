package org.ionchain.wallet.view.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.SoftKeyboardUtil;
import org.ionc.wallet.utils.ToastUtil;
import org.ionchain.wallet.R;
import org.ionchain.wallet.constant.ConstantActivitySkipTag;
import org.ionchain.wallet.constant.ConstantParams;
import org.ionchain.wallet.helper.ActivityHelper;
import org.ionchain.wallet.immersionbar.ImmersionBar;
import org.ionchain.wallet.view.activity.MainActivity;
import org.ionchain.wallet.view.activity.manager.ManageWalletActivity;
import org.ionchain.wallet.view.activity.webview.AgreementWebActivity;
import org.ionchain.wallet.utils.LocalManageUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static org.ionchain.wallet.App.mAppInstance;
import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_MAIN_ACTIVITY;
import static org.ionchain.wallet.constant.ConstantActivitySkipTag.INTENT_FROM_WHERE_TAG;
import static org.ionchain.wallet.constant.ConstantParams.REQUEST_CODE_QRCODE_PERMISSIONS;
import static org.ionchain.wallet.constant.ConstantParams.REQUEST_STORAGE_PERMISSIONS;
import static org.ionchain.wallet.view.fragment.AssetFragment.NEW_WALLET_FOR_RESULT_CODE;

public abstract class AbsBaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    protected AbsBaseActivity mActivity = this;
    protected ImmersionBar mImmersionBar;
    protected String TAG = this.getClass().getSimpleName();
    private ProgressDialog dialog;
    protected String mActivityFrom = INTENT_FROM_MAIN_ACTIVITY; //来自main


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalManageUtil.setLocal(newBase));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());


        ActivityHelper.getHelper().addActivity(this);

        Intent intent = getIntent();


        if (intent != null) {
            handleIntent(intent);
        }

        initView();

        initCommonTitle();
        setImmersionBar();
        initData();
        setListener();
        initSmartRefreshHeaderAndFooterDate();
    }

    protected void setImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
    }

    protected void initCommonTitle() {

    }

    protected int getColorBlue(){
       return getResources().getColor(R.color.blue_top);
    }

    protected int getColorWhite() {
        return getResources().getColor(R.color.white);
    }
    /**
     * 国际好第三方库
     */
    private void initSmartRefreshHeaderAndFooterDate() {
        ClassicsHeader.REFRESH_HEADER_PULLING = getAppString(R.string.smart_refresh_header_pulldown);
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getAppString(R.string.smart_refresh_header_refreshing);
        ClassicsHeader.REFRESH_HEADER_LOADING = getAppString(R.string.smart_refresh_header_loading);
        ClassicsHeader.REFRESH_HEADER_RELEASE = getAppString(R.string.smart_refresh_header_release);
        ClassicsHeader.REFRESH_HEADER_FINISH = getAppString(R.string.smart_refresh_header_finish);
        ClassicsHeader.REFRESH_HEADER_FAILED = getAppString(R.string.smart_refresh_header_failed);
//        ClassicsHeader.REFRESH_HEADER_LASTTIME = "'" + getAppString(R.string.smart_refresh_header_last_time) + "' M-d HH:mm";

        ClassicsFooter.REFRESH_FOOTER_PULLING = getAppString(R.string.smart_refresh_footer_pull_down);
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getAppString(R.string.smart_refresh_footer_refreshing);
        ClassicsFooter.REFRESH_FOOTER_LOADING = getAppString(R.string.smart_refresh_footer_loading);
        ClassicsFooter.REFRESH_FOOTER_RELEASE = getAppString(R.string.smart_refresh_footer_release);
        ClassicsFooter.REFRESH_FOOTER_FINISH = getAppString(R.string.smart_refresh_footer_finish);
        ClassicsFooter.REFRESH_FOOTER_FAILED = getAppString(R.string.smart_refresh_footer_failed);
        ClassicsFooter.REFRESH_FOOTER_NOTHING = getAppString(R.string.smart_refresh_footer_no_more);
    }

    protected void setListener() {
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected void handleIntent(Intent intent) {
        mActivityFrom = intent.getStringExtra(INTENT_FROM_WHERE_TAG) == null ? mActivityFrom : intent.getStringExtra(INTENT_FROM_WHERE_TAG);
        LoggerUtils.i("来自: " + mActivityFrom);
    }


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
    public void showProgress(String msg) {
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
    public void hideProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求相机权限
     *
     * @return 是否开启了权限
     */
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    protected boolean requestCameraPermissions() {
        String[] perms = new String[]{Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getAppString(R.string.permission_camera), REQUEST_CODE_QRCODE_PERMISSIONS, perms);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 请求存储权限
     *
     * @return 是否开启了权限
     */
    @AfterPermissionGranted(REQUEST_STORAGE_PERMISSIONS)
    protected boolean requestStoragePermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getAppString(R.string.permission_storage), REQUEST_STORAGE_PERMISSIONS, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
        LoggerUtils.i("允许" + list.toString());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        ToastUtil.showToastLonger(getAppString(R.string.permission_request));
        LoggerUtils.i("拒绝" + list.toString());
    }

    protected void skip(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /*
     * 跳转到 协议一些页面
     * */
    protected void skipWebProtocol() {
        Intent intent = new Intent(mActivity, AgreementWebActivity.class);
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

    protected void skip(Class<?> clazz, String params, Parcelable obj) {
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

    /**
     * @param id 多语言环境中的
     * @return 字符串
     */
    public String getAppString(int id) {
        return mAppInstance.getAppString(id);
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
        return mAppInstance.getAppString(id, obj);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideProgress();
    }

    /**
     * 判断该导入操作来自哪个模块
     * 1:资产模块,位于主界面 .导入如成功跳转到主界面,(并且主界面显示新导入的钱包,及新钱包作为主钱包展示)
     * 2:钱包管理模块 ,导入成功跳转到钱包管理界面
     * @param walletBean
     */

    protected void skipToBack(WalletBeanNew walletBean) {
        if (IONCWalletSDK.getInstance().getAllWalletNew().size() == 1) {
            finish();
            skip(MainActivity.class);
            return;
        }
        if (mActivityFrom.equals(ConstantActivitySkipTag.INTENT_FROM_MAIN_ACTIVITY)) {
            Intent intent = new Intent();
            /*
             *交易成功，返回成功的hash值给fragment
             */
            intent.putExtra(ConstantParams.SERIALIZABLE_DATA_WALLET_BEAN,walletBean);
            setResult(NEW_WALLET_FOR_RESULT_CODE, intent);
            finish();
        } else if (mActivityFrom.equals(ConstantActivitySkipTag.INTENT_FROM_MANAGER_ACTIVITY)) {
            skip(ManageWalletActivity.class);
//            finish();
        }
    }
    /**

     *

     * @MethodName:closeInputMethod

     * @Description:关闭系统软键盘

     * @throws

     */

    protected void closeInputMethod(){

        try {

            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))

                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),

                            InputMethodManager.HIDE_NOT_ALWAYS);

        } catch (Exception e) { }finally{ }

    }

    /**

     *

     * @MethodName:openInputMethod

     * @Description:打开系统软键盘

     * @throws

     */

    protected void openInputMethod(final EditText editText){

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {

                InputMethodManager inputManager = (InputMethodManager) editText

                        .getContext().getSystemService(

                                Context.INPUT_METHOD_SERVICE);

                inputManager.showSoftInput(editText, 0);

            }

        }, 200);

    }
}
