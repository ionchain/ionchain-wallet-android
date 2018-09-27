package org.ionchain.wallet.mvp.view.activity.wallet;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fast.lib.utils.ToastUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.mvp.callback.OnBalanceCallback;
import org.ionchain.wallet.mvp.callback.OnImportPrivateKeyCallback;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.StringUtils;
import org.ionchain.wallet.widget.DialogImportPrivKey;
import org.ionchain.wallet.widget.DialogPasswordCheck;
import org.ionchain.wallet.widget.IONCTitleBar;

import static org.ionchain.wallet.constant.ConstantParams.REQUEST_MODIFY_WALLET_PWD;
import static org.ionchain.wallet.constant.ConstantParams.SERIALIZABLE_DATA_WALLET_BEAN;
import static org.ionchain.wallet.dao.WalletDaoTools.updateWallet;

/**
 * 修改钱包：钱包名、修改密码、导出私钥
 */
public class ModifyWalletActivity extends AbsBaseActivity implements OnBalanceCallback, OnImportPrivateKeyCallback, View.OnClickListener {


    WalletBean mWallet;

    QMUIDialog dialog;

    private Button delBtn;
    private TextView walletBalanceTv;
    private TextView walletAddressTv;
    private AppCompatEditText walletNameEt;
    private RelativeLayout modifyPwdLayout;
    private RelativeLayout importLayout;

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

        delBtn.setOnClickListener(this);
        modifyPwdLayout.setOnClickListener(this);
        importLayout.setOnClickListener(this);
    }

//    @SuppressLint("HandlerLeak")
//    Handler walletHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            try {
//                ApiConstant.WalletManagerType resulit = ApiConstant.WalletManagerType.codeOf(msg.what);
//                if (null == resulit) return;
//                ResponseModel<String> responseModel = (ResponseModel<String>) msg.obj;
//                switch (resulit) {
//                    case WALLET_BALANCE:
//                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//                            Logger.i("余额已刷新");
//                            if (!StringUtils.isEmpty(ApiWalletManager.getInstance().getMainWallet().getBalance())) {
//                                walletBalanceTv.setText(ApiWalletManager.getInstance().getMainWallet().getBalance());
//                            } else {
//                                walletBalanceTv.setText("0.0000");
//                            }
//                        } else {
//                            ToastUtil.showShortToast("余额刷新失败");
//                        }
//                        break;
//                    case WALLET_EXPORT_PRIVATEKEY:
//                        hideProgress();
//                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//                            String data = responseModel.getData();
//                            mWallet.setPrivateKey(data);
//                            showImportPrivateKeyDialog("");
//                        } else {
//                            ToastUtil.showShortToast("请检查密码是否正确");
//                        }
//                        break;
//                    default:
//                        break;
//
//                }
//            } catch (Throwable e) {
//                Log.e(TAG, "handleMessage", e);
//            }
//        }
//    };


    @Override
    protected void initData() {

        if (!TextUtils.isEmpty(mWallet.getAddress())) {
            walletAddressTv.setText(mWallet.getAddress());
        }

        if (!TextUtils.isEmpty(mWallet.getName())) {
            walletNameEt.setText(mWallet.getName());
        }

        WalletManager.getInstance().getAccountBalance(mWallet, this);

    }

    @Override
    protected void handleIntent() {
        super.handleIntent();
        mWallet = (WalletBean) getIntent().getSerializableExtra(Comm.SERIALIZABLE_DATA);
    }

    @Override
    protected void initView() {
        findViews();
        final IONCTitleBar ioncTitleBar = findViewById(R.id.ionc_title_bar);
        mImmersionBar.titleBar(ioncTitleBar).statusBarDarkFont(true).execute();
        ioncTitleBar.setTitle(mWallet.getName());
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
                if (!StringUtils.isEmpty(pwd) && pwd.equals(pwd1)) {
                    dialog.dismiss();
                    showProgress("正在导出请稍候");
                    WalletManager.getInstance().exportPrivateKey(mWallet.getKeystore(), pwd, ModifyWalletActivity.this);
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
    private void showImportPrivateKeyDialog(String privateKey) {

        new DialogImportPrivKey(this).setPrivateKeyText(privateKey)
                .setCopyBtnClickedListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //复制
                        StringUtils.copy(ModifyWalletActivity.this, mWallet.getPrivateKey());
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
                        Log.i(TAG, "onClick: " + mWallet.getPassword());
                        if (!dialogPasswordCheck.getPasswordEt().getText().toString().equals(mWallet.getPassword())) {
                            ToastUtil.showToastLonger("你输入的密码有误！");
                            return;
                        }
                        if (WalletDaoTools.getAllWallet().size() == 1) {
                            ToastUtil.showToastLonger("不能删除该钱包，您必须至少有一个钱包");
                            return;
                        }

                        WalletManager.getInstance().deleteWallet(mWallet);

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
            case R.id.navigationBack:
                finish();
                break;

            case R.id.copyBtn:
                StringUtils.copy(this, mWallet.getPrivateKey());
                ToastUtil.showShortToast("已复制到剪切板");
                dialog.dismiss();

                break;

            case R.id.delBtn:
                delwallet();

                break;
            case R.id.modifyPwdLayout:
                intent = new Intent(mActivity, ModifyWalletPwdActivity.class);
                intent.putExtra(SERIALIZABLE_DATA_WALLET_BEAN, mWallet);
                startActivityForResult(intent, REQUEST_MODIFY_WALLET_PWD);
                break;
            case R.id.importLayout:
                showEditTextDialog();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult1: " + mWallet.getKeystore());
        if (requestCode == REQUEST_MODIFY_WALLET_PWD && data != null) {
            mWallet = (WalletBean) data.getSerializableExtra(SERIALIZABLE_DATA_WALLET_BEAN);
        }
        Log.i(TAG, "onActivityResult2: " + mWallet.getKeystore());
    }
}
