package org.ionchain.wallet.mvp.view.activity;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.mvp.callback.OnCreateWalletCallback;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.StringUtils;
import org.ionchain.wallet.utils.ToastUtil;

import static org.ionchain.wallet.utils.RandomUntil.getNum;

public class ImportWalletActivity extends AbsBaseActivity implements TextWatcher, OnCreateWalletCallback {

    private AppCompatEditText mPrivateKey;
    private AppCompatEditText mPwd;
    private AppCompatEditText mRepeatPwd;
    private Button importBtn;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-16 23:25:34 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */

    private void findViews() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        ImageView back = (ImageView) findViewById(R.id.back);
        ImageView scan = (ImageView) findViewById(R.id.scan);
        mPrivateKey = (AppCompatEditText) findViewById(R.id.contentEt);
        mPwd = (AppCompatEditText) findViewById(R.id.pwdEt);
        mRepeatPwd = (AppCompatEditText) findViewById(R.id.repwdEt);
        TextView linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
        importBtn = (Button) findViewById(R.id.importBtn);
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                skip(WebViewActivity.class, Comm.SERIALIZABLE_DATA, Comm.URL_AGREE, Comm.SERIALIZABLE_DATA1, "条款");
//                showProgress("正在导入钱包请稍候");
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ScanActivity.class, 999);
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mPrivateKey.getText().toString().trim();
                String resetpass = mRepeatPwd.getText().toString().trim();
                String pass = mPwd.getText().toString().trim();
                if (StringUtils.isEmpty(content)) {
                    ToastUtil.showToastLonger("私钥为空！");
                    return;
                }
                if (content.length() != 64) {
                    ToastUtil.showToastLonger("无效私钥！");

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
                        .importPrivateKey(content, pass, ImportWalletActivity.this);
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
    protected void handleIntent() {
        super.handleIntent();
        Intent intent = getIntent();
//        boolean isAddMode = intent.getBooleanExtra(Comm.JUMP_PARM_ISADDMODE, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 999) {
            String result = data.getStringExtra("result");
            mPrivateKey.setText(result);
        }
    }


    @Override
    protected void initData() {
        mImmersionBar.titleBar(R.id.import_header)
                .statusBarDarkFont(true)
                .execute();
    }


    @Override
    protected void initView() {
        findViews();
        mPrivateKey.addTextChangedListener(this);
        mPwd.addTextChangedListener(this);
        mRepeatPwd.addTextChangedListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_wallet;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String contentstr = mPrivateKey.getText().toString().trim();
        String pwdstr = mPwd.getText().toString().trim();
        String resetpwdstr = mRepeatPwd.getText().toString().trim();

        if (!TextUtils.isEmpty(contentstr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)) {
            importBtn.setEnabled(true);
        } else {
            importBtn.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCreateSuccess(WalletBean walletBean) {
        walletBean.setMIconIdex(getNum(7));
        WalletDaoTools.saveWallet(walletBean);
        hideProgress();
        ToastUtil.showToastLonger("导入成功啦!");
        Log.i(TAG, "onCreateSuccess: "+walletBean.toString());
        skip(MainActivity.class);
    }

    @Override
    public void onCreateFailure(String result) {
        hideProgress();
        ToastUtil.showToastLonger("导入成失败");
        Log.i(TAG, "onCreateFailure: " + result);
    }
}
