package org.ionchain.wallet.mvp.view.activity.imports;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.ToastUtil;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.qrcode.activity.CaptureActivity;
import org.ionchain.wallet.qrcode.activity.CodeUtils;

import java.util.List;

import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.ionchain.wallet.constant.ConstantParams.FROM_SCAN;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ImportByKeystoreActivity extends AbsBaseActivity implements OnCreateWalletCallback, TextWatcher {
    private AppCompatEditText mKeystore;
    private AppCompatEditText pwdEt;
    private AppCompatEditText nameEt;
    private Button importBtn;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private String keystoreStr;
    private String namestr;

    @Override
    protected void setListener() {
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewAlphaAnimation(importBtn);
                if (nameEt.getText() == null) {
                    org.ionchain.wallet.utils.ToastUtil.showToastLonger(getAppString(R.string.illegal_name));
                    return;
                }
                if (pwdEt.getText() == null) {
                    org.ionchain.wallet.utils.ToastUtil.showToastLonger(getAppString(R.string.illegal_password_null));
                    return;
                }
                if (mKeystore.getText() == null) {
                    org.ionchain.wallet.utils.ToastUtil.showToastLonger(getAppString(R.string.key_store_must_not_empty));
                    return;
                }
                keystoreStr = mKeystore.getText().toString();
                //读取keystore密码
                String pass = pwdEt.getText().toString();
                //生成keystory文件
                showProgress(getString(R.string.importing_wallet));
                LoggerUtils.i("正在导入KS......");
                IONCWalletSDK.getInstance().importWalletByKeyStore(namestr, pass, keystoreStr, ImportByKeystoreActivity.this);
            }
        });
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipWebProtocol();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mImmersionBar.titleView(R.id.import_header)
                .statusBarDarkFont(true)
                .execute();
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestCameraPermissions()) {
                    skip(CaptureActivity.class, FROM_SCAN);
                }
            }
        });
        mKeystore = findViewById(R.id.mnemonic);

        pwdEt = findViewById(R.id.pwdEt);
        nameEt = findViewById(R.id.nameEt);
        mKeystore.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        nameEt.addTextChangedListener(this);
        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKeystore.getText() != null && pwdEt.getText() != null && nameEt.getText() != null && checkbox.isChecked()) {
                    importBtn.setEnabled(true);
                    importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
                } else {
                    importBtn.setEnabled(false);
                    importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });

        linkUrlTv = findViewById(R.id.linkUrlTv);
        importBtn = findViewById(R.id.importBtn);

    }

    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_by_keystore;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mKeystore.getText() != null && pwdEt.getText() != null && nameEt.getText() != null) {
            String content = mKeystore.getText().toString().trim();
            String pwdstr = pwdEt.getText().toString().trim();
            namestr = nameEt.getText().toString().trim();

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(namestr) && checkbox.isChecked()) {
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
                skipToBack();
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
                    keystoreStr = result;
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
