package org.ionchain.wallet.ui.main;

import android.os.Bundle;

import com.fast.lib.immersionbar.ImmersionBar;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseFragment;
import org.ionchain.wallet.ui.login.LoginActivity;

public class UserCenterFragment extends BaseFragment {

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.loginRegTv:
                    transfer(LoginActivity.class);
                    break;
                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

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
        setOnClickListener(R.id.loginRegTv);

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
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.tab_user_center;
    }
}
