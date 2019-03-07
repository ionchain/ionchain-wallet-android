package org.ionchain.wallet.mvp.view.activity.modify;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.bean.WalletBean;
import com.ionc.wallet.sdk.callback.OnModifyWalletPassWordCallback;
import com.ionc.wallet.sdk.utils.Logger;
import com.ionc.wallet.sdk.utils.StringUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.ToastUtil;

import java.util.Objects;

import static com.ionc.wallet.sdk.utils.StringUtils.check;
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
                try {
                    String old_pwd_input = Objects.requireNonNull(oldPwdEdit.getText()).toString().trim();
                    String new_pwd_input = Objects.requireNonNull(newPwdEt.getText()).toString().trim();
                    String renew_pwd_input = Objects.requireNonNull(resetNewPwdEt.getText()).toString().trim();
                    String pwd_dao = mWallet.getPassword();
                    if (TextUtils.isEmpty(old_pwd_input)) {
                        ToastUtil.showShortToast("请输入旧密码");
                        return;
                    }
                    if (TextUtils.isEmpty(new_pwd_input)) {
                        ToastUtil.showShortToast("请输入新密码");
                        return;
                    }
                    old_pwd_input = StringUtils.getSHA(old_pwd_input);
                    if (!pwd_dao.equals(old_pwd_input)) {
                        Log.i(getTAG(), "旧密码错误: " + pwd_dao);
                        ToastUtil.showShortToast("旧密码错误");
                        return;
                    }
                    if (!new_pwd_input.equals(renew_pwd_input)) {
                        ToastUtil.showShortToast("新密码两次输入不一至,请重新输入");
                        return;
                    }
                    if (!check(new_pwd_input) || !check(renew_pwd_input)) {
                        ToastUtil.showToastLonger("新密码不符合要求！");
                        return;
                    }

                    IONCWalletSDK.getInstance()
                            .modifyPassWord(mWallet, new_pwd_input, ModifyWalletPwdActivity.this);
                    showProgress("正在修改密码");
                } catch (NullPointerException e) {
                    ToastUtil.showLong("请按要求输入内容!");
                }

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
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mWallet = (WalletBean) intent.getSerializableExtra(SERIALIZABLE_DATA_WALLET_BEAN);
    }

    @Override
    protected void initView() {
        findViews();
        getMImmersionBar().titleView(findViewById(R.id.toolbarlayout)).statusBarDarkFont(true).execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_wallet_pwd;
    }

    @Override
    public void onModifySuccess(WalletBean walletBean) {
        hideProgress();
        Logger.i(getTAG(), "onModifySuccess: " + walletBean.getKeystore());
        ToastUtil.showShortToast("修改密码成功");
        Intent intent = new Intent();
        intent.putExtra(SERIALIZABLE_DATA_WALLET_BEAN, walletBean);
        setResult(REQUEST_MODIFY_WALLET_PWD, intent);
        finish();
    }

    @Override
    public void onModifyFailure(String error) {
        Logger.e(getTAG(), "onModifyFailure: " + error);
        ToastUtil.showShortToast("修改密码失败");
        hideProgress();
    }
}
