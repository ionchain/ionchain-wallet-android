package org.ionchain.wallet.ui.wallet;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnModifyWalletPassWordCallback;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import static org.ionchain.wallet.constant.ConstantParams.REQUEST_MODIFY_WALLET_PWD;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA_WALLET_BEAN;

public class ModifyWalletPwdActivity extends AbsBaseActivity implements OnModifyWalletPassWordCallback {


    WalletBean mWallet;
    private RelativeLayout toolbarlayout;
    private ImageView back;
    private TextView completBtn;
    private AppCompatEditText oldPwdEdit;
    private AppCompatEditText newPwdEt;
    private AppCompatEditText resetNewPwdEt;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-26 10:05:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        toolbarlayout = (RelativeLayout) findViewById(R.id.toolbarlayout);
        back = (ImageView) findViewById(R.id.back);
        completBtn = (TextView) findViewById(R.id.completBtn);
        oldPwdEdit = (AppCompatEditText) findViewById(R.id.oldPwdEdit);
        newPwdEt = (AppCompatEditText) findViewById(R.id.newPwdEt);
        resetNewPwdEt = (AppCompatEditText) findViewById(R.id.resetNewPwdEt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        completBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpwdstr = oldPwdEdit.getText().toString().trim();
                String newpwdstr = newPwdEt.getText().toString().trim();
                String resetnewpwdstr = resetNewPwdEt.getText().toString().trim();
                String old_pwd = mWallet.getPassword();
                if (TextUtils.isEmpty(oldpwdstr)) {
                    ToastUtil.showShortToast("当前密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(newpwdstr)) {
                    ToastUtil.showShortToast("新密码不能为空");
                    return;
                }
                if (!old_pwd.equals(oldpwdstr)) {
                    Log.i(TAG, "旧密码错误: " + old_pwd);
                    ToastUtil.showShortToast("旧密码错误");
                    return;
                }
                if (!newpwdstr.equals(resetnewpwdstr)) {
                    ToastUtil.showShortToast("新密码两次输入不一至,请重新输入");
                    return;
                }
                WalletManager.getInstance()
                        .modifyPassWord(mWallet, newpwdstr, ModifyWalletPwdActivity.this);
                showProgress("正在修改密码");
            }
        });
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void handleIntent() {
        super.handleIntent();
        mWallet = (WalletBean) getIntent().getSerializableExtra(SERIALIZABLE_DATA_WALLET_BEAN);
    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleBar(findViewById(R.id.toolbarlayout)).statusBarDarkFont(true).execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_wallet_pwd;
    }

    @Override
    public void onModifySuccess(WalletBean walletBean) {
        hideProgress();
        Log.i(TAG, "onModifySuccess: " + walletBean.getKeystore());
        ToastUtil.showShortToast("修改密码成功");
        Intent intent = new Intent();
        intent.putExtra(SERIALIZABLE_DATA_WALLET_BEAN, walletBean);
        setResult(REQUEST_MODIFY_WALLET_PWD, intent);
        finish();
    }

    @Override
    public void onModifyFailure(String error) {
        Log.i(TAG, "onModifyFailure: " + error);
        ToastUtil.showShortToast("修改密码失败");
        hideProgress();
    }
}
