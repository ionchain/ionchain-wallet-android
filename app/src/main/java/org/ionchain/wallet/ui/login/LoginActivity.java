package org.ionchain.wallet.ui.login;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.comm.BaseActivity;

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
        mobileEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        loginBtn.setOnClickListener(this);

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
}
