package org.ionchain.wallet.ui.login;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;

import com.fast.lib.event.CommonEvent;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiArticle;
import org.ionchain.wallet.comm.api.ApiLogin;
import org.ionchain.wallet.comm.api.request.ViewParm;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.comm.utils.SPUtils;
import org.ionchain.wallet.model.UserModel;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.login.forget.ForgetPasswordActivity;
import org.ionchain.wallet.ui.login.register.RegisterActivity;

import butterknife.BindView;

public class LoginActivity extends BaseActivity implements TextWatcher{

    @BindView(R.id.mobileEt)
    AppCompatEditText mobileEt;

    @BindView(R.id.pwdEt)
    AppCompatEditText pwdEt;

    @BindView(R.id.loginBtn)
    Button loginBtn;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.loginBtn:
                    login();
                    break;
                case R.id.forgetTv:
                    transfer(ForgetPasswordActivity.class);
                    break;
                case 100:
                    dismissProgressDialog();
                    Log.e("wallet","OKOKOKOKOKO"+obj.toString());
                    if(obj == null)
                        return;

                    ResponseModel<UserModel> responseModel2 = (ResponseModel)obj;
                    if(!verifyStatus(responseModel2)){
                        ToastUtil.showShortToast(responseModel2.getMsg());
                        return;
                    }

                    UserModel userModel = responseModel2.getData();

                    SPUtils.put(Global.mContext, Comm.user, Global.mGson.toJson(userModel));
                    Global.user = userModel;

                    EventBus.getDefault().post(new CommonEvent(Comm.user_info_refresh_type,null));
                    finish();




                    //to do login OK
                    break;
                case R.id.regTv:
                    transfer(RegisterActivity.class);
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
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.regTv);
        setOnClickListener(R.id.forgetTv);
        setOnClickListener(R.id.loginBtn);
        mobileEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);


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
        return R.string.activity_login_title;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String mobilestr = mobileEt.getText().toString().trim();
        String pwdstr = pwdEt.getText().toString().trim();

        if(!TextUtils.isEmpty(mobilestr) && !TextUtils.isEmpty(pwdstr)){
            loginBtn.setEnabled(true);
        }else{
            loginBtn.setEnabled(false);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void login(){
        String mobilestr = mobileEt.getText().toString().trim();
        String pwdstr = pwdEt.getText().toString().trim();


        if(TextUtils.isEmpty(mobilestr)){
            ToastUtil.showShortToast("手机号码不能为空");
            return;
        }

        if(TextUtils.isEmpty(pwdstr)){
            ToastUtil.showShortToast("密码不能为空");
            return;
        }

        ViewParm viewParm = new ViewParm(LoginActivity.this,null,new TypeToken<ResponseModel<UserModel>>(){}.getType(),100);
//        ApiLogin.sendSmsCode("18764266691",viewParm);
//        ApiLogin.register("18764266691","1234","123456Xyx","",viewParm);
//        ApiLogin.login("18764266691","123456Xyx",viewParm);
            ApiLogin.login(mobilestr,pwdstr,viewParm);
            showProgressDialog();
//        ApiLogin.sendSmsCode("18764266691",viewParm);
//        ApiLogin.updatePassWord("18764266691","1234","1234567Xyx",viewParm);

//        ApiArticle.getArticle("1","1","10",viewParm);
//        ApiArticle.viewArticle("1",viewParm);
//        ApiArticle.praiseArticle("1","1",viewParm);

    }
}
