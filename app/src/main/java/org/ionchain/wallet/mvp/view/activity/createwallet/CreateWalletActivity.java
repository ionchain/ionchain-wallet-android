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
import org.ionchain.wallet.helper.Web3jHelper;
import org.ionchain.wallet.mvp.callback.OnImportMnemonicCallback;
import org.ionchain.wallet.mvp.callback.OnSimulateTimeConsume;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.mvp.view.activity.importmode.SelectImportModeActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.constant.ConstantParams.FROM_WELCOME;
import static org.ionchain.wallet.utils.StringUtils.check;

public class CreateWalletActivity extends AbsBaseActivity implements TextWatcher, OnImportMnemonicCallback, OnSimulateTimeConsume {

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
    private String walletnamestr;
    private String pass;
    private String resetpass;

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

                    if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr) && checkbox.isChecked()) {
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

                /*
                 * 从数据库比对，重复检查
                 * */
                if (null != WalletDaoTools.getWalletByName(walletnamestr)) {
                    Toast.makeText(getMActivity().getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!check(resetpass) || !check(pass)) {
                    ToastUtil.showToastLonger("密码不符合要求！");
                    return;
                }
                if (!resetpass.equals(pass)) {
                    ToastUtil.showShortToast("密码和重复密码必须相同");
                    return;
                }

                showProgress("正在创建钱包……");
                Web3jHelper.simulateTimeConsuming(CreateWalletActivity.this);

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
        getMImmersionBar().titleBar(R.id.toolbarlayout).statusBarDarkFont(true).execute();
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
        if (walletNameEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包名字");
            return;
        }
        if (pwdEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包密码");
            return;
        }
        if (resetPwdEt.getText() == null) {
            ToastUtil.showToastLonger("请输入钱包重复密码");
            return;
        }
        walletnamestr = walletNameEt.getText().toString().trim();
        pass = pwdEt.getText().toString().trim();
        resetpass = resetPwdEt.getText().toString().trim();

        if (!TextUtils.isEmpty(walletnamestr) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(resetpass)) {
            createBtn.setEnabled(true);
            createBtn.setBackgroundColor(getResources().getColor(R.color.blue_top));
        } else {
            createBtn.setEnabled(false);
            createBtn.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }


    @Override
    public void onImportMnemonicSuccess(WalletBean walletBean) {
        Log.i(getTAG(), "onCreateSuccess: " + walletBean);
        hideProgress();
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
    public void onImportMnemonicFailure(String error) {
        hideProgress();
        ToastUtil.showToastLonger(error);
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }

    @Override
    public void onSimulateFinish() {
        Web3jHelper.getInstance().createBip39Wallet(walletnamestr, pass, CreateWalletActivity.this);
    }
}
