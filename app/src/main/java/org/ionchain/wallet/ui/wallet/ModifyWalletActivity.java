package org.ionchain.wallet.ui.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fast.lib.event.CommonEvent;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.LibSPUtils;
import com.fast.lib.utils.ToastUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnBalanceCallback;
import org.ionchain.wallet.callback.OnImportPrivateKeyCallback;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.StringUtils;
import org.ionchain.wallet.widget.DialogImportPrivKey;
import org.ionchain.wallet.widget.DialogPasswordCheck;
import org.ionchain.wallet.widget.IONCTitleBar;

import java.io.File;

import butterknife.BindView;

import static org.ionchain.wallet.dao.WalletDaoTools.updateWallet;

/**
 * 修改钱包：钱包名、修改密码、导出私钥
 */
public class ModifyWalletActivity extends BaseActivity implements OnBalanceCallback, OnImportPrivateKeyCallback {


    final int REQUEST_MODIFY_WALLET_PWD = 100;

    @BindView(R.id.walletAddressTv)
    TextView walletAddressTv;

    @BindView(R.id.walletBalanceTv)
    TextView walletBalanceTv;

    @BindView(R.id.walletNameEt)
    AppCompatEditText walletNameEt;

    WalletBean mWallet;

    QMUIDialog dialog;


    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {
                case R.id.navigationBack:
                    finish();
                    break;
                case Comm.modify_wallet_refresh_type:
                    CommonEvent event = (CommonEvent) obj;

                    if (event.getData() == null)
                        return;

                    WalletBean wallet = (WalletBean) event.getData();

                    mWallet = wallet;

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
                    transfer(ModifyWalletPwdActivity.class, Comm.SERIALIZABLE_DATA, mWallet);
                    break;
                case R.id.importLayout:
                    showEditTextDialog();
                    break;

                case 0:
                    dismissProgressDialog();
                    if (obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel) obj;
                    if (!verifyStatus(responseModel)) {
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        } catch (Throwable e) {
            Logger.e(e, TAG);
        }
    }


    @SuppressLint("HandlerLeak")
    Handler walletHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                ApiConstant.WalletManagerType resulit = ApiConstant.WalletManagerType.codeOf(msg.what);
                if (null == resulit) return;
                ResponseModel<String> responseModel = (ResponseModel<String>) msg.obj;
                switch (resulit) {
                    case WALLET_BALANCE:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            Logger.i("余额已刷新");
                            if (!StringUtils.isEmpty(ApiWalletManager.getInstance().getMainWallet().getBalance())) {
                                walletBalanceTv.setText(ApiWalletManager.getInstance().getMainWallet().getBalance());
                            } else {
                                walletBalanceTv.setText("0.0000");
                            }
                        } else {
                            ToastUtil.showShortToast("余额刷新失败");
                        }
                        break;
                    case WALLET_EXPORT_PRIVATEKEY:
                        dismissProgressDialog();
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            String data = responseModel.getData();
                            mWallet.setPrivateKey(data);
                            showImportPrivateKeyDialog("");
                        } else {
                            ToastUtil.showShortToast("请检查密码是否正确");
                        }
                        break;
                    default:
                        break;

                }
            } catch (Throwable e) {
                Log.e(TAG, "handleMessage", e);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setSystemBar(false);
        super.onCreate(savedInstanceState);
        final IONCTitleBar ioncTitleBar = findViewById(R.id.ionc_title_bar);
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
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wallet_modify);
        getViewById(R.id.importLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialog();
            }
        });
        mWallet = (WalletBean) getIntent().getSerializableExtra(Comm.SERIALIZABLE_DATA);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.delBtn);
        setOnClickListener(R.id.modifyPwdLayout);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        if (!TextUtils.isEmpty(mWallet.getAddress())) {
            walletAddressTv.setText(mWallet.getAddress());
        }

        if (!TextUtils.isEmpty(mWallet.getName())) {
            walletNameEt.setText(mWallet.getName());
        }

        WalletManager.getInstance().getAccountBalance(mWallet, this);

    }


    /**
     * 输入密码
     */
    private void showEditTextDialog() {
        ToastUtil.showShortToast("dddddd");
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
                    showProgressDialog("正在导出请稍候");
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
                        String nowWalletName = (String) LibSPUtils.get(ModifyWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULL);

                        File file = new File(mWallet.getKeystore());
                        if (file.exists()) {
                            file.delete();
                        }
                        WalletDaoTools.deleteWallet(mWallet.getId());
                        if (nowWalletName.equals(mWallet.getName())) {
                            WalletBean topWallet = WalletDaoTools.getWalletTop();
                            if (null == topWallet) {
                                LibSPUtils.put(ModifyWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULLWALLET);
                                WalletBean nullWallet = new WalletBean();
                                nullWallet.setName(Comm.NULLWALLETNAME);
                                nullWallet.setAddress(Comm.NULLWALLETNAME);
                                ApiWalletManager.getInstance().setMainWallet(nullWallet);
                            } else {
                                LibSPUtils.put(ModifyWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, topWallet.getName());
                                ApiWalletManager.getInstance().setMainWallet(topWallet);
                            }
                        }

                        //删除的是主钱包
                        finish();
                    }
                }).show();

    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_wallet_modify;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.drawable.qmui_icon_topbar_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_modify_wallet;
    }


    @Override
    protected boolean isRegisterEventBus() {
        return true;
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
        dismissProgressDialog();
    }

    @Override
    public void onImportPriKeyFailure(String erroe) {
        dismissProgressDialog();
    }
}
