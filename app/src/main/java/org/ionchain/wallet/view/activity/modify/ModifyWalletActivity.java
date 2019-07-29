package org.ionchain.wallet.view.activity.modify;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.callback.OnCheckWalletPasswordCallback;
import org.ionc.wallet.callback.OnDeletefinishCallback;
import org.ionc.wallet.callback.OnImportPrivateKeyCallback;
import org.ionc.wallet.callback.OnUpdateWalletCallback;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionc.wallet.utils.LoggerUtils;
import org.ionc.wallet.utils.StringUtils;
import org.ionchain.wallet.R;
import org.ionchain.wallet.utils.SPUtils;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.view.activity.create.CreateWalletSelectActivity;
import org.ionchain.wallet.view.base.AbsBaseActivityTitleThreeTv;
import org.ionchain.wallet.view.widget.dialog.check.DialogPasswordCheck;
import org.ionchain.wallet.view.widget.dialog.export.DialogTextMessage;
import org.ionchain.wallet.view.widget.dialog.modify.ModifyPasswordDialog;
import org.ionchain.wallet.view.widget.dialog.modify.OnModifyPasswordDialogCallback;
import org.web3j.utils.Files;

import java.io.File;
import java.io.IOException;

import static org.ionc.wallet.utils.StringUtils.check;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_CNY;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_IDR;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_KRW;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_USD;
import static org.ionchain.wallet.constant.ConstantParams.PARCELABLE_WALLET_BEAN;
import static org.ionchain.wallet.utils.AnimationUtils.setViewAlphaAnimation;


/**
 * 修改钱包：钱包名、修改密码、导出私钥
 */
public class ModifyWalletActivity extends AbsBaseActivityTitleThreeTv implements
        OnImportPrivateKeyCallback,
        View.OnClickListener,
        OnDeletefinishCallback,
        OnModifyPasswordDialogCallback,
        OnCheckWalletPasswordCallback,
        DialogTextMessage.OnBtnClickedListener, OnUpdateWalletCallback {


    WalletBeanNew mWallet;


    private Button delBtn;
    private TextView walletBalanceTv;
    private TextView rmbBalanceTv;
    private TextView walletAddressTv;
    private TextView coin_type;
    private AppCompatEditText walletNameEt;
    private RelativeLayout modifyPwdLayout;
    private RelativeLayout importLayout;
    private RelativeLayout import_mnemonic_layout;
    private RelativeLayout import_key_store_layout;
    private boolean bShowMnemonicRl;//是否显示导出助记词条目
    private boolean bShowKSRl;//是否显示导出keystore

    private char flag = 0;//默认导出助记词

    private ModifyPasswordDialog mModifyPasswordDialog;
    private DialogPasswordCheck passwordCheck;
    private String newPassword = "";
    private String currentPassword = "";
    private final char FLAG_EXPORT_KS = 0;
    private final char FLAG_EXPORT_PRIVATE = 1;
    private final char FLAG_MODIFY_PWD = 2;
    private final char FLAG_DELETE_WALLET = 3;
    private String privateKey;
    private String json;
    private DialogPasswordCheck deleteWallet;
    private DialogPasswordCheck exportPK;
    private DialogPasswordCheck exportKS;

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
        coin_type = (TextView) findViewById(R.id.coin_type);
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
        if (IONCWalletSDK.getInstance().getAllWalletNew().size() == 1) {
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
        switch (SPUtils.getInstance().getCoinType()) {
            case COIN_TYPE_CNY:
                rmbBalanceTv.setText(mWallet.getRmb());
                break;
            case COIN_TYPE_IDR:
                rmbBalanceTv.setText(mWallet.getIdr());
                break;
            case COIN_TYPE_KRW:
                rmbBalanceTv.setText(mWallet.getKrw());
                break;
            case COIN_TYPE_USD:
                rmbBalanceTv.setText(mWallet.getUs());
                break;
        }
        coin_type.setText(SPUtils.getInstance().getCoinType());

        if (!TextUtils.isEmpty(mWallet.getAddress())) {
            walletAddressTv.setText(mWallet.getAddress());
        }

        if (!TextUtils.isEmpty(mWallet.getName())) {
            walletNameEt.setText(mWallet.getName());
        }
        if (!TextUtils.isEmpty(mWallet.getBalance())) {
            walletBalanceTv.setText(mWallet.getBalance());

        }
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mWallet = (WalletBeanNew) intent.getParcelableExtra(PARCELABLE_WALLET_BEAN);
        bShowMnemonicRl = !StringUtils.isEmpty(mWallet.getMnemonic());
        bShowKSRl = !StringUtils.isEmpty(mWallet.getKeystore());
    }

    @Override
    protected void initView() {
        findViews();

        mTitleRightTv.setOnClickListener(v -> {
            if (walletNameEt.getText() != null && !StringUtils.isEmpty(walletNameEt.getText().toString())) {
                mWallet.setName(walletNameEt.getText().toString());
                mTitleNameTv.setText(walletNameEt.getText().toString());
                SoftKeyboardUtil.hideSoftKeyboard(ModifyWalletActivity.this);
                IONCWalletSDK.getInstance().updateWallet(mWallet);
                ToastUtil.showShort(getAppString(R.string.has_been_done));
            } else {
                ToastUtil.showShort(getAppString(R.string.wallet_name_must_not_empty));
            }
        });
       
    }

    @Override
    protected String getTitleName() {
        return mWallet.getName();
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

        switch (v.getId()) {
            case R.id.copyBtn://复制
                StringUtils.copy(this, mWallet.getPrivateKey());
                ToastUtil.showShortToast(getAppString(R.string.copy_done));
                break;
            case R.id.delBtn://删除钱包
                deleteWallet = new DialogPasswordCheck(this);

                flag = FLAG_DELETE_WALLET;
                deleteWallet.setLeftBtnText(getAppString(R.string.cancel));
                deleteWallet.setRightBtnText(getAppString(R.string.sure));
                deleteWallet.setTitleText(getAppString(R.string.please_input_wallet_password));
                deleteWallet.setLeftBtnClickedListener(v12 -> {
                    deleteWallet.dismiss();  //删除钱包
                });
                deleteWallet.setRightBtnClickedListener(v1 -> {
                    if (StringUtils.isEmpty(deleteWallet.getPasswordEt().getText().toString())) {
                        ToastUtil.showToastLonger(getAppString(R.string.please_input_wallet_password));
                        return;
                    }
                    String p_input = deleteWallet.getPasswordEt().getText().toString();
                    IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWallet, p_input, mWallet.getKeystore(), ModifyWalletActivity.this); //删除钱包的时候检查密码
                });
                deleteWallet.show();//删除钱包
                break;
            case R.id.modifyPwdLayout://修改密码
                flag = FLAG_MODIFY_PWD;
                mModifyPasswordDialog = new ModifyPasswordDialog(this, this);
                mModifyPasswordDialog.show();
                break;
            case R.id.import_pri_layout://导出私钥
                flag = FLAG_EXPORT_PRIVATE;
                exportPK = new DialogPasswordCheck(this);
                exportPK.setTitleText(getAppString(R.string.export_private_key));
                exportPK.setLeftBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exportPK.dismiss(); //导出私钥
                    }
                });
                exportPK.setRightBtnClickedListener(v15 -> {
                    /*比对密码是否正确*/
                    String pwd1 = exportPK.getPasswordEt().getText().toString();
                    IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWallet, pwd1, mWallet.getKeystore(), ModifyWalletActivity.this); //导出私钥
                });
                exportPK.show();//导出私钥
                break;
            case R.id.import_key_store_layout://导出KS
                flag = FLAG_EXPORT_KS;
                exportKS = new DialogPasswordCheck(this);
                exportKS.setLeftBtnClickedListener(v13 -> {
                    exportKS.dismiss(); //导出KS
                });
                exportKS.setRightBtnClickedListener(v14 -> {
                    /*比对密码是否正确*/
                    String pwd1 = exportKS.getPasswordEt().getText().toString();
                    IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWallet, pwd1, mWallet.getKeystore(), ModifyWalletActivity.this);//导出KS
                });
                exportKS.show(); //导出KS
                break;

        }
    }

    /**
     * 删除完成:检查数据库是否还有钱包,如果有将数据库第一个钱包设置为首页展示钱包
     */
    @Override
    public void onDeleteFinish() {
        //检查是否还有钱包
        if (IONCWalletSDK.getInstance().getAllWalletNew() == null || IONCWalletSDK.getInstance().getAllWalletNew().size() > 0) {
            IONCWalletSDK.getInstance().getAllWalletNew().get(0).setIsMainWallet(true);
            IONCWalletSDK.getInstance().saveWallet(IONCWalletSDK.getInstance().getAllWalletNew().get(0));

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
    public void onModifyPasswordDialogParam(String currentPassword, String newPassword, String newPasswordAgain) {
        if (!check(newPassword) || !check(newPasswordAgain)) {
            ToastUtil.showToastLonger(getResources().getString(R.string.illegal_password));
            return;
        }
        mModifyPasswordDialog.dismiss();
        showProgress(getAppString(R.string.modifying_password));
        this.newPassword = newPassword;
        this.currentPassword = currentPassword;
        IONCWalletSDK.getInstance().checkCurrentWalletPassword(mWallet, currentPassword, mWallet.getKeystore(), this); //修改密码对话框
    }


    @Override
    public void onCheckWalletPasswordSuccess(WalletBeanNew bean) {
        switch (flag) {
            case FLAG_DELETE_WALLET:
                deleteWallet.dismiss();
                IONCWalletSDK.getInstance().deleteWallet(mWallet, ModifyWalletActivity.this);
                finish();
                break;
            case FLAG_EXPORT_KS:
                exportKS.dismiss();//导出KS
                try {
                    String path = mWallet.getKeystore();
                    json = Files.readString(new File(path));
                    LoggerUtils.i(json);
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
                exportPK.dismiss();
                showImportPrivateKeyDialog(bean.getPrivateKey());
                break;
            case FLAG_MODIFY_PWD: //修改密码
                hideProgress();
                LoggerUtils.i("wallet -bean" + bean);
                IONCWalletSDK.getInstance()
                        .updatePasswordAndKeyStore(bean, newPassword, this);
                break;
        }

    }

    @Override
    public void onCheckWalletPasswordFailure(String errorMsg) {
        LoggerUtils.e(errorMsg);
        ToastUtil.showToastLonger(getAppString(R.string.please_check_password));
        if (mModifyPasswordDialog != null) {
            hideProgress();
        }
        if (passwordCheck != null) {
            passwordCheck.dismiss(); //onCheckWalletPasswordFailure
        }
        if (exportPK != null) {
            exportPK.dismiss(); //onCheckWalletPasswordFailure
        }
        if (exportKS != null) {
            exportKS.dismiss(); //onCheckWalletPasswordFailure
        }
        if (deleteWallet != null) {
            deleteWallet.dismiss(); //onCheckWalletPasswordFailure
        }
    }

    @Override
    public void onDialogTextMessageBtnClicked(DialogTextMessage dialogTextMessage) {
        switch (flag) {
            case FLAG_EXPORT_PRIVATE:
                //复制
                StringUtils.copy(ModifyWalletActivity.this, privateKey);
                ToastUtil.showToastLonger(getAppString(R.string.copy_done_private));
                break;
            case FLAG_EXPORT_KS:
                StringUtils.copy(ModifyWalletActivity.this, json);
                ToastUtil.showToastLonger(getAppString(R.string.copy_done_ks));
                break;
        }

    }

    @Override
    public void onUpdateWalletSuccess(WalletBeanNew wallet) {
        mWallet = wallet;
        hideProgress();
        ToastUtil.showShortToast(getAppString(R.string.modify_password_success));
    }

    @Override
    public void onUpdateWalletFailure(String error) {
        ToastUtil.showShortToast(getAppString(R.string.modify_password_failure));
        hideProgress();
    }
}
