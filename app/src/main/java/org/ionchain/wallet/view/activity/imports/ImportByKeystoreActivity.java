package org.ionchain.wallet.view.activity.imports;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.ionchain.wallet.R;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;
import org.ionchain.wallet.view.activity.MainActivity;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleThree;

import java.util.List;

import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.ionchain.wallet.constant.ConstantParams.FROM_SCAN;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

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
                org.ionchain.wallet.utils.ToastUtil.showToastLonger(getAppString(R.string.key_store_must_not_empty));
                return;
            }
            //检查名字是否为空
            namestr = nameEt.getText().toString().trim();
            if (TextUtils.isEmpty(namestr)) {
                org.ionchain.wallet.utils.ToastUtil.showToastLonger(getResources().getString(R.string.illegal_name));
                return;
            }
            //检查密码是否为空
            String pass = pwdEt.getText().toString();

            if (TextUtils.isEmpty(pass)) {
                org.ionchain.wallet.utils.ToastUtil.showToastLonger(getResources().getString(R.string.illegal_password_null));
                return;
            }
            //检查是否选中协议
            if (!checkbox.isChecked()) {
                org.ionchain.wallet.utils.ToastUtil.showToastLonger(getResources().getString(R.string.please_aggreement));
                return;
            }
            setViewAlphaAnimation(importBtn);

            if (IONCWalletSDK.getInstance().getWalletByName(namestr)!=null) {
                Toast.makeText(mActivity.getApplicationContext(), getResources().getString(R.string.wallet_name_exists), Toast.LENGTH_SHORT).show();
                return;
            }

            //生成keystory文件
            showProgress(getString(R.string.importing_wallet));
            LoggerUtils.i("正在导入KS......");
            IONCWalletSDK.getInstance().importWalletByKeyStore(namestr, pass, mKeystoreStr, ImportByKeystoreActivity.this);
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
        WalletBeanNew wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());
        LoggerUtils.i("KS导入成功:" + walletBean.toString());
        if (null != wallet) {
            ToastUtil.showLong(getAppString(R.string.wallet_exists));
        } else {
            IONCWalletSDK.getInstance().changeMainWalletAndSave(walletBean);
            walletBean.setMIconIndex(getNum(7));
            ToastUtil.showToastLonger(getAppString(R.string.import_success));
            if (IONCWalletSDK.getInstance().getAllWalletNew().size() == 1) {
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
