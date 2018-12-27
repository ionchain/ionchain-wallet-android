package org.ionchain.wallet.mvp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.widget.RadioGroup;

import com.ionc.wallet.sdk.utils.Logger;

import org.ionchain.wallet.R;
import org.ionchain.wallet.helper.ActivityHelper;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.mvp.view.fragment.DevicesFragment;
import org.ionchain.wallet.mvp.view.fragment.HomeFragment;
import org.ionchain.wallet.mvp.view.fragment.MineFragment;
import org.ionchain.wallet.mvp.view.fragment.ShopFragment;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbsBaseActivity implements RadioGroup.OnCheckedChangeListener {

    private long mExitTime = 0;

    private RadioGroup mRadioGroup;


    private HomeFragment mAssetFragment;
    private MineFragment mMineFragment;
    private DevicesFragment mDevicesFragment;
    private ShopFragment mShopFragment;

    private List<Fragment> mFragments = new ArrayList<>();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private int mPostion;

    private MyOrientoinListener myOrientoinListener;


    @Override
    protected void initData() {
        //初始化 fragment
        mAssetFragment = new HomeFragment();
        mMineFragment = new MineFragment();
        mDevicesFragment = new DevicesFragment();
        mShopFragment = new ShopFragment();
        mFragments.add(mAssetFragment);
        mFragments.add(mDevicesFragment);
        mFragments.add(mShopFragment);
        mFragments.add(mMineFragment);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fm_contariner, mAssetFragment);
        mFragmentTransaction.commit();
    }

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void initView() {
//        myOrientoinListener = new MyOrientoinListener(this);
//        boolean autoRotateOn = (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
//        //检查系统是否开启自动旋转
//        if (autoRotateOn) {
//            myOrientoinListener.enable();
//        }
        mRadioGroup = findViewById(R.id.rg_main);
        mRadioGroup.setOnCheckedChangeListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时取消监听
        myOrientoinListener.disable();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showShortToast(getString(R.string.exit_app));
                mExitTime = System.currentTimeMillis();
            } else {
                ActivityHelper.getHelper().finishAllActivity();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        hideNow();
        switch (checkedId) {
            case R.id.rb_asset:
                mPostion = 0;
                break;
            case R.id.rb_devices:
                mPostion = 1;
                break;
            case R.id.rb_shop:
                mPostion = 2;
                break;
            case R.id.rb_mine:
                mPostion = 3;
                break;
        }
        showNext();
        mFragmentTransaction.commit();
    }

    private void hideNow() {
        if (!mFragments.get(mPostion).isAdded()) {
            mFragmentTransaction.add(R.id.fm_contariner, mFragments.get(mPostion));
        } else {
            mFragmentTransaction.hide(mFragments.get(mPostion));
        }
    }

    private void showNext() {
        if (!mFragments.get(mPostion).isAdded()) {
            mFragmentTransaction.add(R.id.fm_contariner, mFragments.get(mPostion));
        } else {
            mFragmentTransaction.show(mFragments.get(mPostion));
        }
    }

    class MyOrientoinListener extends OrientationEventListener {

        private String TAG = "MyOrientoinListener";
        public MyOrientoinListener(Context context) {
            super(context);
        }

        public MyOrientoinListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            Logger.i(TAG, "orention" + orientation);
            int screenOrientation = getResources().getConfiguration().orientation;
            if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    Logger.i(TAG, "设置竖屏");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            } else if (orientation > 225 && orientation < 315) { //设置横屏
                Logger.i(TAG, "设置横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
                Logger.i(TAG, "反向横屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
            } else if (orientation > 135 && orientation < 225) {
                Logger.i(TAG, "反向竖屏");
                if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
            }
        }
    }

}
