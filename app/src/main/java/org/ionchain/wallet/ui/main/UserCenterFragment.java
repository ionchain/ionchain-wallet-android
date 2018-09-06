package org.ionchain.wallet.ui.main;

import android.os.Bundle;
import android.widget.TextView;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.ui.account.MessageCenterActivity;
import org.ionchain.wallet.ui.account.WalletManageActivity;
import org.ionchain.wallet.ui.comm.BaseFragment;
import org.ionchain.wallet.ui.login.LoginActivity;

import butterknife.BindView;

public class UserCenterFragment extends BaseFragment {

    @BindView(R.id.loginRegTv)
    TextView loginRegTv;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.loginRegTv:
                    if(Global.user == null)
                        transfer(LoginActivity.class);
                    else{
                        transfer(SettingActivity.class);
                    }
                    break;
                case R.id.messageCenterRLayout:
                    transfer(MessageCenterActivity.class);
                    break;
                case R.id.walletManageRLayout:
                    transfer(WalletManageActivity.class);
                    break;
                case Comm.user_info_refresh_type:

                    refreshData();

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
        setContentView(R.layout.fragment_user_center);

        mImmersionBar.titleBar(getViewById(R.id.top_view))
                .statusBarDarkFont(true)
                .statusBarColor("#ffffff")
                .init();
//        setDarkStatusBar(mImmersionBar,getViewById(R.id.top_view));
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mImmersionBar.titleBar(getViewById(R.id.top_view))
                    .statusBarDarkFont(true)
                    .init();
        }
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.loginRegTv);
        setOnClickListener(R.id.walletManageRLayout);
        setOnClickListener(R.id.messageCenterRLayout);
        setOnClickListener(R.id.hint_message_num);//消息数量提示
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        refreshData();
    }

    private void refreshData(){
        try{
            /*
             * 移除 进入 此页面时 对用户是否的登录的判断
             * */

//            if(Global.user == null){
//                loginRegTv.setText("注册/登陆  >");
//                return;
//            }
            loginRegTv.setText(Global.user.getUserName());
        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        /*
        * 移除 进入 此页面时 对用户是否的登录的判断
        * */
//        if (Global.user == null) {
//            transfer(LoginActivity.class);
//            getActivity().finish();
//            return;
//        }
    }
    @Override
    protected void onUserVisible() {

    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
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
