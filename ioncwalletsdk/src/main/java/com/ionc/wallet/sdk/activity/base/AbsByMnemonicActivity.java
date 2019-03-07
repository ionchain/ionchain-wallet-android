package com.ionc.wallet.sdk.activity.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.ionc.wallet.sdk.callback.OnUpdatePasswordCallback;
import com.ionc.wallet.sdk.utils.Logger;
import com.ionc.wallet.sdk.utils.StringUtils;
import com.ionc.wallet.sdk.utils.ToastUtil;

import java.util.Arrays;

import static com.ionc.wallet.sdk.utils.RandomUntil.getNum;
import static com.ionc.wallet.sdk.utils.StringUtils.check;

public abstract class AbsByMnemonicActivity extends BaseActivity implements TextWatcher, OnImportMnemonicCallback, OnUpdatePasswordCallback {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_by_mnemonic;
    }

    private RelativeLayout importHeader;
    private ImageView back;
    private AppCompatEditText mnemonic;
    private AppCompatEditText pwdEt;
    private AppCompatEditText repwdEt;
    private Button importBtn;
    private String newPassword;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-10-01 21:39:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        importHeader = (RelativeLayout) findViewById(R.id.import_header);
        back = (ImageView) findViewById(R.id.back);
        mnemonic = (AppCompatEditText) findViewById(R.id.mnemonic);
        pwdEt = (AppCompatEditText) findViewById(R.id.pwdEt);
        repwdEt = (AppCompatEditText) findViewById(R.id.repwdEt);
        importBtn = (Button) findViewById(R.id.importBtn);
    }

    @Override
    protected void setListener() {
        super.setListener();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mnemonic.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        repwdEt.addTextChangedListener(this);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mnemonic.getText() == null) {
                    ToastUtil.showToastLonger("助记词不能为空！");
                    return;
                }
                if (pwdEt.getText() == null) {
                    ToastUtil.showToastLonger("密码不能为空！");
                    return;
                }
                if (repwdEt.getText() == null) {
                    ToastUtil.showToastLonger("重复密码不能为空！");
                    return;
                }
                String content = mnemonic.getText().toString().trim();
                String resetpass = pwdEt.getText().toString().trim();
                String pass = repwdEt.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    ToastUtil.showToastLonger("请输入正确的助记词！");
                    return;
                }
                if (!check(resetpass) || !check(pass)) {
                    ToastUtil.showToastLonger("密码不符合要求！");
                    return;
                }
                if (!resetpass.equals(pass)) {
                    Toast.makeText(getApplicationContext(), "密码和重复密码必须相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                newPassword = pass;
                showProgress("正在导入钱包请稍候");
                IONCWalletSDK.getInstance()
                        .importWalletByMnemonicCode("", Arrays.asList(content.split(" ")), pass, AbsByMnemonicActivity.this);
            }
        });
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        findViews();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mnemonic.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
            String content = mnemonic.getText().toString().trim();
            String pwdstr = pwdEt.getText().toString().trim();
            String resetpwdstr = repwdEt.getText().toString().trim();

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)) {
                importBtn.setEnabled(true);
                importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
            } else {
                importBtn.setEnabled(false);
                importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        }

    }


    @Override
    public void onImportMnemonicSuccess(WalletBean walletBean) {
        final WalletBean wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());
        Logger.i("onCreateSuccess: " + walletBean.toString());
        hideProgress();
        if (null != wallet) {
            wallet.setPassword(walletBean.getPassword());
            wallet.setPrivateKey(walletBean.getPrivateKey());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("该钱包已存在")
                    .setMessage("是否继续导入？/n继续导入则会更新钱包密码!")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IONCWalletSDK.getInstance().updatePasswordAndKeyStore(wallet, newPassword, AbsByMnemonicActivity.this);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        } else {
            walletBean.setMIconIdex(getNum(7));
            ToastUtil.showToastLonger("导入成功啦!");
            IONCWalletSDK.getInstance().saveWallet(walletBean);
            onSDKCreateSuccess(walletBean);
        }
    }

    @Override
    public void onImportMnemonicFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger("导入成失败");
        Logger.i("onCreateFailure: " + error);
        onSDKCreateFailure(error);
    }

    @Override
    public void onUpdatePasswordSuccess(WalletBean wallet) {
        IONCWalletSDK.getInstance().removeWalletPrivateKey(wallet);
        ToastUtil.showToastLonger("更新成功啦!");
        onSDKCreateSuccess(wallet);

    }

    @Override
    public void onUpdatePasswordFailure(String error) {
        ToastUtil.showToastLonger(error);
        onSDKCreateFailure(error);

    }

}
