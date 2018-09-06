package org.ionchain.wallet.ui.comm;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fast.lib.base.LibActivity;
import com.fast.lib.logger.Logger;
import com.fast.lib.okhttp.ResponseBean;
import com.gyf.barlibrary.ImmersionBar;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.comm.utils.SoftKeyboardUtil;
import org.ionchain.wallet.manager.ActivityHelper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;


/**
 * 作者：小狼人 on 17/4/27 16:36
 * 邮箱：siberiawolf89@gmail.com
 */
public abstract class BaseActivity extends LibActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    protected String TAG;
    public Toolbar mToolbar;
//    private boolean systemBar = true;

    int statusBarHeight = 0;

    private Menu menu;
    protected ImmersionBar mImmersionBar;

//    /**
//     * 需要进行检测的权限数组
//     */
//    protected String[] needPermissions = {
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.READ_PHONE_STATE
//    };

//    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = false;

    private boolean isClose = true;


    public void setSystemBar(boolean systemBar) {
    }

    public int getStatusBarHeight() {
        return statusBarHeight;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    @Override
    public void aidHandleMessage(int what, Object obj) {
        super.aidHandleMessage(what, obj);

        try {
            switch (what) {
                // 网络请求返回失败
                case NETWORK_FAIL_TYPE:

                    if (obj == null)
                        return;

                    ResponseBean failBean = (ResponseBean) obj;

                    String errorMsg = failBean.obj.toString();

                    ResponseModel errorModel = new ResponseModel();
                    String[] msg = errorMsg.split(":");
                    errorModel.setCode(msg[0]);
                    errorModel.setMsg(msg[1]);
                    handleMessage(failBean.refreshType, errorModel);
                    Logger.i(errorModel.toString());
                    break;
                // 网络请求返回成功
                case NETWORK_SUCCESSFUL_TYPE:
                    if (obj == null)
                        return;

                    ResponseBean successfulBean = (ResponseBean) obj;

                    String result = String.valueOf(successfulBean.obj);

                    Object jsonObject = null;

                    try {
                        jsonObject = Global.mGson.fromJson(result, successfulBean.mType);

                    } catch (Throwable e) {

                        try {
                            HashMap map = Global.mGson.fromJson(result, HashMap.class);

                            ResponseModel responseModel = new ResponseModel();
                            responseModel.setCode(map.get("code").toString());
                            responseModel.setMsg(map.get("msg").toString());
                            responseModel.setData(Global.mGson.toJson(map.get("data")));
                            jsonObject = responseModel;
                        } catch (Throwable e1) {
                            Logger.e(TAG, e);
                            Logger.e(TAG, e1);
                            ResponseModel jsonMsgModel = new ResponseModel();
                            jsonMsgModel.setCode("800");
                            jsonMsgModel.setMsg("Json解析的错误");
                            jsonObject = jsonMsgModel;
                        }
                    } finally {
                        handleMessage(successfulBean.refreshType, jsonObject);
                    }

                    break;
                default:
                    handleMessage(what, obj);
                    break;
            }
        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }

    public void handleMessage(int what, Object obj) {
    }

    ;


    public void initToolbar() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.back);

            if (mToolbar == null)
                return;


            if (getActivityTitleContent() != 0) {
                mToolbar.setTitle(getActivityTitleContent());
            } else {
                mToolbar.setTitle("");
            }
            setSupportActionBar(mToolbar);

            if (getHomeAsUpIndicatorIcon() != 0) {
                mToolbar.setNavigationIcon(getHomeAsUpIndicatorIcon());

            } else {
                mToolbar.setNavigationIcon(null);
            }
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aidsendMessage(R.id.navigationBack, null);
                }
            });
        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }


    public void setNavigationIcon(@DrawableRes int resId) {
        try {
            if (resId == 0) {
                mToolbar.setNavigationIcon(null);
            } else {
                mToolbar.setNavigationIcon(resId);
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }

    public void setNavigationContentDescription(String resId) {
        try {
            mToolbar.setNavigationContentDescription(resId);
        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }

    public boolean verifyStatus(ResponseModel responseModel) {
        try {
            if (!TextUtils.isEmpty(responseModel.code) && responseModel.code.equals(Comm.SUCCESS)) {
                return true;
            } else {
                return false;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        ActivityHelper.getHelper().addActivity(this);
        mImmersionBar = ImmersionBar.with(this);
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        initView(savedInstanceState);
        initData(savedInstanceState);
        setListener();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        ImmersionBar.with(this).init();
        initToolbar();

    }


    /**
     * 初始化布局以及View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void initData(Bundle savedInstanceState);


    /**
     * 设置点击事件
     *
     * @param id 控件的id
     */
    protected void setOnClickListener(@IdRes int id) {
        getViewById(id).setOnClickListener(this);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(View rootView, @IdRes int id) {
        return (VT) rootView.findViewById(id);
    }


    public abstract int getActivityMenuRes();

    public abstract int getHomeAsUpIndicatorIcon();

    public abstract int getActivityTitleContent();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getActivityMenuRes() == 0) {
            return false;
        }
        getMenuInflater().inflate(getActivityMenuRes(), menu);

        this.menu = menu;

        return true;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        aidsendMessage(item.getItemId(), item);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showProgressDialog() {
        super.showProgressDialog();
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        removeActivityFromTransitionManager(this);

    }


    //解决共享元素转场动画内存泄漏
    public void removeActivityFromTransitionManager(Activity activity) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        Class transitionManagerClass = TransitionManager.class;
        try {
            Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            //noinspection unchecked
            ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> runningTransitions
                    = (ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>)
                    runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions.get() == null || runningTransitions.get().get() == null) {
                return;
            }
            ArrayMap map = runningTransitions.get().get();
            View decorView = activity.getWindow().getDecorView();
            if (map.containsKey(decorView)) {
                map.remove(decorView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
