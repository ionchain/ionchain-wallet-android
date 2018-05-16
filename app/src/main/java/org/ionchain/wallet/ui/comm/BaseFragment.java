package org.ionchain.wallet.ui.comm;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fast.lib.base.LibFragment;
import com.fast.lib.logger.Logger;
import com.fast.lib.okhttp.ResponseBean;

import org.greenrobot.eventbus.EventBus;
import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.model.ResponseModel;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by siberiawolf on 16/08/2016.
 */
public abstract class BaseFragment extends LibFragment {

    protected String TAG;
    protected View mContentView;

    protected Toolbar mToolbar;
    public TextView title;
    public View lineView;

    private Menu menu;

    private boolean isClose = true;

    private Unbinder unbinder;


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

                        try{
                            HashMap map =  Global.mGson.fromJson(result,HashMap.class);
                            ResponseModel responseModel = new ResponseModel();
                            responseModel.setCode(map.get("code").toString());
                            responseModel.setMsg(map.get("msg").toString());
                            responseModel.setData(result);
                            jsonObject = responseModel;
                        }catch (Throwable e1){
                            Logger.e(TAG, e);
                            Logger.e(TAG, e1);
                            ResponseModel jsonMsgModel = new ResponseModel();
                            jsonMsgModel.setCode("800");
                            jsonMsgModel.setMsg("Json解析的错误");
                            jsonMsgModel.setData(result);
                            jsonObject = jsonMsgModel;
                        }
                    }finally {
                        handleMessage(successfulBean.refreshType, jsonObject);
                    }

                    break;
                default:
                    handleMessage(what, obj);
                    break;
            }
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void handleMessage(int what, Object obj) {
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            onUserVisible();
        }

        if ((isVisibleToUser && isResumed())) {
            onResume();
        }
    }


    protected abstract void immersionInit();

    public void initToolbar() {
        try {
            mToolbar = (Toolbar) mContentView.findViewById(R.id.toolbar);

            if (mToolbar == null)
                return;

            title = (TextView) mContentView.findViewById(R.id.TITLE);
            lineView = mContentView.findViewById(R.id.lineView);

            if(title != null)
                if (getActivityTitleContent() != 0) {
                    title.setText(getActivityTitleContent());
                } else {
                    title.setText("");
                }


            mToolbar.setTitle("");
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

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
            Logger.e(e,TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);
        TAG = this.getClass().getSimpleName();
        // 避免多次从xml中加载布局文件
        if (mContentView == null) {

            if (getActivityMenuRes() != 0)
                setHasOptionsMenu(true);

            initView(savedInstanceState);

            processLogic(savedInstanceState);
            setListener();

            if (isRegisterEventBus()) {
                EventBus.getDefault().register(this);
            }


        } else {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }


        return mContentView;
    }

    protected void setContentView(@LayoutRes int layoutResID) {
        try {
            mContentView = LayoutInflater.from(getActivity()).inflate(layoutResID, null);
            unbinder = ButterKnife.bind(this, mContentView);
            initToolbar();

        } catch (Throwable e) {
            Logger.e(e,TAG);
        }

    }

    protected View getContentView() {
        try {

            return mContentView;

        } catch (Throwable e) {
            Logger.e(e,TAG);
        }

        return null;

    }

    public boolean verifyStatus(ResponseModel responseModel) {
        try {
            if (!TextUtils.isEmpty(responseModel.getCode()) && responseModel.getCode().equals(Comm.SUCCESS)) {
                return true;
            } else {
                return false;
            }

        } catch (Throwable e) {
            Logger.e(e,TAG);
        }

        return true;
    }

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
        return (VT) mContentView.findViewById(id);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(View rootView,@IdRes int id) {
        return (VT) rootView.findViewById(id);
    }

    /**
     * 初始化View控件
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
    protected abstract void processLogic(Bundle savedInstanceState);

    /**
     * 当fragment对用户可见时，会调用该方法，可在该方法中懒加载网络数据
     */
    protected abstract void onUserVisible();

    public abstract int getActivityMenuRes();

    public abstract int getHomeAsUpIndicatorIcon();

    public abstract int getActivityTitleContent();


    public void setNavigationIcon(@DrawableRes int resId) {
        try {
            if(resId == 0){
                mToolbar.setNavigationIcon(null);
            }else{
                mToolbar.setNavigationIcon(resId);
            }

        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void setTitleBackground(@DrawableRes int resId) {
        try {
            title.setBackgroundResource(resId);
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void setTitle(String resId) {
        try {
            if(title != null)
                title.setText(resId);
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (getActivityMenuRes() == 0) {
            return;
        }
        inflater.inflate(getActivityMenuRes(), menu);

        this.menu = menu;
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
        return true;
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
    public void onDestroyView() {
        super.onDestroyView();
        Logger.i(TAG + "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i(TAG + "onDestroy");

        unbinder.unbind();

        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        Logger.i(TAG + "==onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.i(TAG + "==onResume");
        if (getUserVisibleHint()) {
            immersionInit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.i(TAG + "==onPause");
    }

    @Override
    public void onStop() {

        super.onStop();
        Logger.i(TAG + "==onStop");
    }



}
