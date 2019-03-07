package org.ionchain.wallet.mvp.view.activity.importmode;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.callback.OnImportMnemonicCallback;
import com.ionc.wallet.sdk.callback.OnUpdatePasswordCallback;
import com.ionc.wallet.sdk.utils.StringUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Arrays;

import static com.ionc.wallet.sdk.utils.RandomUntil.getNum;
import static com.ionc.wallet.sdk.utils.StringUtils.check;
import static org.ionchain.wallet.constant.ConstantParams.SERVER_PROTOCOL_VALUE;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ImportByMnemonicActivity extends AbsBaseActivity implements TextWatcher, OnImportMnemonicCallback, OnUpdatePasswordCallback {
    private RelativeLayout importHeader;
    private ImageView back;
    private AppCompatEditText mnemonic;
    private AppCompatEditText pwdEt;
    private AppCompatEditText repwdEt;
    private CheckBox checkbox;
    private TextView linkUrlTv;
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
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
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
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mnemonic.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
                    String content = mnemonic.getText().toString().trim();
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
                setViewAlphaAnimation(importBtn);
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
                String content = mnemonic.getText().toString();


                String resetpass = pwdEt.getText().toString().trim();
                String pass = repwdEt.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    ToastUtil.showToastLonger("助记词不能为空！");
                    return;
                }
                String con_temp = content.replace(" ","");
                String [] array_str =content.split(" ");
                if (!StringUtils.isEN(con_temp) || array_str.length < 12) {
                    ToastUtil.showLong("助记词不正确");
                    return;
                }
                if (!check(resetpass) || !check(pass)) {
                    ToastUtil.showToastLonger("密码不符合要求！");
                    return;
                }
                if (!resetpass.equals(pass)) {
                    Toast.makeText(getMActivity().getApplicationContext(), "密码和重复密码必须相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                newPassword = pass;
                showProgress("正在导入钱包请稍候");
                IONCWalletSDK.getInstance()
                        .importWalletByMnemonicCode("", Arrays.asList(content.split(" ")), pass, ImportByMnemonicActivity.this);
            }
        });
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipWeb(SERVER_PROTOCOL_VALUE);
            }
        });
    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    protected void initData() {
        getMImmersionBar().titleView(R.id.import_header)
                .statusBarDarkFont(true)
                .execute();
    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_by_mnemonic;
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

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr) && checkbox.isChecked()) {
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
        Log.i(getTAG(), "onCreateSuccess: " + walletBean.toString());

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
                            IONCWalletSDK.getInstance().updatePasswordAndKeyStore(wallet, newPassword, ImportByMnemonicActivity.this);

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

            IONCWalletSDK.getInstance().saveWallet(walletBean);
            ToastUtil.showToastLonger("导入成功啦!");
            skip(MainActivity.class);
        }
        hideProgress();
    }

    @Override
    public void onImportMnemonicFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger("导入成失败");
        Log.i(getTAG(), "onCreateFailure: " + error);
    }

    @Override
    public void onUpdatePasswordSuccess(WalletBean wallet) {
        IONCWalletSDK.getInstance().removeWalletPrivateKey(wallet);
        ToastUtil.showToastLonger("更新成功啦!");
        skip(MainActivity.class);
    }

    @Override
    public void onUpdatePasswordFailure(String error) {
        ToastUtil.showToastLonger(error);
    }
}
