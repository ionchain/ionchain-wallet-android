package org.ionc.wallet.activity.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import com.ionc.wallet.sdk.R;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnImportMnemonicCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.RandomUntil;
import org.ionc.wallet.utils.StringUtils;
import org.ionc.wallet.utils.ToastUtil;

import java.util.Arrays;

import static org.ionc.wallet.utils.StringUtils.check;

public abstract class AbsByMnemonicActivity extends BaseActivity implements TextWatcher, OnImportMnemonicCallback, OnUpdateWalletCallback {


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
                    ToastUtil.showToastLonger(getAppString(R.string.mnemonics_can_not_empty));
                    return;
                }
                if (pwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.password_can_not_empty));
                    return;
                }
                if (repwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.passwords_again_can_not_empty));
                    return;
                }
                String content = mnemonic.getText().toString().trim();
                String resetpass = pwdEt.getText().toString().trim();
                String pass = repwdEt.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    ToastUtil.showToastLonger(getAppString(R.string.please_input_correct_mnemonics));
                    return;
                }
                if (!check(resetpass) || !check(pass)) {
                    ToastUtil.showToastLonger(getAppString(R.string.password_illegal_empty));
                    return;
                }
                if (!resetpass.equals(pass)) {
                    Toast.makeText(getApplicationContext(), getAppString(R.string.password_twice_must_equal), Toast.LENGTH_SHORT).show();
                    return;
                }
                newPassword = pass;
                showProgress(getAppString(R.string.importing_wallet));
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
    public void onImportMnemonicSuccess(WalletBeanNew walletBean) {
        final WalletBeanNew wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());
        LoggerUtils.i("onCreateSuccess: " + walletBean.toString());
        hideProgress();
        if (null != wallet) {
            wallet.setPassword(walletBean.getPassword());
            wallet.setPrivateKey(walletBean.getPrivateKey());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getAppString(R.string.wallet_exist))
                    .setMessage(getAppString(R.string.import_and_update_password))
                    .setPositiveButton(getAppString(R.string.continues), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IONCWalletSDK.getInstance().updatePasswordAndKeyStore(wallet, newPassword, AbsByMnemonicActivity.this);
                        }
                    })
                    .setNegativeButton(getAppString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        } else {
            walletBean.setMIconIndex(RandomUntil.getNum(7));
            ToastUtil.showToastLonger(getAppString(R.string.import_success));
            IONCWalletSDK.getInstance().saveWallet(walletBean);
            onSDKCreateSuccess(walletBean);
        }
    }

    @Override
    public void onImportMnemonicFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger(getAppString(R.string.import_failure));
        LoggerUtils.i("onCreateFailure: " + error);
        onSDKCreateFailure(error);
    }

    @Override
    public void onUpdateWalletSuccess(WalletBeanNew wallet) {
        IONCWalletSDK.getInstance().updateWallet(wallet);
        ToastUtil.showToastLonger(getAppString(R.string.update_success));
        onSDKCreateSuccess(wallet);

    }

    @Override
    public void onUpdateWalletFailure(String error) {
        ToastUtil.showToastLonger(error);
        onSDKCreateFailure(error);

    }

}
