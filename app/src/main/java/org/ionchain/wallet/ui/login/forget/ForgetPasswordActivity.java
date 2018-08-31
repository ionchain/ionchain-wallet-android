package org.ionchain.wallet.ui.login.forget;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;

public class ForgetPasswordActivity extends BaseActivity {


    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
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
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);

    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.register_container,new ForgetPasswordFirstFragment());
        if (!isFinishing()){
            transaction.commitAllowingStateLoss();
        }
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

}
