package org.ionc.wallet.view.activity.imports;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.ionclib.bean.KeystoreBean;
import org.ionc.ionclib.bean.WalletBeanNew;
import org.ionc.ionclib.callback.OnCreateWalletCallback;
import org.ionc.ionclib.utils.ToastUtil;
import org.ionc.ionclib.web3j.IONCSDKWallet;
import org.ionc.wallet.qrcode.activity.CaptureActivity;
import org.ionc.wallet.qrcode.activity.CodeUtils;
import org.ionc.wallet.utils.GsonUtils;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ViewUtils;
import org.ionc.wallet.view.activity.MainActivity;
import org.ionc.wallet.view.base.AbsBaseActivityTitleThree;
import org.ionchain.wallet.R;

import java.util.List;

import static org.ionc.ionclib.utils.RandomUntil.getNum;
import static org.ionc.wallet.constant.ConstantParams.FROM_SCAN;

public class ImportByKeystoreActivity extends AbsBaseActivityTitleThree implements OnCreateWalletCallback {
    private AppCompatEditText mKeystore;
    private AppCompatEditText pwdEt;
    private AppCompatEditText nameEt;
    private Button importBtn;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private String mKeystoreStr;
    private String namestr;

    @Override
    protected String getTitleName() {
        return getAppString(R.string.import_ks);
    }

    @Override
    protected void setListener() {
        super.setListener();
        importBtn.setOnClickListener(v -> {
            //检查私钥是否为空
            mKeystoreStr = mKeystore.getText().toString();
            if (TextUtils.isEmpty(mKeystoreStr)) {
                ToastUtil.showToastLonger(getAppString(R.string.key_store_must_not_empty));
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

            if (TextUtils.isEmpty(pass)) {
                ToastUtil.showToastLonger(getResources().getString(R.string.illegal_password_null));
                return;
            }
            //检查是否选中协议
            if (!checkbox.isChecked()) {
                ToastUtil.showToastLonger(getResources().getString(R.string.please_aggreement));
                return;
            }
            ViewUtils.setViewAlphaAnimation(importBtn);

            if (IONCSDKWallet.getWalletByName(namestr)!=null) {
                Toast.makeText(mActivity.getApplicationContext(), getResources().getString(R.string.wallet_name_exists), Toast.LENGTH_SHORT).show();
                return;
            }

            //生成keystory文件
            showProgress(getString(R.string.importing_wallet));
            LoggerUtils.i("正在导入KS......");
            KeystoreBean keystoreBean = GsonUtils.gsonToBean(mKeystoreStr, KeystoreBean.class);
            IONCSDKWallet.importWalletByKeyStore(namestr, pass, keystoreBean, mKeystoreStr,ImportByKeystoreActivity.this);
        });
        linkUrlTv.setOnClickListener(v -> skipWebProtocol());
        mTitleRightImage.setOnClickListener(v -> {
            if (requestCameraPermissions()) {
                skip(CaptureActivity.class, FROM_SCAN);
            }
        });

    }


    @Override
    protected void initView() {

        mKeystore = findViewById(R.id.mnemonic);

        pwdEt = findViewById(R.id.pwdEt);
        nameEt = findViewById(R.id.nameEt);
        checkbox = findViewById(R.id.checkbox);



        linkUrlTv = findViewById(R.id.linkUrlTv);
        importBtn = findViewById(R.id.importBtn);

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_by_keystore;
    }


    @Override
    public void onCreateSuccess(WalletBeanNew walletBean) {
        hideProgress();
        WalletBeanNew wallet = IONCSDKWallet.getWalletByAddress(walletBean.getAddress().toLowerCase());
        LoggerUtils.i("KS导入成功:" + walletBean.toString());
        if (null != wallet) {
            ToastUtil.showLong(getAppString(R.string.wallet_exists));
        } else {
            IONCSDKWallet.changeMainWalletAndSave(walletBean);
            walletBean.setMIconIndex(getNum(7));
            ToastUtil.showToastLonger(getAppString(R.string.import_success));
            if (IONCSDKWallet.getAllWalletNew().size() == 1) {
                skip(MainActivity.class);
            } else {
                skipToBack(walletBean);//KS 导入钱包
            }
        }
    }

    @Override
    public void onCreateFailure(String error) {
        hideProgress();
        LoggerUtils.e("KS导入失败:" + error);
        if ("Invalid password provided".equals(error)) {
            ToastUtil.showToastLonger(getAppString(R.string.illegal_password));
        } else {
            ToastUtil.showToastLonger(getAppString(R.string.import_error));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * 处理二维码扫描结果
         */
        if (requestCode == FROM_SCAN) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    mKeystore.setText(result);
                    mKeystoreStr = result;
                    LoggerUtils.i(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showLong(getAppString(R.string.error_parase_toast_qr_code));
                }
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        super.onPermissionsGranted(requestCode, list);
        skip(CaptureActivity.class, FROM_SCAN);
    }

}
