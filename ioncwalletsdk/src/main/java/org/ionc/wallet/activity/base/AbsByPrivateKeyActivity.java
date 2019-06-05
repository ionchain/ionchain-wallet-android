package org.ionc.wallet.activity.base;

import android.app.AlertDialog;
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

import androidx.appcompat.widget.AppCompatEditText;

import com.ionc.wallet.sdk.R;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.RandomUntil;
import org.ionc.wallet.utils.ToastUtil;

import static org.ionc.wallet.utils.StringUtils.check;

/**
 * 之所以设计成抽象的，是因为有个回调要用户自己实现
 */
public abstract class AbsByPrivateKeyActivity extends BaseActivity implements TextWatcher, OnCreateWalletCallback, OnUpdateWalletCallback {

    private AppCompatEditText mPrivateKey;
    private AppCompatEditText pwdEt;
    private AppCompatEditText repwdEt;
    private Button importBtn;
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
        importBtn = (Button) findViewById(R.id.importBtn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass;//获取密码
                String pass2;

                if (mPrivateKey.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.private_must_not_empty));
                    return;
                }
                if (pwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.password_illegal_empty));
                    return;
                }
                if (repwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.password_again_illegal_empty));
                    return;
                }
                private_key = mPrivateKey.getText().toString().trim();
                pass2 = repwdEt.getText().toString().trim();
                pass = pwdEt.getText().toString().trim();
                if (!check(pass2) || !check(pass)) {
                    ToastUtil.showToastLonger(getAppString(R.string.password_illegal));
                    return;
                }

                if (private_key.startsWith("0x")) {
                    private_key = private_key.substring(2);
                }
                if (private_key.length() != 64) {
                    ToastUtil.showToastLonger(getAppString(R.string.illegal_private_key));
                    return;
                }

                if (!pass2.equals(pass)) {
                    Toast.makeText(getApplicationContext(), getAppString(R.string.password_twice_must_equal), Toast.LENGTH_SHORT).show();
                    return;
                }
                newPassword = pass;
                showProgress(getAppString(R.string.importing_please_wait));
                IONCWalletSDK.getInstance()
                        .importPrivateKey("", private_key, pass, AbsByPrivateKeyActivity.this);
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
        return R.layout.activity_by_pri_key;
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
    public void onCreateSuccess(final WalletBeanNew walletBean) {
        LoggerUtils.i(walletBean.toString());
        hideProgress();
        final WalletBeanNew wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());
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
                            IONCWalletSDK.getInstance().updatePasswordAndKeyStore(wallet, newPassword, AbsByPrivateKeyActivity.this);
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
    public void onCreateFailure(String result) {
        hideProgress();
        ToastUtil.showToastLonger(getAppString(R.string.import_failure));
        LoggerUtils.e("onCreateFailure: " + result);
        onSDKCreateFailure(result);

    }

    @Override
    public void onUpdateWalletSuccess(WalletBeanNew wallet) {
        IONCWalletSDK.getInstance().updateWallet(wallet);
//        wallet.setPrivateKey("");//不保存私钥
        ToastUtil.showToastLonger(getAppString(R.string.update_success));
        onSDKCreateSuccess(wallet);
    }

    @Override
    public void onUpdateWalletFailure(String error) {
        ToastUtil.showToastLonger(error);
        LoggerUtils.e("导入失败 " + error);
        onSDKCreateFailure(error);
    }

}
