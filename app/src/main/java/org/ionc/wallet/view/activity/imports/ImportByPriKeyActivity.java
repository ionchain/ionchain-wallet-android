package org.ionc.wallet.view.activity.imports;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.qrcode.activity.CaptureActivity;
import org.ionc.wallet.qrcode.activity.CodeUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.ionc.wallet.view.activity.MainActivity;
import org.ionc.wallet.view.base.AbsBaseActivityTitleThree;
import org.ionc.wallet.web3j.IONCWallet;
import org.ionchain.wallet.R;
import org.web3j.crypto.WalletUtils;

import java.util.List;

import static org.ionc.wallet.constant.ConstantParams.FROM_SCAN;
import static org.ionc.wallet.utils.AnimationUtils.setViewAlphaAnimation;
import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.ionc.wallet.utils.StringUtils.check;

public class ImportByPriKeyActivity extends AbsBaseActivityTitleThree implements OnCreateWalletCallback, OnUpdateWalletCallback {

    private AppCompatEditText mPrivateKey;
    private AppCompatEditText pwdEt;
    private AppCompatEditText nameEt;
    private AppCompatEditText repwdEt;
    private Button importBtn;
    private CheckBox checkbox;
    private String private_key;
    private String newPasswordTemp;
    private String namestr;
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

    }

    @Override
    protected void initView() {
        findViews();
    }

    @Override
    protected String getTitleName() {
        return getAppString(R.string.import_private_key);
    }

    @Override
    protected void setListener() {
        super.setListener();

        linkUrlTv.setOnClickListener(v -> skipWebProtocol());
        mTitleRightImage.setOnClickListener(v -> {
            if (requestCameraPermissions()) {
                skip(CaptureActivity.class, FROM_SCAN);
            }
        });
        importBtn.setOnClickListener(v -> {
            //检查私钥是否为空
            String private_key = mPrivateKey.getText().toString().trim();
            if (TextUtils.isEmpty(private_key)) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_private_key_null));
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

            if (IONCWallet.getWalletByName(namestr) != null) {
                Toast.makeText(mActivity.getApplicationContext(), getResources().getString(R.string.wallet_name_exists), Toast.LENGTH_SHORT).show();
                return;
            }

            setViewAlphaAnimation(importBtn);

            if (!WalletUtils.isValidPrivateKey(mPrivateKey.getText().toString())) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_private_key));
                return;
            }

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
            newPasswordTemp = pass;
            showProgress(getString(R.string.importing_wallet));
            IONCWallet.importPrivateKey(namestr, private_key, pass, ImportByPriKeyActivity.this);
        });
        mTitleLeftImage.setOnClickListener(v -> finish());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_by_pri_key;
    }



    /**
     * @param walletBean 钱包
     */
    @Override
    public void onCreateSuccess(final WalletBeanNew walletBean) {
        LoggerUtils.i(walletBean.toString());
        hideProgress();
        final WalletBeanNew wallet = IONCWallet.getWalletByAddress(walletBean.getAddress().toLowerCase());
        if (null != wallet) {
            LoggerUtils.i("导入私钥--钱包存在,是否更新?" + wallet.toString());
            wallet.setPassword(walletBean.getPassword());
            wallet.setPrivateKey(walletBean.getPrivateKey());
            wallet.setName(namestr);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.wallet_name_exists))
                    .setMessage(getString(R.string.import_and_update_password))
                    .setPositiveButton(R.string.continues, (dialog, which) -> {
                        dialog.dismiss();
                        LoggerUtils.i("钱包已存在,执行更新");
                        IONCWallet.updatePasswordAndKeyStore(wallet, newPasswordTemp, ImportByPriKeyActivity.this);
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                        LoggerUtils.i("钱包已存在,取消更新");
                        dialog.dismiss();
                    })
                    .show();
        } else {
            walletBean.setMIconIndex(getNum(7));
            LoggerUtils.i("导入私钥--钱包不存在---执行导入---导入私钥成功");
            ToastUtil.showToastLonger(getResources().getString(R.string.import_success));
            IONCWallet.changeMainWalletAndSave(walletBean);
            if (IONCWallet.getAllWalletNew().size() == 1) {
                LoggerUtils.i("导入私钥--钱包不存在---执行导入---导入私钥成功--只有一个钱包");
                skip(MainActivity.class);
            } else {
                skipToBack(walletBean); //私钥导入钱包
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
        IONCWallet.updateWallet(wallet);
        ToastUtil.showToastLonger(getResources().getString(R.string.update_success));
        skipToBack(wallet); //私钥更新钱包
    }


    @Override
    public void onUpdateWalletFailure(String error) {
        ToastUtil.showToastLonger(error);
        LoggerUtils.e("导入失败 " + error);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
