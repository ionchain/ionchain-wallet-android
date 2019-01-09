package com.ionc.wallet.sdk.activity.newwallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.R;
import com.ionc.wallet.sdk.activity.BaseActivity;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.callback.OnCreateWalletCallback;
import com.ionc.wallet.sdk.callback.OnUpdatePasswordCallback;
import com.ionc.wallet.sdk.utils.Logger;
import com.ionc.wallet.sdk.utils.ToastUtil;

import static com.ionc.wallet.sdk.IONCWalletSDK.getWalletByAddress;
import static com.ionc.wallet.sdk.IONCWalletSDK.saveWallet;
import static com.ionc.wallet.sdk.IONCWalletSDK.updateWallet;
import static com.ionc.wallet.sdk.utils.RandomUntil.getNum;
import static com.ionc.wallet.sdk.utils.StringUtils.check;

public class NewWalletByPriKeyActivity extends BaseActivity implements TextWatcher, OnCreateWalletCallback, OnUpdatePasswordCallback {

    private AppCompatEditText mPrivateKey;
    private AppCompatEditText pwdEt;
    private AppCompatEditText repwdEt;
    private Button importBtn;
    private boolean isWelcome;
    private CheckBox checkbox;
    private String private_key;
    private String newPassword;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-16 23:25:34 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    private void findViews() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        ImageView back = (ImageView) findViewById(R.id.back);
        mPrivateKey = (AppCompatEditText) findViewById(R.id.contentEt);
        pwdEt = (AppCompatEditText) findViewById(R.id.pwdEt);
        repwdEt = (AppCompatEditText) findViewById(R.id.repwdEt);
        TextView linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
        importBtn = (Button) findViewById(R.id.importBtn);
        checkbox = (CheckBox) findViewById(R.id.checkbox);

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrivateKey.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
                    String content = mPrivateKey.getText().toString().trim();
                    String pwdstr = pwdEt.getText().toString().trim();
                    String resetpwdstr = repwdEt.getText().toString().trim();

                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr) && checkbox.isChecked()) {
                        importBtn.setEnabled(true);
                        importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
                    } else {
                        importBtn.setEnabled(false);
                        importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                    }
                }
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass;//获取密码
                String pass2;

                if (mPrivateKey.getText() == null) {
                    ToastUtil.showToastLonger("私钥不能为空！");
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
                private_key = mPrivateKey.getText().toString().trim();
                pass2 = repwdEt.getText().toString().trim();
                pass = pwdEt.getText().toString().trim();
                if (!check(pass2) || !check(pass)) {
                    ToastUtil.showToastLonger("密码不符合要求！");
                    return;
                }

                if (private_key.startsWith("0x")) {
                    private_key = private_key.substring(2);
                }
                if (private_key.length() != 64) {
                    ToastUtil.showToastLonger("无效私钥！");
                    return;
                }

                if (!pass2.equals(pass)) {
                    Toast.makeText(getApplicationContext(), "密码和重复密码必须相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                newPassword = pass;
                showProgress("正在导入钱包请稍候");
                IONCWalletSDK.getInstance()
                        .importPrivateKey(private_key, pass, NewWalletByPriKeyActivity.this);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    protected void initData() {

    }

    @Override
    protected void handleIntent(Intent intent) {
    }

    @Override
    protected void initView() {
        findViews();
        mPrivateKey.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        repwdEt.addTextChangedListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_wallet_by_pri_key;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mPrivateKey.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
            String contentstr = mPrivateKey.getText().toString().trim();
            String pwdstr = pwdEt.getText().toString().trim();
            String resetpwdstr = repwdEt.getText().toString().trim();

            if (!TextUtils.isEmpty(contentstr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)) {
                importBtn.setEnabled(true);
                importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
            } else {
                importBtn.setEnabled(false);
                importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        }

    }

    @Override
    public void onCreateSuccess(final WalletBean walletBean) {
        Logger.i(walletBean.toString());
        hideProgress();
        final WalletBean wallet = getWalletByAddress(walletBean.getAddress());
        if (null != wallet) {
            wallet.setPassword(walletBean.getPassword());
            wallet.setPrivateKey(walletBean.getPrivateKey());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("该钱包已存在")
                    .setMessage("是否继续导入？\n继续导入则会更新钱包密码!")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IONCWalletSDK.getInstance().updatePasswordAndKeyStore(wallet, newPassword, NewWalletByPriKeyActivity.this);
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
            saveWallet(walletBean);
            ToastUtil.showToastLonger("导入成功啦!");
            if (isWelcome) {
                walletBean.setIsShowWallet(true);
            } else {
                walletBean.setIsShowWallet(false);
            }
            saveWallet(walletBean);
//            skip(MainActivity.class);
        }

    }

    @Override
    public void onCreateFailure(String result) {
        hideProgress();
        ToastUtil.showToastLonger("导入成失败");
        Logger.e( "onCreateFailure: " + result);
    }

    @Override
    public void onUpdatePasswordSuccess(WalletBean wallet) {
        updateWallet(wallet);
//        wallet.setPrivateKey("");//不保存私钥
        ToastUtil.showToastLonger("更新成功啦!");
//        skip(MainActivity.class);
    }

    @Override
    public void onUpdatePasswordFailure(String error) {
        ToastUtil.showToastLonger(error);
        Logger.e("导入失败 " + error);
    }

    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
