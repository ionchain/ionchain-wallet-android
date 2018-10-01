package org.ionchain.wallet.mvp.view.activity.importmode;

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
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.StringUtils;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Arrays;

import static org.ionchain.wallet.utils.RandomUntil.getNum;

public class ImportByMnemonicActivity extends AbsBaseActivity implements TextWatcher, OnCreateWalletCallback {
    private RelativeLayout importHeader;
    private ImageView back;
    private AppCompatEditText mnemonic;
    private AppCompatEditText pwdEt;
    private AppCompatEditText repwdEt;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private Button importBtn;

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
        repwdEt = (AppCompatEditText) findViewById(R.id.repwdEt);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
        importBtn = (Button) findViewById(R.id.importBtn);
    }

    @Override
    protected void setListener() {
        super.setListener();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mnemonic.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        repwdEt.addTextChangedListener(this);

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mnemonic.getText() == null) {
                    ToastUtil.showToastLonger("助记词不能为空！");
                    return;
                }
                if (pwdEt.getText() == null) {
                    ToastUtil.showToastLonger("密码不能为空！");
                    return;
                }
                if (repwdEt.getText() == null) {
                    ToastUtil.showToastLonger("重复密码不能为空！");
                    return;
                }
                String content = mnemonic.getText().toString().trim();
                String resetpass = pwdEt.getText().toString().trim();
                String pass = repwdEt.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    ToastUtil.showToastLonger("助记词有误");
                    return;
                }

                WalletBean wallet = WalletDaoTools.getWalletByPrivateKey(content);
                if (null != wallet) {
                    Toast.makeText(mActivity, "该钱包已存在,钱包名 : " + wallet.getName(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!resetpass.equals(pass)) {
                    Toast.makeText(mActivity.getApplicationContext(), "密码和重复密码必须相同", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgress("正在导入钱包请稍候");
                WalletManager.getInstance()
                        .importWalletByMnemonicCode("", Arrays.asList(content.split(" ")), pass, ImportByMnemonicActivity.this);
            }
        });
    }

    @Override
    protected void initData() {

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
        if (mnemonic.getText() != null && pwdEt.getText() != null && repwdEt.getText() != null) {
            String content = mnemonic.getText().toString().trim();
            String pwdstr = pwdEt.getText().toString().trim();
            String resetpwdstr = repwdEt.getText().toString().trim();

            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)) {
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
        walletBean.setMIconIdex(getNum(7));
        WalletDaoTools.saveWallet(walletBean);
        hideProgress();
        ToastUtil.showToastLonger("导入成功啦!");
        Log.i(TAG, "onCreateSuccess: " + walletBean.toString());
        skip(MainActivity.class);
    }

    @Override
    public void onCreateFailure(String result) {
        hideProgress();
        ToastUtil.showToastLonger("导入成失败");
        Log.i(TAG, "onCreateFailure: " + result);
    }
}
