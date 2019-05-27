package org.ionchain.wallet.mvp.view.activity.imports;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.web3j.crypto.WalletUtils;

import java.util.List;

import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.ionc.wallet.utils.StringUtils.check;
import static org.ionchain.wallet.constant.ConstantParams.FROM_SCAN;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ImportByPriKeyActivity extends AbsBaseActivity implements TextWatcher, OnCreateWalletCallback, OnUpdateWalletCallback {

    private AppCompatEditText mPrivateKey;
    private AppCompatEditText pwdEt;
    private AppCompatEditText nameEt;
    private AppCompatEditText repwdEt;
    private Button importBtn;
    private CheckBox checkbox;
    private String private_key;
    private String newPassword;
    private String nameStr;
    private ImageView back;
    private TextView linkUrlTv;
    private ImageView scan;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-16 23:25:34 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    private void findViews() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        back = (ImageView) findViewById(R.id.back);
        scan = (ImageView) findViewById(R.id.scan);
        mPrivateKey = (AppCompatEditText) findViewById(R.id.contentEt);
        pwdEt = (AppCompatEditText) findViewById(R.id.pwdEt);
        nameEt = (AppCompatEditText) findViewById(R.id.nameEt);
        repwdEt = (AppCompatEditText) findViewById(R.id.repwdEt);
        linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
        importBtn = (Button) findViewById(R.id.importBtn);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        super.onPermissionsGranted(requestCode, list);
        skip(CaptureActivity.class, FROM_SCAN);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        super.onPermissionsDenied(requestCode, list);
        LoggerUtils.e("permission", "拒绝");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FROM_SCAN) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    mPrivateKey.setText(result);
                    private_key = result;
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showLong(getString(R.string.error_parase_toast_qr_code));
                }
            }
        }
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
        mPrivateKey.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        repwdEt.addTextChangedListener(this);
    }

    @Override
    protected void setListener() {
        super.setListener();
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPrivateKey.getText() != null && nameEt.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
                    String content = mPrivateKey.getText().toString().trim();
                    String pwdstr = pwdEt.getText().toString().trim();
                    nameStr = nameEt.getText().toString().trim();
                    String resetpwdstr = repwdEt.getText().toString().trim();

                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(nameStr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr) && checkbox.isChecked()) {
                        importBtn.setEnabled(true);
                        importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
                    } else {
                        importBtn.setEnabled(false);
                        importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                    }
                }
            }
        });
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipWebProtocol();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestCameraPermissions()) {
                    skip(CaptureActivity.class, FROM_SCAN);
                }
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass;//获取密码
                String pass2;
                setViewAlphaAnimation(importBtn);
                if (mPrivateKey.getText() == null) {
                    ToastUtil.showToastLonger(getResources().getString(R.string.illegal_private_key_null));
                    return;
                }
                if (!WalletUtils.isValidPrivateKey(mPrivateKey.getText().toString())) {
                    ToastUtil.showToastLonger(getResources().getString(com.ionc.wallet.sdk.R.string.error_private_key));
                    return;
                }
                if (nameEt.getText() == null) {
                    ToastUtil.showToastLonger(getResources().getString(R.string.illegal_name));
                    return;
                }
                if (pwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getResources().getString(R.string.illegal_password_null));
                    return;
                }
                if (repwdEt.getText() == null) {
                    ToastUtil.showToastLonger(getResources().getString(R.string.illegal_re_password_null));
                    return;
                }
                private_key = mPrivateKey.getText().toString().trim();
                pass2 = repwdEt.getText().toString().trim();
                pass = pwdEt.getText().toString().trim();
                if (!check(pass2) || !check(pass)) {
                    ToastUtil.showToastLonger(getResources().getString(R.string.illegal_password));
                    return;
                }

                if (private_key.startsWith("0x")) {
                    private_key = private_key.substring(2);
                }
                if (private_key.length() != 64) {
                    ToastUtil.showToastLonger(getResources().getString(R.string.illegal_private_key));
                    return;
                }

                if (!pass2.equals(pass)) {
                    Toast.makeText(mActivity.getApplicationContext(), getResources().getString(R.string.illegal_password_must_equal), Toast.LENGTH_SHORT).show();
                    return;
                }
                newPassword = pass;
                showProgress(getString(R.string.importing_wallet));
                IONCWalletSDK.getInstance()
                        .importPrivateKey(nameStr, private_key, pass, ImportByPriKeyActivity.this);
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
    protected int getLayoutId() {
        return R.layout.activity_import_by_pri_key;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mPrivateKey.getText() != null && nameEt.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
            String contentstr = mPrivateKey.getText().toString().trim();
            String pwdstr = pwdEt.getText().toString().trim();
            nameStr = pwdEt.getText().toString().trim();
            String resetpwdstr = repwdEt.getText().toString().trim();

            if (!TextUtils.isEmpty(contentstr) && !TextUtils.isEmpty(nameStr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr) && checkbox.isChecked()) {
                importBtn.setEnabled(true);
                importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
            } else {
                importBtn.setEnabled(false);
                importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        }

    }

    /**
     * @param walletBean 钱包
     */
    @Override
    public void onCreateSuccess(final WalletBeanNew walletBean) {
        LoggerUtils.i(walletBean.toString());
        hideProgress();
        final WalletBeanNew wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());
        if (null != wallet) {
            LoggerUtils.i("导入私钥--钱包存在,是否更新?" + wallet.toString());
            wallet.setPassword(walletBean.getPassword());
            wallet.setPrivateKey(walletBean.getPrivateKey());
            wallet.setName(nameStr);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.wallet_name_exists))
                    .setMessage(getString(R.string.import_and_update_password))
                    .setPositiveButton(R.string.continues, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            LoggerUtils.i("钱包已存在,执行更新");
                            IONCWalletSDK.getInstance().updatePasswordAndKeyStore(wallet, newPassword, ImportByPriKeyActivity.this);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoggerUtils.i("钱包已存在,取消更新");
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            walletBean.setMIconIndex(getNum(7));
            LoggerUtils.i("导入私钥--钱包不存在---执行导入---导入私钥成功");
            ToastUtil.showToastLonger(getResources().getString(R.string.import_success));
            IONCWalletSDK.getInstance().changeMainWalletAndSave(walletBean);
            if (IONCWalletSDK.getInstance().getAllWalletNew().size() == 1) {
                LoggerUtils.i("导入私钥--钱包不存在---执行导入---导入私钥成功--只有一个钱包");
                skip(MainActivity.class);
            } else {
                skipToBack();
            }
        }
    }

    @Override
    public void onCreateFailure(String result) {
        hideProgress();
        ToastUtil.showToastLonger(getResources().getString(R.string.import_error));
        LoggerUtils.e(TAG, "onCreateFailure: " + result);
    }

    @Override
    public void onUpdateWalletSuccess(WalletBeanNew wallet) {
        IONCWalletSDK.getInstance().updateWallet(wallet);
        ToastUtil.showToastLonger(getResources().getString(R.string.update_success));
        skipToBack();
    }


    @Override
    public void onUpdateWalletFailure(String error) {
        ToastUtil.showToastLonger(error);
        LoggerUtils.e("导入失败 " + error);
    }

}
