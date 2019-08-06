package org.ionchain.wallet.view.activity.imports;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnImportMnemonicCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.sdk.IONCWallet;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.activity.MainActivity;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleTwo;

import java.util.Arrays;

import static org.ionc.wallet.utils.StringUtils.check;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ImportByMnemonicActivity extends AbsBaseActivityTitleTwo implements  OnImportMnemonicCallback, OnUpdateWalletCallback {
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
        importBtn.setOnClickListener(v -> {
            //检查私钥是否为空
            String content = mnemonic.getText().toString();
            if (TextUtils.isEmpty(content)) {
                ToastUtil.showToastLonger(getAppString(R.string.error_mnemonics));
                return;
            }
            //检查名字是否为空
            namestr = nameEt.getText().toString().trim();
            if (TextUtils.isEmpty(namestr)) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_name));
                return;
            }
            //检查密码是否为空
            String pass = pwdEt.getText().toString();
            String pass2 = repwdEt.getText().toString();

            if (TextUtils.isEmpty(pass)) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_password_null));
                return;
            }
            if (TextUtils.isEmpty(pass2)) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_re_password_null));
                return;
            }

            //检查是否选中协议
            if (!checkbox.isChecked()) {
                ToastUtil.showToastLonger(getResources().getString(R.string.please_aggreement));
                return;
            }
            setViewAlphaAnimation(importBtn);

            String con_temp = content.replace(" ", "");
            String[] array_str = content.split(" ");
            if (!StringUtils.isEN(con_temp) || array_str.length < 12) {
                ToastUtil.showLong(getAppString(R.string.error_mnemonics));
                return;
            }
            if (!check(pass2) || !check(pass)) {
                ToastUtil.showToastLonger(getAppString(R.string.illegal_password));
                return;
            }
            if (!pass2.equals(pass)) {
                Toast.makeText(mActivity.getApplicationContext(), getAppString(R.string.illegal_password_must_equal), Toast.LENGTH_SHORT).show();
                return;
            }
            if (IONCWallet.getWalletByName(namestr)!=null) {
                Toast.makeText(mActivity.getApplicationContext(), getResources().getString(R.string.wallet_name_exists), Toast.LENGTH_SHORT).show();
                return;
            }
            newPassword = pass;
            showProgress(getAppString(R.string.importing_wallet));
            IONCWallet
                    .importWalletByMnemonicCode(namestr, Arrays.asList(content.split(" ")), pass, ImportByMnemonicActivity.this);
        });
        linkUrlTv.setOnClickListener(v -> skipWebProtocol());
    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    protected void initData() {
       
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.import_by_mnemonics);
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
    public void onImportMnemonicSuccess(WalletBeanNew walletBean) {
        walletBean.setMnemonic("");
        final WalletBeanNew wallet = IONCWallet.getWalletByAddress(walletBean.getAddress().toLowerCase());
        if (null != wallet) {
            wallet.setPassword(walletBean.getPassword());
            wallet.setPrivateKey(walletBean.getPrivateKey());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getAppString(R.string.wallet_exists))
                    .setMessage(getAppString(R.string.import_and_update_password))
                    .setPositiveButton(getAppString(R.string.continues), (dialog, which) -> {

                        dialog.dismiss();
                        IONCWallet.updatePasswordAndKeyStore(wallet, newPassword, ImportByMnemonicActivity.this);

                    })
                    .setNegativeButton(getAppString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            IONCWallet.changeMainWalletAndSave(walletBean);
            ToastUtil.showToastLonger(getAppString(R.string.import_success));
            if (IONCWallet.getAllWalletNew().size()==1) {
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
        IONCWallet.updateWallet(wallet);
        ToastUtil.showToastLonger(getAppString(R.string.update_success));
        skipToBack(wallet);//z助记词更新钱包
    }

    @Override
    public void onUpdateWalletFailure(String error) {
        ToastUtil.showToastLonger(error);
    }
}
