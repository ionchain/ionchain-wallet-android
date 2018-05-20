package org.ionchain.wallet.ui.main;

import android.os.Bundle;

import com.fast.lib.immersionbar.ImmersionBar;

import org.ionchain.wallet.R;
import org.ionchain.wallet.ui.comm.BaseFragment;

public class UserCenterFragment extends BaseFragment {


    @Override
    protected void immersionInit() {
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .statusBarColor(R.color.qmui_config_color_blue)
                .navigationBarColor(R.color.black,0.5f)
                .fitsSystemWindows(true)
                .init();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_user_center);

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.drawable.qmui_icon_topbar_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.tab_user_center;
    }
}
