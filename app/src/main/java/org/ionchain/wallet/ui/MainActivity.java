package org.ionchain.wallet.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.manager.ActivityHelper;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.main.HomeFragment;
import org.ionchain.wallet.ui.main.UserCenterFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private long mExitTime = 0;

    private RadioGroup mRadioGroup;


    private HomeFragment mAssetFragment;
    private UserCenterFragment mMineFragment;
    private List<Fragment> mFragments = new ArrayList<>();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private int mPostion;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {
                case R.id.navigationBack:
                    finish();
                    break;
                case 0:
                    dismissProgressDialog();
                    if (obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel) obj;
                    if (!verifyStatus(responseModel)) {
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setSystemBar(false);
        super.onCreate(savedInstanceState);
        mImmersionBar
                .statusBarColor("#3574FA")
                .init();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mRadioGroup = findViewById(R.id.rg_main);
        mRadioGroup.setOnCheckedChangeListener(this);

        //初始化 fragment
        mAssetFragment = new HomeFragment();
        mMineFragment = new UserCenterFragment();
        mFragments.add(mAssetFragment);
        mFragments.add(mMineFragment);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.fm_contariner, mAssetFragment);
        mFragmentTransaction.commit();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }


    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return 0;
    }

    @Override
    public int getActivityTitleContent() {
        return 0;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showShortToast(getString(R.string.exit_app));
                mExitTime = System.currentTimeMillis();
            } else {
                ActivityHelper.getHelper().AppExit(this);
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
            case R.id.rb_mine:
                mPostion = 1;
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
