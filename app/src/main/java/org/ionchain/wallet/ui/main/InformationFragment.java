package org.ionchain.wallet.ui.main;

import android.os.Bundle;
import android.widget.TextView;

import com.fast.lib.immersionbar.ImmersionBar;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.google.gson.reflect.TypeToken;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.helper.RequestHelper;
import org.ionchain.wallet.ui.comm.BaseFragment;

import butterknife.BindView;

public class InformationFragment extends BaseFragment {

    @BindView(R.id.infoTv)
    TextView infoTv;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case 100:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        infoTv.setText(responseModel.getData());
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
        setContentView(R.layout.fragment_information);

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        showProgressDialog();
        //RequestHelper.sendHttpGet(this,"https://api.douban.com/v2/book/1220562",null,new TypeToken<ResponseModel<String>>(){}.getType(),100);

        //测式  正确格式参考上一条
        RequestHelper.sendHttpGet(this,"https://api.douban.com/v2/book/1220562",null,new TypeToken<String>(){}.getType(),100);

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
        return R.string.tab_info;
    }
}
