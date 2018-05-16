package org.ionchain.wallet.ui.main;

import android.os.Bundle;
import android.widget.ImageView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.config.ImgLoader;
import org.ionchain.wallet.ui.comm.BaseFragment;

import butterknife.BindView;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.testIv)
    ImageView testIv;

    @Override
    protected void immersionInit() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_home);

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        ImgLoader.loadStringRes(testIv,Comm.TESTIMG,null,null);
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
        return R.string.tab_home;
    }
}
