package org.ionchain.wallet.mvp.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import org.ionchain.wallet.R;
import org.ionchain.wallet.helper.ActivityHelper;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.mvp.view.fragment.DevicesFragment;
import org.ionchain.wallet.mvp.view.fragment.AssetFragment;
import org.ionchain.wallet.mvp.view.fragment.MineFragment;
import org.ionchain.wallet.mvp.view.fragment.ShopFragment;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbsBaseActivity implements RadioGroup.OnCheckedChangeListener {

    private long mExitTime = 0;

    private RadioGroup mRadioGroup;


    private AssetFragment mAssetFragment;
    private MineFragment mMineFragment;
    private DevicesFragment mDevicesFragment;
    private ShopFragment mShopFragment;

    private List<Fragment> mFragments = new ArrayList<>();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private int mPostion;



    @Override
    protected void initData() {
        //初始化 fragment
        mAssetFragment = new AssetFragment();
        mMineFragment = new MineFragment();
        mDevicesFragment = new DevicesFragment();
        mShopFragment = new ShopFragment();
        mFragments.add(mAssetFragment);
        mFragments.add(mDevicesFragment);
        mFragments.add(mShopFragment);
        mFragments.add(mMineFragment);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fm_container, mAssetFragment);
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
//        myOrientoinListener.disable();
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
            mFragmentTransaction.add(R.id.fm_container, mFragments.get(mPostion));
        } else {
            mFragmentTransaction.hide(mFragments.get(mPostion));
        }
    }

    private void showNext() {
        if (!mFragments.get(mPostion).isAdded()) {
            mFragmentTransaction.add(R.id.fm_container, mFragments.get(mPostion));
        } else {
            mFragmentTransaction.show(mFragments.get(mPostion));
        }
    }
    public static void reStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
