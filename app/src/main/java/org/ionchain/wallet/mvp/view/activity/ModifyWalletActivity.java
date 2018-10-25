package org.ionchain.wallet.mvp.view.activity;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.myweb3j.Web3jHelper;
import org.ionchain.wallet.mvp.callback.OnBalanceCallback;
import org.ionchain.wallet.mvp.callback.OnImportPrivateKeyCallback;
import org.ionchain.wallet.mvp.callback.OnSimulateTimeConsume;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.Md5Utils;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.StringUtils;
import org.ionchain.wallet.utils.ToastUtil;
import org.ionchain.wallet.widget.DialogImportPrivKey;
import org.ionchain.wallet.widget.DialogPasswordCheck;
import org.ionchain.wallet.widget.IONCTitleBar;
import org.ionchain.wallet.widget.ImportMnemonicDialog;

import static org.ionchain.wallet.constant.ConstantParams.REQUEST_MODIFY_WALLET_PWD;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA_WALLET_BEAN;
import static org.ionchain.wallet.dao.WalletDaoTools.updateWallet;

/**
 * 修改钱包：钱包名、修改密码、导出私钥
 */
public class ModifyWalletActivity extends AbsBaseActivity implements OnBalanceCallback, OnImportPrivateKeyCallback, View.OnClickListener, OnSimulateTimeConsume {


    WalletBean mWallet;


    private Button delBtn;
    private TextView walletBalanceTv;
    private TextView walletAddressTv;
    private AppCompatEditText walletNameEt;
    private RelativeLayout modifyPwdLayout;
    private RelativeLayout importLayout;
    private RelativeLayout import_mnemonic_layout;
    private boolean bShowMnemonicRl;//是否显示导出助记词条目

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-26 14:47:05 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        delBtn = (Button) findViewById(R.id.delBtn);
        walletBalanceTv = (TextView) findViewById(R.id.walletBalanceTv);
        walletAddressTv = (TextView) findViewById(R.id.walletAddressTv);
        walletNameEt = (AppCompatEditText) findViewById(R.id.walletNameEt);
        modifyPwdLayout = (RelativeLayout) findViewById(R.id.modifyPwdLayout);
        importLayout = (RelativeLayout) findViewById(R.id.importLayout);
        import_mnemonic_layout = (RelativeLayout) findViewById(R.id.import_mnemonic_layout);
        if (!bShowMnemonicRl) {
            import_mnemonic_layout.setVisibility(View.GONE);
        }
        import_mnemonic_layout.setOnClickListener(this);
        delBtn.setOnClickListener(this);
        modifyPwdLayout.setOnClickListener(this);
        importLayout.setOnClickListener(this);
    }

    @Override
    protected void initData() {

        if (!TextUtils.isEmpty(mWallet.getAddress())) {
            walletAddressTv.setText(mWallet.getAddress());
        }

        if (!TextUtils.isEmpty(mWallet.getName())) {
            walletNameEt.setText(mWallet.getName());
        }

        Web3jHelper.getInstance().getAccountBalance(mWallet, this);

    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        mWallet = (WalletBean) intent.getSerializableExtra(SERIALIZABLE_DATA);
        bShowMnemonicRl = !StringUtils.isEmpty(mWallet.getMnemonic());
    }

    @Override
    protected void initView() {
        findViews();
        final IONCTitleBar ioncTitleBar = findViewById(R.id.ionc_title_bar);
        getMImmersionBar().titleBar(ioncTitleBar).statusBarDarkFont(true).execute();
        ioncTitleBar.setTitle(mWallet.getName());
        ioncTitleBar.setLeftImgRes(R.mipmap.arrow_back_white);
        ioncTitleBar.setLeftBtnCLickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftKeyboardUtil.hideSoftKeyboard(ModifyWalletActivity.this);
                finish();
            }
        });
        ioncTitleBar.setRightTextCLickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(walletNameEt.getText().toString())) {
                    mWallet.setName(walletNameEt.getText().toString());
                    ioncTitleBar.setTitle(walletNameEt.getText().toString());
                    SoftKeyboardUtil.hideSoftKeyboard(ModifyWalletActivity.this);
                    updateWallet(mWallet);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_modify;
    }


    /**
     * 输入密码
     */
    private void showEditTextDialog() {
        final DialogPasswordCheck dialog = new DialogPasswordCheck(this);
        dialog.setLeftBtnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setRightBtnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*比对密码是否正确*/
                String pwd = mWallet.getPassword();
                String pwd1 = dialog.getPasswordEt().getText().toString();
                if (!StringUtils.isEmpty(pwd) && pwd.equals(Md5Utils.md5(pwd1))) {
                    dialog.dismiss();
                    showProgress("正在导出请稍候");
                    Web3jHelper.getInstance().exportPrivateKey(mWallet.getKeystore(), pwd, ModifyWalletActivity.this);
                } else {
                    ToastUtil.showToastLonger("请检查密码是否正确！");
                }
            }
        });
        dialog.show();
    }

    /**
     * 导出私钥
     *
     * @param privateKey
     */
    private void showImportPrivateKeyDialog(final String privateKey) {

        new DialogImportPrivKey(this).setPrivateKeyText(privateKey)
                .setCopyBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //复制
                        StringUtils.copy(ModifyWalletActivity.this, privateKey);
                        ToastUtil.showToastLonger("已复制私钥");
                    }
                }).show();

    }

    /**
     * 删钱包
     */
    private void delwallet() {

        final DialogPasswordCheck dialogPasswordCheck = new DialogPasswordCheck(this);
        dialogPasswordCheck.setLeftBtnText("取消")
                .setRightBtnText("确定")
                .setTitleText("请输入该钱包的密码")
                .setLeftBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPasswordCheck.dismiss();
                    }
                })
                .setRightBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (StringUtils.isEmpty(dialogPasswordCheck.getPasswordEt().getText().toString())) {
                            ToastUtil.showToastLonger("请输入密码！");
                            return;
                        }
                        Log.i(getTAG(), "onClick: " + mWallet.getPassword());
                        if (!dialogPasswordCheck.getPasswordEt().getText().toString().equals(mWallet.getPassword())) {
                            ToastUtil.showToastLonger("你输入的密码有误！");
                            return;
                        }
                        if (WalletDaoTools.getAllWallet().size() == 1) {
                            ToastUtil.showToastLonger("不能删除该钱包，您必须至少有一个钱包");
                            return;
                        }

                        Web3jHelper.getInstance().deleteWallet(mWallet);

                        finish();
                    }
                }).show();

    }

    @Override
    public void onBalanceSuccess(WalletBean walletBean) {
        walletBalanceTv.setText(walletBean.getBalance());
    }

    @Override
    public void onBalanceFailure(String error) {
        walletBalanceTv.setText("0.0000");
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
        Intent intent;
        switch (v.getId()) {


            case R.id.copyBtn:
                StringUtils.copy(this, mWallet.getPrivateKey());
                ToastUtil.showShortToast("已复制到剪切板");

                break;

            case R.id.delBtn:
                delwallet();

                break;
            case R.id.modifyPwdLayout:
                intent = new Intent(getMActivity(), ModifyWalletPwdActivity.class);
                intent.putExtra(SERIALIZABLE_DATA_WALLET_BEAN, mWallet);
                startActivityForResult(intent, REQUEST_MODIFY_WALLET_PWD);
                break;
            case R.id.importLayout:
                showEditTextDialog();
                break;
            case R.id.import_mnemonic_layout:
                final DialogPasswordCheck dialog = new DialogPasswordCheck(this);
                dialog.setLeftBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setRightBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*比对密码是否正确*/
                        String pwd = mWallet.getPassword();
                        String pwd1 = dialog.getPasswordEt().getText().toString();
                        if (!StringUtils.isEmpty(pwd) && pwd.equals(Md5Utils.md5(pwd1))) {
                            dialog.dismiss();
                            showProgress("正在导出请稍候");
                            Web3jHelper.simulateTimeConsuming(ModifyWalletActivity.this);
                        } else {
                            ToastUtil.showToastLonger("请检查密码是否正确！");
                        }
                    }
                });
                dialog.show();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(getTAG(), "onActivityResult1: " + mWallet.getKeystore());
        if (requestCode == REQUEST_MODIFY_WALLET_PWD && data != null) {
            mWallet = (WalletBean) data.getSerializableExtra(SERIALIZABLE_DATA_WALLET_BEAN);
        }
        Log.i(getTAG(), "onActivityResult2: " + mWallet.getKeystore());
    }

    @Override
    public void onSimulateFinish() {
        hideProgress();
        ImportMnemonicDialog dialog = new ImportMnemonicDialog(this).setMnemonic(mWallet.getMnemonic());
        dialog.show();
    }
}
