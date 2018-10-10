package org.ionchain.wallet.mvp.view.activity.createwallet;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.mvp.callback.OnCreateWalletCallback;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.activity.importmode.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.constant.ConstantParams.FROM_WELCOME;

public class CreateWalletActivity extends AbsBaseActivity implements TextWatcher, OnCreateWalletCallback {

    final int REQUEST_CODE_CREATE_PERMISSIONS = 1;
    //判定是否需要刷新
    private WalletBean mCreateWallet = null;


    private RelativeLayout toolbarlayout;
    private ImageView back;
    private AppCompatEditText walletNameEt;
    private AppCompatEditText pwdEt;
    private AppCompatEditText resetPwdEt;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private Button createBtn;
    private TextView importBtn;

    private boolean isWelcome;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-17 19:48:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        toolbarlayout = (RelativeLayout) findViewById(R.id.toolbarlayout);
        back = (ImageView) findViewById(R.id.back);
        walletNameEt = (AppCompatEditText) findViewById(R.id.walletNameEt);
        pwdEt = (AppCompatEditText) findViewById(R.id.pwdEt);
        resetPwdEt = (AppCompatEditText) findViewById(R.id.resetPwdEt);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
        createBtn = (Button) findViewById(R.id.createBtn);
        importBtn = (TextView) findViewById(R.id.importBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walletNameEt.getText() != null && pwdEt.getText() != null && resetPwdEt.getText() != null) {
                    String content = walletNameEt.getText().toString().trim();
                    String pwdstr = pwdEt.getText().toString().trim();
                    String resetpwdstr = resetPwdEt.getText().toString().trim();

                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)&&checkbox.isChecked()) {
                        createBtn.setEnabled(true);
                        createBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
                    } else {
                        createBtn.setEnabled(false);
                        createBtn.setBackgroundColor(getResources().getColor(R.color.grey));
                    }
                }
            }
        });
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String walletname = "";
                String resetpass = "";
                String pass = "";//获取密码
                if (walletNameEt.getText() == null) {
                    ToastUtil.showToastLonger("名字不能为空！");
                    return;
                }
                if (pwdEt.getText() == null) {
                    ToastUtil.showToastLonger("密码不能为空！");
                    return;
                }
                if (resetPwdEt.getText() == null) {
                    ToastUtil.showToastLonger("重复密码不能为空！");
                    return;
                }
                walletname = walletNameEt.getText().toString().trim();
                /*
                 * 从数据库比对，重复检查
                 * */
                if (null != WalletDaoTools.getWalletByName(walletname)) {
                    Toast.makeText(mActivity.getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                pass = pwdEt.getText().toString().trim();//获取密码
                resetpass = resetPwdEt.getText().toString().trim();
                if (resetpass.length() < 8 || pass.length() < 8) {
                    ToastUtil.showToastLonger("密码长度不能小于8！");
                    return;
                }
                if (!resetpass.equals(pass)) {
                    ToastUtil.showShortToast("密码和重复密码必须相同");
                    return;
                }


                WalletManager.getInstance().createBip39Wallet(walletname, pass, CreateWalletActivity.this);
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(SelectImportModeActivity.class);
            }
        });
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                skip(WebViewActivity.class, Comm.SERIALIZABLE_DATA, Comm.URL_AGREE, Comm.SERIALIZABLE_DATA1, "条款");
            }
        });
        walletNameEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        resetPwdEt.addTextChangedListener(this);
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        isWelcome = intent.getBooleanExtra(FROM_WELCOME, false);
    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleBar(R.id.toolbarlayout).statusBarDarkFont(true).execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_create;
    }

    @Override
    protected void initData() {

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String walletnamestr = walletNameEt.getText().toString().trim();
        String pwdstr = pwdEt.getText().toString().trim();
        String resetpwdstr = resetPwdEt.getText().toString().trim();

        if (!TextUtils.isEmpty(walletnamestr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)) {
            createBtn.setEnabled(true);
            createBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
        } else {
            createBtn.setEnabled(false);
            createBtn.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }


    @Override
    public void onCreateSuccess(WalletBean walletBean) {
        Log.i(TAG, "onCreateSuccess: " + walletBean);
        if (isWelcome) {
            walletBean.setIsShowWallet(true);
        } else {
            walletBean.setIsShowWallet(false);
        }
        WalletDaoTools.saveWallet(walletBean);
        SoftKeyboardUtil.hideSoftKeyboard(this);
        skip(MainActivity.class);
    }

    @Override
    public void onCreateFailure(String result) {
        Log.i(TAG, "onCreateFailure: " + result);
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }
}
