package org.ionchain.wallet.mvp.view.activity.imports;

import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnImportMnemonicCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Arrays;

import static org.ionc.wallet.utils.StringUtils.check;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ImportByMnemonicActivity extends AbsBaseActivity implements TextWatcher, OnImportMnemonicCallback, OnUpdateWalletCallback {
    private RelativeLayout importHeader;
    private ImageView back;
    private AppCompatEditText mnemonic;
    private AppCompatEditText pwdEt;
    private AppCompatEditText nameEt;
    private AppCompatEditText repwdEt;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private Button importBtn;
    private String newPassword;
    private String namestr;

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
        nameEt = (AppCompatEditText) findViewById(R.id.nameEt);
        repwdEt = (AppCompatEditText) findViewById(R.id.repwdEt);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
        importBtn = (Button) findViewById(R.id.importBtn);
    }

    @Override
    protected void setListener() {
        super.setListener();
        back.setOnClickListener(v -> finish());

        mnemonic.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        nameEt.addTextChangedListener(this);
        repwdEt.addTextChangedListener(this);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mnemonic.getText() != null && nameEt.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
                    String content = mnemonic.getText().toString().trim();
                    namestr = nameEt.getText().toString().trim();
                    String pwdstr = pwdEt.getText().toString().trim();
                    String resetpwdstr = repwdEt.getText().toString().trim();

                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(namestr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr) && checkbox.isChecked()) {
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
                    ToastUtil.showToastLonger(getAppString(R.string.error_mnemonics));
                    return;
                }

                if (pwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.illegal_password_null));
                    return;
                }
                if (repwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.illegal_re_password_null));
                    return;
                }if (nameEt.getText() == null) {
                    ToastUtil.showToastLonger(getAppString(R.string.illegal_name));
                    return;
                }
                String content = mnemonic.getText().toString();


                String resetpass = pwdEt.getText().toString().trim();
                String pass = repwdEt.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    ToastUtil.showToastLonger(getAppString(R.string.error_mnemonics));
                    return;
                }
                String con_temp = content.replace(" ", "");
                String[] array_str = content.split(" ");
                if (!StringUtils.isEN(con_temp) || array_str.length < 12) {
                    ToastUtil.showLong(getAppString(R.string.error_mnemonics));
                    return;
                }
                if (!check(resetpass) || !check(pass)) {
                    ToastUtil.showToastLonger(getAppString(R.string.illegal_password));
                    return;
                }
                if (!resetpass.equals(pass)) {
                    Toast.makeText(mActivity.getApplicationContext(), getAppString(R.string.illegal_password_must_equal), Toast.LENGTH_SHORT).show();
                    return;
                }
                namestr = nameEt.getText().toString().trim();
                if (IONCWalletSDK.getInstance().getWalletByName(namestr)!=null) {
                    Toast.makeText(mActivity.getApplicationContext(), getResources().getString(R.string.wallet_name_exists), Toast.LENGTH_SHORT).show();
                    return;
                }
                newPassword = pass;
                showProgress(getAppString(R.string.importing_wallet));
                IONCWalletSDK.getInstance()
                        .importWalletByMnemonicCode(namestr, Arrays.asList(content.split(" ")), pass, ImportByMnemonicActivity.this);
            }
        });
        linkUrlTv.setOnClickListener(v -> skipWebProtocol());
    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    protected void initData() {
        mImmersionBar.titleView(R.id.import_header)
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
        if (mnemonic.getText() != null && nameEt.getText() != null&& pwdEt.getText() != null && repwdEt.getText() != null) {
            String content = mnemonic.getText().toString().trim();
            String pwdstr = pwdEt.getText().toString().trim();
            String resetpwdstr = repwdEt.getText().toString().trim();
            namestr = nameEt.getText().toString().trim();
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(namestr)&& !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr) && checkbox.isChecked()) {
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
        walletBean.setMnemonic("");
        final WalletBeanNew wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());

        if (null != wallet) {
            wallet.setPassword(walletBean.getPassword());
            wallet.setPrivateKey(walletBean.getPrivateKey());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getAppString(R.string.wallet_exists))
                    .setMessage(getAppString(R.string.import_and_update_password))
                    .setPositiveButton(getAppString(R.string.continues), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            IONCWalletSDK.getInstance().updatePasswordAndKeyStore(wallet, newPassword, ImportByMnemonicActivity.this);

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
            IONCWalletSDK.getInstance().changeMainWalletAndSave(walletBean);
            ToastUtil.showToastLonger(getAppString(R.string.import_success));
            if (IONCWalletSDK.getInstance().getAllWalletNew().size()==1) {
                skip(MainActivity.class);
                finish();
            }else {
                skipToBack(walletBean);//助记词导入钱包
            }
        }
        hideProgress();
    }

    @Override
    public void onImportMnemonicFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger(getAppString(R.string.import_error));
    }

    @Override
    public void onUpdateWalletSuccess(WalletBeanNew wallet) {
        IONCWalletSDK.getInstance().updateWallet(wallet);
        ToastUtil.showToastLonger(getAppString(R.string.update_success));
        skipToBack(wallet);//z助记词更新钱包
    }

    @Override
    public void onUpdateWalletFailure(String error) {
        ToastUtil.showToastLonger(error);
    }
}
