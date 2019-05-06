package org.ionchain.wallet.mvp.view.activity.imports;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.ionc.qrcode.activity.CaptureActivity;
import org.ionc.qrcode.activity.CodeUtils;
import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.callback.OnCreateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.Logger;
import org.ionc.wallet.utils.ToastUtil;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import java.util.List;

import static org.ionc.wallet.utils.RandomUntil.getNum;
import static org.ionchain.wallet.constant.ConstantParams.FROM_SCAN;
import static org.ionchain.wallet.constant.ConstantParams.SERVER_PROTOCOL_VALUE;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

public class ImportByKeystoreActivity extends AbsBaseActivity implements OnCreateWalletCallback, TextWatcher {
    private AppCompatEditText mKeystore;
    private AppCompatEditText pwdEt;
    private Button importBtn;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private String keystoreStr;

    @Override
    protected void setListener() {

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
        mKeystore.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        checkbox = findViewById(R.id.checkbox);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKeystore.getText() != null && pwdEt.getText() != null && checkbox.
                        isChecked()) {
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
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewAlphaAnimation(importBtn);
                keystoreStr = mKeystore.getText().toString();
                //读取keystore密码
                String pass = pwdEt.getText().toString();
                //生成keystory文件
                showProgress(getString(R.string.importing_wallet));
                IONCWalletSDK.getInstance().importWalletByKeyStore(pass, keystoreStr, (OnCreateWalletCallback) mActivity);
            }
        });
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipWeb(SERVER_PROTOCOL_VALUE);
            }
        });
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
        if (mKeystore.getText() != null && pwdEt.getText() != null && pwdEt.getText() != null) {
            String content = mKeystore.getText().toString().trim();
            String pwdstr = pwdEt.getText().toString().trim();

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && checkbox.isChecked()) {
                importBtn.setEnabled(true);
                importBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
            } else {
                importBtn.setEnabled(false);
                importBtn.setBackgroundColor(getResources().getColor(R.color.grey));
            }
        }
    }

    @Override
    public void onCreateSuccess(WalletBean walletBean) {
        WalletBean wallet = IONCWalletSDK.getInstance().getWalletByAddress(walletBean.getAddress());
        Logger.i(walletBean.toString());
        if (null != wallet) {
            ToastUtil.showLong(getAppString(R.string.wallet_name_exists));
        } else {
            walletBean.setMIconIdex(getNum(7));
            IONCWalletSDK.getInstance().saveWallet(walletBean);
            ToastUtil.showToastLonger(getAppString(R.string.import_success));
            skip(MainActivity.class);
        }
        hideProgress();
    }

    @Override
    public void onCreateFailure(String error) {
        hideProgress();
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
                    Logger.i(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    ToastUtil.showLong(getAppString(R.string.toast_qr_code_parase_error));
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
