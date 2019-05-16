package org.ionchain.wallet.mvp.view.activity.modify;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnDeletefinishCallback;
import org.ionc.wallet.callback.OnImportPrivateKeyCallback;
import org.ionc.wallet.callback.OnModifyWalletPassWordCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.Logger;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.mvp.view.activity.create.CreateWalletSelectActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.IONCTitleBar;
import org.ionchain.wallet.widget.dialog.check.DialogPasswordCheck;
import org.ionchain.wallet.widget.dialog.export.DialogTextMessage;
import org.ionchain.wallet.widget.dialog.modify.ModifyDialog;
import org.ionchain.wallet.widget.dialog.modify.OnModifyPasswordCallback;
import org.web3j.utils.Files;

import java.io.File;
import java.io.IOException;

import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_WALLET_BEAN;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;

/**
 * 修改钱包：钱包名、修改密码、导出私钥
 */
public class ModifyAndExportWalletActivity extends AbsBaseActivity implements
        OnImportPrivateKeyCallback,
        View.OnClickListener,
        OnDeletefinishCallback,
        OnModifyPasswordCallback,
        OnModifyWalletPassWordCallback,
        OnCheckWalletPasswordCallback,
        DialogTextMessage.OnBtnClickedListener {


    WalletBean mWallet;


    private Button delBtn;
    private TextView walletBalanceTv;
    private TextView rmbBalanceTv;
    private TextView walletAddressTv;
    private AppCompatEditText walletNameEt;
    private RelativeLayout modifyPwdLayout;
    private RelativeLayout importLayout;
    private RelativeLayout import_mnemonic_layout;
    private RelativeLayout import_key_store_layout;
    private boolean bShowMnemonicRl;//是否显示导出助记词条目
    private boolean bShowKSRl;//是否显示导出keystore

    private char flag = 0;//默认导出助记词

    private ModifyDialog modifyDialog;
    private DialogPasswordCheck passwordCheck;
    private String newPassword = "";
    private String currentPassword = "";
    private final char FLAG_EXPORT_KS = 0;
    private final char FLAG_EXPORT_PRIVATE = 1;
    private final char FLAG_MODIFY_PWD = 2;
    private final char FLAG_DELETE_WALLET = 3;
    private String privateKey;
    private String json;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-26 14:47:05 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        delBtn = (Button) findViewById(R.id.delBtn);
        walletBalanceTv = (TextView) findViewById(R.id.walletBalanceTv);
        rmbBalanceTv = (TextView) findViewById(R.id.rmbBalanceTv);
        walletAddressTv = (TextView) findViewById(R.id.walletAddressTv);
        walletNameEt = (AppCompatEditText) findViewById(R.id.walletNameEt);
        modifyPwdLayout = (RelativeLayout) findViewById(R.id.modifyPwdLayout);
        importLayout = (RelativeLayout) findViewById(R.id.import_pri_layout);
        import_mnemonic_layout = (RelativeLayout) findViewById(R.id.import_mnemonic_layout);
        import_key_store_layout = (RelativeLayout) findViewById(R.id.import_key_store_layout);
        if (!bShowMnemonicRl) {
            import_mnemonic_layout.setVisibility(View.GONE);
        }
        if (!bShowKSRl) {
            import_key_store_layout.setVisibility(View.GONE);
        }
        if (IONCWalletSDK.getInstance().getAllWallet().size() == 1) {
            delBtn.setVisibility(View.GONE);
        }
        import_mnemonic_layout.setOnClickListener(this);
        delBtn.setOnClickListener(this);
        modifyPwdLayout.setOnClickListener(this);
        import_key_store_layout.setOnClickListener(this);
        importLayout.setOnClickListener(this);
    }

    @Override
    protected void initData() {

        if (mWallet == null) {
            return;
        }
        if (!TextUtils.isEmpty(mWallet.getAddress())) {
            walletAddressTv.setText(mWallet.getAddress());
        }

        if (!TextUtils.isEmpty(mWallet.getName())) {
            walletNameEt.setText(mWallet.getName());
        }
        if (!TextUtils.isEmpty(mWallet.getBalance())) {
            walletBalanceTv.setText(mWallet.getBalance());

        }
        if (!TextUtils.isEmpty(mWallet.getRmb())) {
            rmbBalanceTv.setText(mWallet.getRmb());
        }

    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mWallet = (WalletBean) intent.getParcelableExtra(PARCELABLE_WALLET_BEAN);
        bShowMnemonicRl = !StringUtils.isEmpty(mWallet.getMnemonic());
        bShowKSRl = !StringUtils.isEmpty(mWallet.getKeystore());
    }

    @Override
    protected void initView() {
        findViews();
        final IONCTitleBar ioncTitleBar = findViewById(R.id.ionc_title_bar);
        mImmersionBar.titleView(ioncTitleBar).statusBarDarkFont(true).execute();
        ioncTitleBar.setTitle(mWallet.getName());
        ioncTitleBar.setLeftImgRes(R.mipmap.arrow_back_white);
        ioncTitleBar.setLeftBtnCLickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftKeyboardUtil.hideSoftKeyboard(ModifyAndExportWalletActivity.this);
                finish();
            }
        });
        ioncTitleBar.setRightTextCLickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walletNameEt.getText() != null && !StringUtils.isEmpty(walletNameEt.getText().toString())) {
                    mWallet.setName(walletNameEt.getText().toString());
                    ioncTitleBar.setTitle(walletNameEt.getText().toString());
                    SoftKeyboardUtil.hideSoftKeyboard(ModifyAndExportWalletActivity.this);
                    IONCWalletSDK.getInstance().updateWallet(mWallet);
                } else {
                    ToastUtil.showShort(getAppString(R.string.wallet_name_must_not_empty));
                }

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_modify;
    }


    /**
     * 导出私钥
     *
     * @param privateKey
     */
    private void showImportPrivateKeyDialog(final String privateKey) {
        this.privateKey = privateKey;
        new DialogTextMessage(this).setTitle(getAppString(R.string.export_private_key)).setCancelByBackKey(true).setMessage(privateKey)
                .setCopyBtnClickedListener(this).show();

    }


    @Override
    public void onImportPriKeySuccess(String privateKey) {
        showImportPrivateKeyDialog(privateKey);
        hideProgress();
    }

    @Override
    public void onImportPriKeyFailure(String erroe) {
        hideProgress();
    }

    @Override
    public void onClick(View v) {
        setViewAlphaAnimation(v);
        passwordCheck = new DialogPasswordCheck(this);

        switch (v.getId()) {
            case R.id.copyBtn://复制
                StringUtils.copy(this, mWallet.getPrivateKey());
                ToastUtil.showShortToast(getAppString(R.string.copy_done));
                break;
            case R.id.delBtn://删除钱包
                flag = FLAG_DELETE_WALLET;
                passwordCheck.setLeftBtnText(getAppString(R.string.cancel));
                passwordCheck.setRightBtnText(getAppString(R.string.sure));
                passwordCheck.setTitleText(getAppString(R.string.please_input_wallet_password));
                passwordCheck.setLeftBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passwordCheck.dismiss();
                    }
                });
                passwordCheck.setRightBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (StringUtils.isEmpty(passwordCheck.getPasswordEt().getText().toString())) {
                            ToastUtil.showToastLonger(getAppString(R.string.please_input_wallet_password));
                            return;
                        }
                        String p_input = passwordCheck.getPasswordEt().getText().toString();
                        IONCWalletSDK.getInstance().checkPassword(p_input, mWallet.getKeystore(), ModifyAndExportWalletActivity.this);
                    }
                });
                passwordCheck.show();
                break;
            case R.id.modifyPwdLayout://修改密码
                flag = FLAG_MODIFY_PWD;
                modifyDialog = new ModifyDialog(this, this);
                modifyDialog.show();
                break;
            case R.id.import_pri_layout://导出私钥
                flag = FLAG_EXPORT_PRIVATE;
                passwordCheck.setTitleText(getAppString(R.string.export_private_key));
                passwordCheck.setLeftBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passwordCheck.dismiss();
                    }
                });
                passwordCheck.setRightBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*比对密码是否正确*/
                        String pwd1 = passwordCheck.getPasswordEt().getText().toString();
                        IONCWalletSDK.getInstance().checkPassword(pwd1, mWallet.getKeystore(), ModifyAndExportWalletActivity.this);
                    }
                });
                passwordCheck.show();
                break;
            case R.id.import_key_store_layout://导出KS
                flag = FLAG_EXPORT_KS;
                passwordCheck.setLeftBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passwordCheck.dismiss();
                    }
                });
                passwordCheck.setRightBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*比对密码是否正确*/
                        String pwd1 = passwordCheck.getPasswordEt().getText().toString();
                        IONCWalletSDK.getInstance().checkPassword(pwd1, mWallet.getKeystore(), ModifyAndExportWalletActivity.this);
                    }
                });
                passwordCheck.show();
                break;

        }
    }

    /**
     * 删除完成:检查数据库是否还有钱包,如果有将数据库第一个钱包设置为首页展示钱包
     */
    @Override
    public void onDeleteFinish() {
        //检查是否还有钱包
        if (IONCWalletSDK.getInstance().getAllWallet() == null || IONCWalletSDK.getInstance().getAllWallet().size() > 0) {
            IONCWalletSDK.getInstance().getAllWallet().get(0).setIsMainWallet(true);
            IONCWalletSDK.getInstance().saveWallet(IONCWalletSDK.getInstance().getAllWallet().get(0));
        } else {
            //去创建钱包
            Intent intent1 = new Intent(this, CreateWalletSelectActivity.class);
            startActivity(intent1);
            finish();
        }
    }

    /**
     * 修改密码的对话框回到函数
     *
     * @param currentPassword  当前密码
     * @param newPassword      新密码
     * @param newPasswordAgain 重复新密码
     */
    @Override
    public void onModifyDialogParam(String currentPassword, String newPassword, String newPasswordAgain) {
        this.newPassword = newPassword;
        this.currentPassword = currentPassword;
        IONCWalletSDK.getInstance().checkPassword(currentPassword, mWallet.getKeystore(), this);
    }

    @Override
    public void onModifySuccess(WalletBean walletBean) {
        mWallet = walletBean;
        hideProgress();
        ToastUtil.showShortToast(getAppString(R.string.modify_password_success));
    }

    @Override
    public void onModifyFailure(String error) {
        ToastUtil.showShortToast(getAppString(R.string.modify_password_failure));
        hideProgress();
    }

    @Override
    public void onCheckWalletPasswordSuccess(WalletBean bean) {
        passwordCheck.dismiss();
        switch (flag) {
            case FLAG_DELETE_WALLET:
                IONCWalletSDK.getInstance().deleteWallet(mWallet, ModifyAndExportWalletActivity.this);
                finish();
                break;
            case FLAG_EXPORT_KS:
                try {
                    String path = mWallet.getKeystore();
                    json = Files.readString(new File(path));
                    Logger.i(json);
                    DialogTextMessage d = new DialogTextMessage(this);
                    d.setMessage(json);
                    d.setTitle(getAppString(R.string.export_keystore));
                    d.setCancelByBackKey(true);
                    d.setCopyBtnClickedListener(this);
                    d.show();
                } catch (IOException e) {
                    ToastUtil.showToastLonger(e.getMessage());
                }
                break;
            case FLAG_EXPORT_PRIVATE:
                showImportPrivateKeyDialog(bean.getPrivateKey());
                break;
            case FLAG_MODIFY_PWD:
                modifyDialog.dismiss();
                IONCWalletSDK.getInstance()
                        .modifyPassWord(bean, newPassword, this);
                showProgress(getAppString(R.string.modifying_password));
                break;
        }

    }

    @Override
    public void onCheckWalletPasswordFailure(String errorMsg) {
        Logger.e(errorMsg);
        ToastUtil.showToastLonger(getAppString(R.string.please_check_password));
        if (modifyDialog != null) {
            modifyDialog.dismiss();
        }
        if (passwordCheck != null) {
            passwordCheck.dismiss();
        }
    }

    @Override
    public void onDialogTextMessageBtnClicked(DialogTextMessage dialogTextMessage) {
        switch (flag) {
            case FLAG_EXPORT_PRIVATE:
                //复制
                StringUtils.copy(ModifyAndExportWalletActivity.this, privateKey);
                ToastUtil.showToastLonger(getAppString(R.string.copy_done_private));
                break;
            case FLAG_EXPORT_KS:
                StringUtils.copy(ModifyAndExportWalletActivity.this, json);
                ToastUtil.showToastLonger(getAppString(R.string.copy_done_ks));
                break;
        }

    }
}
