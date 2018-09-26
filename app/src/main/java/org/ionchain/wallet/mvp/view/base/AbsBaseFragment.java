package org.ionchain.wallet.mvp.view.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fast.lib.logger.Logger;

import org.ionchain.wallet.immersionbar.ImmersionBar;

import java.io.Serializable;


/**
 * author  binny
 * date 5/9
 */
public abstract class AbsBaseFragment extends Fragment {

    protected AbsBaseActivity mActivity;
    protected final String TAG;
    protected View mContainerView;
    protected boolean mIsFirstBindData = true;
    protected ImmersionBar mImmersionBar;
    protected boolean isRefreshing=false;//listview是否可用
    public AbsBaseFragment() {
        this.TAG = this.getClass().getSimpleName();
    }


    /*
     * 防止频繁请求网络
     * */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mIsFirstBindData && mContainerView != null && isVisibleToUser) {
            mIsFirstBindData = false;
            initData();//创建其他fragment 时  不加载数据，当 该fragment 可见时，加载数据
        }
    }


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
            //防止重复创建视图
            return mContainerView;
        }

        /*
         * 创建视图
         * */
        /*
         * 如果第一次创建时，可见，则加载数据，绑定数据
         * */


        mContainerView = inflater.inflate(getFragmentLayout(), container, false);
        initView(mContainerView);
        mImmersionBar = ImmersionBar.with(getActivity(), this);
        initImmersionBar();
        setListener();
        if (getUserVisibleHint()) {
            mIsFirstBindData = false;
            initData();//第一个 可见的 fragment 要加载数据
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
    protected void initImmersionBar() {

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
        Intent intent = new Intent(getActivity(), clazz);
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
            Intent intent = new Intent(getActivity(), clazz);
            intent.putExtra(params, obj);
            startActivityForResult(intent, 0);
        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }


}
