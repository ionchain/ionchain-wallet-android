package org.ionchain.wallet.ui.login.forget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.google.gson.reflect.TypeToken;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiLogin;
import org.ionchain.wallet.comm.api.request.ViewParm;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.ui.comm.BaseFragment;

import butterknife.BindView;

public class ForgetPasswordThirdFragment extends BaseFragment implements TextWatcher {

    @BindView(R.id.passwordEt1)
    AppCompatEditText passwordEt1;

    @BindView(R.id.passwordEt2)
    AppCompatEditText passwordEt2;

    @BindView(R.id.submitBtn)
    Button submitBtn;

    @BindView(R.id.haveAccountTv)
    TextView haveAccountTv;

    @BindView(R.id.loginTv)
    TextView loginTv;

    String mMobileStr;
    String mVerifyCodeStr;


    public static ForgetPasswordThirdFragment newInstance(String mobile, String verifyCode) {

        Bundle args = new Bundle();

        ForgetPasswordThirdFragment fragment = new ForgetPasswordThirdFragment();
        args.putString(Comm.SERIALIZABLE_DATA, mobile);
        args.putString(Comm.SERIALIZABLE_DATA1, verifyCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    break;
                case R.id.loginTv:
                    getActivity().finish();
                    break;
                case R.id.submitBtn:
                    if (passwordEt1.getText().toString().trim().equals(passwordEt2.getText().toString().trim()))
                    {
                        showKeyboard(false);
                    }else {
                        ToastUtil.showShortToast("两次输入密码不一致！！");
                    }


                    ViewParm viewParm = new ViewParm(null,this, new TypeToken<ResponseModel<String>>(){}.getType(),100);
                    ApiLogin.updatePassWord(mMobileStr,mVerifyCodeStr,passwordEt1.getText().toString().trim(),viewParm);

                    showProgressDialog();


                    break;
                case 100:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }
                    getActivity().finish();

                    ToastUtil.showShortToast("修改成功,请登录");


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_register_third);

        Bundle arguments = getArguments();
        if (arguments == null) return;
        mMobileStr = arguments.getString(Comm.SERIALIZABLE_DATA, "");
        mVerifyCodeStr = arguments.getString(Comm.SERIALIZABLE_DATA1, "");

        loginTv.setVisibility(View.GONE);
        haveAccountTv.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.loginTv);
        setOnClickListener(R.id.submitBtn);
        passwordEt1.addTextChangedListener(this);
        passwordEt2.addTextChangedListener(this);
    }


    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getActivity().getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getActivity().getCurrentFocus(), 0);
            }
        } else {
            if (getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
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
        return R.mipmap.arrow_back_blue;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.setPassWord;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String passwordStr1 = passwordEt1.getText().toString().trim();
        String passwordStr2 = passwordEt2.getText().toString().trim();

        if(!TextUtils.isEmpty(passwordStr1)&&!TextUtils.isEmpty(passwordStr2)){
            submitBtn.setEnabled(true);
        }else{
            submitBtn.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
