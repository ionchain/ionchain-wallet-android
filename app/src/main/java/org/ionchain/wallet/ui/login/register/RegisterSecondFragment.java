package org.ionchain.wallet.ui.login.register;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
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

public class RegisterSecondFragment extends BaseFragment implements TextWatcher {


    @BindView(R.id.hideEt)
    AppCompatEditText hideEt;

    @BindView(R.id.codeTv1)
    TextView codeTv1;

    @BindView(R.id.codeTv2)
    TextView codeTv2;

    @BindView(R.id.codeTv3)
    TextView codeTv3;

    @BindView(R.id.codeTv4)
    TextView codeTv4;

    @BindView(R.id.tipsTv)
    TextView tipsTv;

    private String mMobileStr = "";
    private String mInvitationCodeStr = "";

    public static RegisterSecondFragment newInstance(String mobile, String invitationCode) {

        Bundle args = new Bundle();

        RegisterSecondFragment fragment = new RegisterSecondFragment();
        args.putString(Comm.SERIALIZABLE_DATA, mobile);
        args.putString(Comm.SERIALIZABLE_DATA1, invitationCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {
                case R.id.navigationBack:
                    break;
                case R.id.loginTv:
                    getActivity().finish();
                    break;
                case 100:
                    dismissProgressDialog();
                    if (obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel) obj;
                    if (!verifyStatus(responseModel)) {
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }

                    ToastUtil.showShortToast(responseModel.getMsg());


                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }



    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_register_second);

        Bundle arguments = getArguments();
        if (arguments == null) return;
        mMobileStr = arguments.getString(Comm.SERIALIZABLE_DATA, "");
        mInvitationCodeStr = arguments.getString(Comm.SERIALIZABLE_DATA1, "");

        ViewParm viewParm = new ViewParm(null,this,new TypeToken<ResponseModel<String>>(){}.getType(),100);
        ApiLogin.sendSmsCode(mMobileStr,viewParm);
        showProgressDialog();

        tipsTv.setText("我们已经向"+mMobileStr+"发送验证码短信，请查收并输入短信验证码");
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.loginTv);
        hideEt.addTextChangedListener(this);
    }


    private void replaceFragment(String mobile, String invitationCode, String verifyCode) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RegisterThirdFragment registerThirdFragment = RegisterThirdFragment.newInstance(mobile, invitationCode, verifyCode);
        transaction.add(R.id.register_container, registerThirdFragment)
                .hide(RegisterSecondFragment.this).show(registerThirdFragment);
        if (getActivity() != null && !getActivity().isFinishing()) {
            transaction.commitAllowingStateLoss();
        }
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
    protected void processLogic(Bundle savedInstanceState) {

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
        return R.string.input_code;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String hideEtStr = hideEt.getText().toString().trim();

        char[] arr = hideEtStr.toCharArray();
        codeTv1.setText("");
        codeTv2.setText("");
        codeTv3.setText("");
        codeTv4.setText("");
        for (int i = 0; i < arr.length; i++) {
            if (i == 0) {
                codeTv1.setText(String.valueOf(arr[0]));
            } else if (i == 1) {
                codeTv2.setText(String.valueOf(arr[1]));
            } else if (i == 2) {
                codeTv3.setText(String.valueOf(arr[2]));
            } else if (i == 3) {
                codeTv4.setText(String.valueOf(arr[3]));
                replaceFragment(mMobileStr, mInvitationCodeStr, hideEtStr);
                showKeyboard(false);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
