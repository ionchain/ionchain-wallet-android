package org.ionchain.wallet.ui.login;

import android.os.Bundle;

import com.fast.lib.immersionbar.ImmersionBar;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;

public class LoginActivity extends BaseActivity {


    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.cancelTv:
                    finish();
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
    protected void onCreate(Bundle savedInstanceState) {
        setSystemBar(false);
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .statusBarView(R.id.statusView)
                .navigationBarColor(R.color.black,0.5f)
                .fitsSystemWindows(false)
                .init();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.cancelTv);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

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
        return R.string.activity_test;
    }
}
