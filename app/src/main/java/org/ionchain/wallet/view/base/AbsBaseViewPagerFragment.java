package org.ionchain.wallet.view.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.ionc.wallet.bean.TxRecordBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.ionchain.wallet.App;
import org.ionchain.wallet.R;
import org.ionchain.wallet.adapter.txrecoder.TxRecordAdapter;
import org.ionchain.wallet.immersionbar.ImmersionBar;
import org.ionchain.wallet.presenter.transcation.TxRecordPresenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static org.ionchain.wallet.constant.ConstantParams.REQUEST_CODE_QRCODE_PERMISSIONS;
import static org.ionchain.wallet.constant.ConstantParams.REQUEST_STORAGE_PERMISSIONS;


/**
 * author  binny
 * date 5/9
 */
public abstract class AbsBaseViewPagerFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    protected AbsBaseActivity mActivity;
    private View mContainerView;
    protected ImmersionBar mImmersionBar;

    protected String TAG = "visible";
    protected String TAG_NAME;

    /**
     * 当前钱包
     */
    protected static WalletBeanNew mWalletBeanNew;
    /**
     * 记录适配器
     */

    protected TxRecordAdapter mTxRecordAdapter;
    /**
     * 钱包记录的实际数据集
     */
    protected List<TxRecordBean> mListAllData = new ArrayList<>();
    protected List<TxRecordBean> mListAllDataTemp = new ArrayList<>();
    protected List<TxRecordBean> mListNetTemp = new ArrayList<>();
    /**
     * 转出记录
     */
    protected List<TxRecordBean> mListDoneData = new ArrayList<>();
    /**
     * 转入记录
     */
    protected List<TxRecordBean> mListDoingData = new ArrayList<>();
    protected List<TxRecordBean> mListFailureData = new ArrayList<>();


    /**
     * 交易记录
     */
    protected TxRecordPresenter mTxRecordPresenter;

    public static final char TYPE_ALL = 0;
    public static final char TYPE_DONE = 1;
    public static final char TYPE_DOING = 2;
    public static final char TYPE_FAILURE = 3;

    public AbsBaseViewPagerFragment() {
        this.TAG_NAME = this.getClass().getSimpleName();
    }

    protected boolean mVisibleToUser;

    /*
     * 防止频繁请求网络
     * */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisibleToUser = isVisibleToUser;
        LoggerUtils.i("local-data", "   isVisibleToUser = " + isVisibleToUser + ";  mContainerView = " + mContainerView);
        if (mContainerView != null && isVisibleToUser) {
            LoggerUtils.i(TAG, "AbsBaseViewPagerFragment setUserVisibleHint visible ");
            visible();//创建其他fragment 时  不加载数据，当 该fragment 可见时，加载数据
        } else {
            unVisible();
        }
    }

    public void unVisible() {

    }

    /**
     * 对用户可见
     */
    protected abstract void visible();


    /**
     * 执行该方法时，Fragment与Activity已经完成绑定
     * <p>
     * 该方法有一个Activity类型的参数，代表绑定的Activity，这时候你可以执行诸如mActivity = activity的操作。
     * <p>
     * 在API低于23的版本中不会执行该方法
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AbsBaseActivity) context;
    }


    /**
     * 在API低于23的版本中会执行该方法
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AbsBaseActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContainerView != null) {
//            //防止重复创建视图
//            LoggerUtils.i(TAG, "AbsBaseViewPagerFragment 无需重新创建视图");
//            initView(mContainerView);
//
//            mImmersionBar = ImmersionBar.with(mActivity, this);
//            initImmersionBar();
//            setListener();
//            if (getUserVisibleHint()) {
//                LoggerUtils.i(TAG, "visible 第一次 可见的 fragment 要加载数据");
//                initData();//第一个 可见的 fragment 要加载数据
//            } else {
//                LoggerUtils.i(TAG, "visible 非第一次 可见的 fragment 不要加载数据");
//            }
            return mContainerView;
        }


        /*
         * 创建视图
         * 如果第一次创建时，可见，则加载数据，绑定数据
         * */

        mContainerView = inflater.inflate(getFragmentLayout(), container, false);
        initView(mContainerView);

        mImmersionBar = ImmersionBar.with(mActivity, this);
        initImmersionBar();
        setListener();
        if (getUserVisibleHint()) {
            LoggerUtils.i(TAG, "第一次 可见的 fragment 要加载数据 ");
            initData();//第一个 可见的 fragment 要加载数据
        } else {
            LoggerUtils.i(TAG, "非第一次 可见的 fragment 不要加载数据 ");
        }
        return mContainerView;
    }

    protected void setListener() {

    }


    /**
     * @return 布局文件
     */
    protected abstract int getFragmentLayout();

    /**
     * 初始化view
     *
     * @param view 实例化的ivew
     */
    protected abstract void initView(View view);


    /**
     * 当孩子需要初始化数据，或者联网请求绑定数据，展示数据的 等等可以重写该方法
     * <p>
     * 加载本地数据 或者网络数据
     */
    protected abstract void initData();

    /**
     * 设置沉浸式
     */
    private void initImmersionBar() {
        mImmersionBar
                .statusBarColor(getTopBarColor())
                .execute();
    }

    private int getTopBarColor() {
        return R.color.main_color;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null)
            mImmersionBar.destroy();
    }

    /**
     * @param clazz 跳转的activity
     */
    protected void skip(Class<?> clazz) {
        Intent intent = new Intent(mActivity, clazz);
        startActivity(intent);
    }

    /**
     * @param intent 跳转的activity
     */
    protected void skip(Intent intent) {
        startActivity(intent);
    }

    protected void skip(Class<?> clazz, String params, Serializable obj) {
        try {
            Intent intent = new Intent(mActivity, clazz);
            intent.putExtra(params, obj);
            startActivityForResult(intent, 0);
        } catch (Throwable e) {
            LoggerUtils.e(e.getMessage());
        }
    }

    protected void skip(Class<?> clazz, String params, Parcelable obj) {
        try {
            Intent intent = new Intent(mActivity, clazz);
            intent.putExtra(params, obj);
            startActivityForResult(intent, 0);
        } catch (Throwable e) {
            LoggerUtils.e(e.getMessage());
        }
    }

    /**
     * 请求相机权限
     *
     * @return 是否开启了权限
     */
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    protected boolean requestCameraPermissions() {
        String[] perms = new String[]{Manifest.permission.CAMERA, Manifest.permission.VIBRATE};
        if (!EasyPermissions.hasPermissions(App.mAppInstance, perms)) {
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
        if (!EasyPermissions.hasPermissions(App.mAppInstance, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getAppString(R.string.permission_storage), REQUEST_STORAGE_PERMISSIONS, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        ToastUtil.showToastLonger(getAppString(R.string.permission_request));
        LoggerUtils.i("AbsBaseViewPagerFragment 拒绝" + list.toString());
    }

    protected void showProgress(String msg) {
        mActivity.showProgress(msg);
    }

    protected void hideProgress() {
        mActivity.hideProgress();
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
