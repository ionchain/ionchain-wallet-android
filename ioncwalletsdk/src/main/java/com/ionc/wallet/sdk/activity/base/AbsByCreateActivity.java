package com.ionc.wallet.sdk.activity.base;

import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.R;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.callback.OnImportMnemonicCallback;
import com.ionc.wallet.sdk.callback.OnSimulateTimeConsume;
import com.ionc.wallet.sdk.utils.Logger;
import com.ionc.wallet.sdk.utils.SoftKeyboardUtil;
import com.ionc.wallet.sdk.utils.ToastUtil;

import static com.ionc.wallet.sdk.utils.StringUtils.check;

public abstract class AbsByCreateActivity extends BaseActivity implements View.OnClickListener, TextWatcher, OnSimulateTimeConsume, OnImportMnemonicCallback {

    private RelativeLayout header;
    private ImageView back;
    private AppCompatEditText walletNameEt;
    private AppCompatEditText pwdEt;
    private AppCompatEditText resetPwdEt;
    private Button createBtn;


    private String walletnamestr;
    private String pass;
    private String resetpass;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_wallet;
    }


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-01-08 15:11:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        header = (RelativeLayout) findViewById(R.id.header);
        back = (ImageView) findViewById(R.id.back);
        walletNameEt = (AppCompatEditText) findViewById(R.id.walletNameEt);
        pwdEt = (AppCompatEditText) findViewById(R.id.pwdEt);
        resetPwdEt = (AppCompatEditText) findViewById(R.id.resetPwdEt);
        createBtn = (Button) findViewById(R.id.createBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                 * 从数据库比对，重复检查
                 * */
                if (null != IONCWalletSDK.getInstance().getWalletByName(walletnamestr)) {
                    Toast.makeText(mActivity.getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!check(resetpass) || !check(pass)) {
                    ToastUtil.showToastLonger("密码不符合要求！");
                    return;
                }
                if (!resetpass.equals(pass)) {
                    ToastUtil.showShortToast("密码和重复密码必须相同");
                    return;
                }

                showProgress("正在创建钱包……");
                IONCWalletSDK.getInstance().simulateTimeConsuming(AbsByCreateActivity.this);

            }
        });
        walletNameEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        resetPwdEt.addTextChangedListener(this);
        createBtn.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-01-08 15:11:15 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == createBtn) {
            /*
             * 从数据库比对，重复检查
             * */
            if (null != IONCWalletSDK.getInstance().getWalletByName(walletnamestr)) {
                Toast.makeText(getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!check(resetpass) || !check(pass)) {
                ToastUtil.showToastLonger("密码不符合要求！");
                return;
            }
            if (!resetpass.equals(pass)) {
                ToastUtil.showShortToast("密码和重复密码必须相同");
                return;
            }

            showProgress("正在创建钱包……");
            IONCWalletSDK.getInstance().simulateTimeConsuming(AbsByCreateActivity.this);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (walletNameEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包名字");
            return;
        }
        if (pwdEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包密码");
            return;
        }
        if (resetPwdEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包重复密码");
            return;
        }
        walletnamestr = walletNameEt.getText().toString().trim();
        pass = pwdEt.getText().toString().trim();
        resetpass = resetPwdEt.getText().toString().trim();

        if (!TextUtils.isEmpty(walletnamestr) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(resetpass)) {
            createBtn.setEnabled(true);
            createBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
        } else {
            createBtn.setEnabled(false);
            createBtn.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }

    @Override
    public void onSimulateFinish() {
        IONCWalletSDK.getInstance().createBip39Wallet(walletnamestr, pass, AbsByCreateActivity.this);
    }

    @Override
    public void onImportMnemonicSuccess(WalletBean walletBean) {
        Logger.i("onCreateSuccess: " + walletBean);
        hideProgress();
        IONCWalletSDK.getInstance().saveWallet(walletBean);
        SoftKeyboardUtil.hideSoftKeyboard(this);
        onSDKCreateSuccess(walletBean);
    }

    @Override
    public void onImportMnemonicFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger(error);
        SoftKeyboardUtil.hideSoftKeyboard(this);
        onSDKCreateFailure(error);
    }
}
