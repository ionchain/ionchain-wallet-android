package com.ionc.wallet.sdk.activity.newwallet;

import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.R;
import com.ionc.wallet.sdk.activity.BaseActivity;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.callback.OnCreateWalletCallback;
import com.ionc.wallet.sdk.utils.Logger;
import com.ionc.wallet.sdk.utils.StringUtils;
import com.ionc.wallet.sdk.utils.ToastUtil;

import static com.ionc.wallet.sdk.utils.RandomUntil.getNum;

public class ByKeystoreActivity extends BaseActivity implements OnCreateWalletCallback, TextWatcher {

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mKeystore = findViewById(R.id.mnemonic);
        pwdEt = findViewById(R.id.pwdEt);
        mKeystore.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKeystore.getText() != null && StringUtils.check(mKeystore.getText().toString()) && pwdEt.getText() != null && StringUtils.check(pwdEt.getText().toString()) && checkbox.isChecked()) {
                    importBtn.setEnabled(true);
                    importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
                } else {
                    importBtn.setEnabled(false);
                    importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });
        importBtn = findViewById(R.id.importBtn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKeystore.getText() != null && pwdEt.getText() != null) {
                    keystoreStr = mKeystore.getText().toString();
                    //读取keystore密码
                    String pass = pwdEt.getText().toString();
                    //生成keystory文件
                    showProgress("正在导入钱包请稍候");
                    IONCWalletSDK.getInstance().importWalletByKeyStore(pass, keystoreStr, ByKeystoreActivity.this);
                }else {
                    ToastUtil.showLong("请检查输入是否正确！");

                }
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_by_keystore;
    }

    private AppCompatEditText mKeystore;
    private AppCompatEditText pwdEt;
    private Button importBtn;
    private Boolean isWelcome;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private String keystoreStr;




    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mKeystore.getText() != null && pwdEt.getText() != null && pwdEt.getText() != null) {
            String content = mKeystore.getText().toString();
            String pwdstr = pwdEt.getText().toString();

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && checkbox.isChecked()) {
                importBtn.setEnabled(true);
                importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
            } else {
                importBtn.setEnabled(false);
                importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        }
    }

    @Override
    public void onCreateSuccess(WalletBean walletBean) {
        Logger.i(walletBean.toString());
        hideProgress();
        walletBean.setMIconIdex(getNum(7));
        ToastUtil.showToastLonger("导入成功啦!");
//        skip(MainActivity:: class.java)
    }

    @Override
    public void onCreateFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger("导入成失败");
        ToastUtil.showLong( "onCreateFailure: $result");
    }
}
