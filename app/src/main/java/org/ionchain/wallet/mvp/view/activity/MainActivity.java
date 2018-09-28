package org.ionchain.wallet.mvp.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;


import org.ionchain.wallet.R;
import org.ionchain.wallet.manager.ActivityHelper;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.mvp.view.fragment.DevicesFragment;
import org.ionchain.wallet.mvp.view.fragment.HomeFragment;
import org.ionchain.wallet.mvp.view.fragment.MineFragment;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbsBaseActivity implements RadioGroup.OnCheckedChangeListener {

    private long mExitTime = 0;

    private RadioGroup mRadioGroup;


    private HomeFragment mAssetFragment;
    private MineFragment mMineFragment;
    private DevicesFragment mDevicesFragment;
    private List<Fragment> mFragments = new ArrayList<>();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private int mPostion;


    @Override
    protected void initData() {
        //初始化 fragment
        mAssetFragment = new HomeFragment();
        mMineFragment = new MineFragment();
        mDevicesFragment = new DevicesFragment();
        mFragments.add(mAssetFragment);
        mFragments.add(mDevicesFragment);
        mFragments.add(mMineFragment);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fm_contariner, mAssetFragment);
        mFragmentTransaction.commit();
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        mRadioGroup = findViewById(R.id.rg_main);
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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
            case R.id.rb_mine:
                mPostion = 2;
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

}
