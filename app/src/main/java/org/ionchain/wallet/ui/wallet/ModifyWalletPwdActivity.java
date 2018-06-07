package org.ionchain.wallet.ui.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;

import com.fast.lib.event.CommonEvent;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.db.WalletDaoTools;
import org.ionchain.wallet.ui.comm.BaseActivity;

import butterknife.BindView;

public class ModifyWalletPwdActivity extends BaseActivity {

    @BindView(R.id.oldPwdEdit)
    AppCompatEditText oldPwdEdit;

    @BindView(R.id.newPwdEt)
    AppCompatEditText newPwdEt;

    @BindView(R.id.resetNewPwdEt)
    AppCompatEditText resetNewPwdEt;

    Wallet mWallet;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.completBtn:

                    String oldpwdstr = oldPwdEdit.getText().toString().trim();
                    String newpwdstr = newPwdEt.getText().toString().trim();
                    String resetnewpwdstr = resetNewPwdEt.getText().toString().trim();

                    if(TextUtils.isEmpty(oldpwdstr)){
                        ToastUtil.showShortToast("当前密码不能为空");
                        return;
                    }

                    if(TextUtils.isEmpty(newpwdstr)){
                        ToastUtil.showShortToast("新密码不能为空");
                        return;
                    }

                    if(!mWallet.getPassword().equals(oldpwdstr)){
                        ToastUtil.showShortToast("旧密码错误");
                        return;
                    }

                    if(!newpwdstr.equals(resetnewpwdstr)){
                        ToastUtil.showShortToast("新密码两次输入不一至,请重新输入");
                        return;
                    }



                    if(!newpwdstr.equals(resetnewpwdstr)){
                        ToastUtil.showShortToast("新密码两次输入不一至,请重新输入");
                        return;
                    }


                    ApiWalletManager.getInstance().editPassWord(mWallet,newpwdstr,walletHandler);
                    showProgressDialog("正在修改密码");


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



    @SuppressLint("HandlerLeak")
    Handler walletHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgressDialog();
            try {
                ApiConstant.WalletManagerType resulit = ApiConstant.WalletManagerType.codeOf(msg.what);
                if (null == resulit) return;
                ResponseModel responseModel = (ResponseModel<String>) msg.obj;
                switch (resulit) {
                    case WALLET_EDIT_PASS:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {

                            Wallet wallet = (Wallet)responseModel.data;
                            WalletDaoTools.updateWallet(wallet);

                            EventBus.getDefault().post(new CommonEvent(Comm.modify_wallet_refresh_type,wallet));

                            ToastUtil.showShortToast("修改密码成功");
                        } else {
                            ToastUtil.showShortToast("修改密码失败");
                        }
                        finish();
                        break;

                    default:
                        break;

                }
            } catch (Throwable e) {
                Log.e(TAG, "handleMessage", e);
            }
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modify_wallet_pwd);

        mWallet = (Wallet) getIntent().getSerializableExtra(Comm.SERIALIZABLE_DATA);

    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_wallet_over;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_modify_wallet_pwd;
    }
}
