package org.ionc.wallet.activity.base;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatEditText;

import com.ionc.wallet.sdk.R;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.RandomUntil;
import org.ionc.wallet.utils.ToastUtil;

public abstract class AbsByKeystoreActivity extends BaseActivity implements OnCreateWalletCallback, TextWatcher {
    private AppCompatEditText mKeystore;
    private AppCompatEditText pwdEt;
    private Button importBtn;
    private String keystoreStr;
    private String newPassword;

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
        importBtn = findViewById(R.id.importBtn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKeystore.getText() != null && pwdEt.getText() != null) {
                    keystoreStr = mKeystore.getText().toString();
                    //读取keystore密码
                    String pass = pwdEt.getText().toString();
                    //生成keystory文件
                    newPassword = pass;
                    showProgress(getAppString(R.string.importing_please_wait));
                    IONCWalletSDK.getInstance().importWalletByKeyStore("", pass, keystoreStr, AbsByKeystoreActivity.this);
                } else {
                    ToastUtil.showLong(getAppString(R.string.please_check_input));

                }
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_by_keystore;
    }


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

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr)) {
                importBtn.setEnabled(true);
                importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
            } else {
                importBtn.setEnabled(false);
                importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        }
    }

    @Override
    public void onCreateSuccess(WalletBeanNew walletBean) {
//        LoggerUtils.i(walletBean.toString());
//        hideProgress();
//        walletBean.setMIconIdex(getNum(7));
//        saveWallet(walletBean);
//        ToastUtil.showToastLonger("导入成功啦!");
//        onSDKCreateSuccess();
        final WalletBeanNew wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());
        LoggerUtils.i("onCreateSuccess: " + walletBean.toString());
        hideProgress();

        if (null != wallet) {
            ToastUtil.showLong(getAppString(R.string.wallet_exist_name_is) + wallet.getName());
        } else {
            walletBean.setMIconIndex(RandomUntil.getNum(7));
            IONCWalletSDK.getInstance().saveWallet(walletBean);
            ToastUtil.showToastLonger(getAppString(R.string.import_success));
            onSDKCreateSuccess(walletBean);
        }
    }

    @Override
    public void onCreateFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger(getAppString(R.string.import_failure));
        ToastUtil.showLong("onCreateFailure: $result");
        onSDKCreateFailure(error);

    }

}
