package org.ionchain.wallet.mvp.view.activity;

import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.ui.login.forget.ForgetPasswordActivity;
import org.ionchain.wallet.ui.login.register.RegisterActivity;

public class LoginActivity extends AbsBaseActivity implements TextWatcher{

    private RelativeLayout toolbarlayout;
    private ImageView back;
    private TextView regTv;
    private ImageView headIv;
    private AppCompatEditText mobileEt;
    private AppCompatEditText pwdEt;
    private Button loginBtn;
    private TextView forgetTv;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-26 22:19:19 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        toolbarlayout = (RelativeLayout)findViewById( R.id.toolbarlayout );
        back = (ImageView)findViewById( R.id.back );
        regTv = (TextView)findViewById( R.id.regTv );
        headIv = (ImageView)findViewById( R.id.headIv );
        mobileEt = (AppCompatEditText)findViewById( R.id.mobileEt );
        pwdEt = (AppCompatEditText)findViewById( R.id.pwdEt );
        loginBtn = (Button)findViewById( R.id.loginBtn );
        forgetTv = (TextView)findViewById( R.id.forgetTv );
    }



    @Override
    protected void initData() {
        mImmersionBar.titleBar(findViewById(R.id.header)).statusBarDarkFont(true).execute();
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        regTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(RegisterActivity.class);
            }
        });
        forgetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ForgetPasswordActivity.class);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mobileEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
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


    }
}
