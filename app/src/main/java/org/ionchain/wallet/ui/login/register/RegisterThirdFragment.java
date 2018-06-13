package org.ionchain.wallet.ui.login.register;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.ui.comm.BaseFragment;

import butterknife.BindView;

public class RegisterThirdFragment extends BaseFragment implements TextWatcher {

    @BindView(R.id.passwordEt1)
    AppCompatEditText passwordEt1;

    @BindView(R.id.passwordEt2)
    AppCompatEditText passwordEt2;

    @BindView(R.id.submitBtn)
    Button submitBtn;


    public static RegisterThirdFragment newInstance(String mobile, String invitationCode,String verifyCode) {

        Bundle args = new Bundle();

        RegisterThirdFragment fragment = new RegisterThirdFragment();
        args.putString(Comm.SERIALIZABLE_DATA, mobile);
        args.putString(Comm.SERIALIZABLE_DATA1, invitationCode);
        args.putString(Comm.SERIALIZABLE_DATA2, verifyCode);
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

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_register_third);
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
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.register;
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
