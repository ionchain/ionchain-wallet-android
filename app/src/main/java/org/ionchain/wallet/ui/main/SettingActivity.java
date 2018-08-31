package org.ionchain.wallet.ui.main;

import android.os.Bundle;

import com.fast.lib.event.CommonEvent;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.comm.utils.SPUtils;
import org.ionchain.wallet.ui.comm.BaseActivity;

public class SettingActivity extends BaseActivity {


    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.exitBtn:

                    SPUtils.remove(Global.mContext, Comm.user);
                    Global.user = null;
                    EventBus.getDefault().post(new CommonEvent(Comm.user_info_refresh_type,null));
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
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.exitBtn);
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
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_setting_title;
    }
}
