package org.ionchain.wallet.ui.login.register;

import android.content.Context;
import android.content.SharedPreferences;
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
import org.ionchain.wallet.comm.utils.CheckUtil;
import org.ionchain.wallet.ui.comm.BaseFragment;
import org.ionchain.wallet.ui.comm.WebViewActivity;
import org.ionchain.wallet.ui.login.view.CountTimer;

import java.sql.Date;

import butterknife.BindView;


/**
 * 注册界面
 */
public class RegisterFirstFragment extends BaseFragment implements TextWatcher {

    @BindView(R.id.mobileEt)
    AppCompatEditText mobileEt;

    @BindView(R.id.invitationCodeEt)
    AppCompatEditText invitationCodeEt;

    @BindView(R.id.nextBtn)
    Button nextBtn;

    private SharedPreferences spf;
    private CountTimer mGetCodeTimer;
    private long old_time;
    private int tick_time;

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    getActivity().finish();
                    break;
                case R.id.loginTv:
                    getActivity().finish();
                    break;
                case R.id.agreementTv:
                    transfer(WebViewActivity.class, Comm.SERIALIZABLE_DATA, Comm.URL_AGREE, Comm.SERIALIZABLE_DATA1, "条款");
                    break;
                case R.id.nextBtn:
                    showKeyboard(false);

                    if (CheckUtil.isMobileNO(mobileEt.getText().toString().trim())){

                        if (mGetCodeTimer == null) {
                            mGetCodeTimer = new CountTimer(nextBtn, 30,R.color.white,R.color.white);
                            mGetCodeTimer.start();
                        } else {
                            int mTickTime = CountTimer.TIME_REST;
                            if (mTickTime == 1) {
                                mGetCodeTimer.cancel();
                                mGetCodeTimer = new CountTimer(nextBtn, 30,R.color.white,R.color.white);
                                mGetCodeTimer.start();
                            }
                        }

                        replaceFragment(mobileEt.getText().toString().trim(),invitationCodeEt.getText().toString().trim());
                    }else {
                        ToastUtil.showShortToast("手机号格式错误！！");
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
        setContentView(R.layout.fragment_register_first);
        initTime();
    }

    private void initTime() {
        spf = getActivity().getSharedPreferences("registered_timer", Context.MODE_PRIVATE);
        old_time = spf.getLong("old_time", 0);
        tick_time = spf.getInt("tick_time", 0);
        Date date = new Date(System.currentTimeMillis());
        if ((date.getTime() - old_time) / 1000 < tick_time) {
            if (mGetCodeTimer == null) {
                mGetCodeTimer = new CountTimer(nextBtn, (int) (tick_time - (date.getTime() - old_time) / 1000) ,R.color.white,R.color.white);
                mGetCodeTimer.start();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tick_time = spf.getInt("tick_time", 0);
        old_time = spf.getLong("old_time", 0);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.agreementTv);
        setOnClickListener(R.id.loginTv);
        setOnClickListener(R.id.nextBtn);
        mobileEt.addTextChangedListener(this);
    }

    private void replaceFragment(String mobile, String invitationCode) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RegisterSecondFragment registerSecondFragment = RegisterSecondFragment.newInstance(mobile,invitationCode);
        transaction.add(R.id.register_container, registerSecondFragment)
                .hide(RegisterFirstFragment.this).show(registerSecondFragment);
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
    public void onDestroy() {
        Date date = new Date(System.currentTimeMillis());
        if (mGetCodeTimer != null) {
            mGetCodeTimer.cancel();
        }
        spf.edit().putLong("old_time", date.getTime()).putInt("tick_time", CountTimer.TIME_REST).apply();
        super.onDestroy();
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
        return R.string.register;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String mobilestr = mobileEt.getText().toString().trim();

        if(!TextUtils.isEmpty(mobilestr)){
            nextBtn.setEnabled(true);
        }else{
            nextBtn.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
