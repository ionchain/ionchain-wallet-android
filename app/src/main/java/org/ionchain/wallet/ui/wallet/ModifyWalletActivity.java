package org.ionchain.wallet.ui.wallet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.fast.lib.event.CommonEvent;
import com.fast.lib.immersionbar.ImmersionBar;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.utils.StringUtils;
import org.ionchain.wallet.ui.comm.BaseActivity;

import butterknife.BindView;

public class ModifyWalletActivity extends BaseActivity {


    final int REQUEST_MODIFY_WALLET_PWD = 100;

    @BindView(R.id.walletAddressTv)
    TextView walletAddressTv;

    @BindView(R.id.walletBalanceTv)
            TextView walletBalanceTv;

    @BindView(R.id.walletNameEt)
    AppCompatEditText walletNameEt;

    Wallet mWallet;

    QMUIDialog dialog;


    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:
                    finish();
                    break;
                case Comm.modify_wallet_refresh_type:
                    CommonEvent event = (CommonEvent)obj;

                    if(event.getData() == null)
                        return;

                    Wallet wallet = (Wallet)event.getData();

                    mWallet = wallet;

                    break;
                case R.id.copyBtn:
                    StringUtils.copy(this,mWallet.getPrivateKey());
                    ToastUtil.showShortToast("已复制到剪切板");
                    dialog.dismiss();

                    break;
                case R.id.saveBtn:
                    break;
                case R.id.delBtn:
                    finish();
                    break;
                case R.id.modifyPwdLayout:
                    transfer(ModifyWalletPwdActivity.class,Comm.SERIALIZABLE_DATA,mWallet);
                    break;
                case R.id.importLayout:
                    showEditTextDialog();
                    break;

                case 0:
                    dismissProgressDialog();
                    if(obj == null)
                        return;

                    ResponseModel<String> responseModel = (ResponseModel)obj;
                    if(!verifyStatus(responseModel)){
                        ToastUtil.showShortToast(responseModel.getMsg());
                        return;
                    }


                    break;
            }

        }catch (Throwable e){
            Logger.e(e,TAG);
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
                            Logger.i("余额度已刷新");
                            walletBalanceTv.setText(ApiWalletManager.getInstance().getMyWallet().getBalance());
                        } else {
                            ToastUtil.showShortToast("余额度刷新失败");
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
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .transparentStatusBar()
                .statusBarView(R.id.statusView)
                .navigationBarColor(R.color.black,0.5f)
                .fitsSystemWindows(false)
                .init();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wallet_modify);
        mWallet = (Wallet) getIntent().getSerializableExtra(Comm.SERIALIZABLE_DATA);

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.delBtn);
        setOnClickListener(R.id.modifyPwdLayout);
        setOnClickListener(R.id.importLayout);

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

        if(!TextUtils.isEmpty(mWallet.getAddress())){
            walletAddressTv.setText(mWallet.getAddress());
        }

        if(!TextUtils.isEmpty(mWallet.getName())){
            walletNameEt.setText(mWallet.getName());
        }

        ApiWalletManager.getInstance().reLoadBlance(mWallet,walletHandler);

    }


    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("输入密码")
                .setPlaceholder("在此输入密码")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {

                            if(!text.toString().equals(mWallet.getPassword())){
                                ToastUtil.showShortToast("密码错误");
                            }else{
                                showImportPrivateKeyDialog();
                                dialog.dismiss();
                            }

                        } else {
                            ToastUtil.showShortToast("请输入密码");
                        }
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    private void showImportPrivateKeyDialog(){
        QMUIDialog.CustomDialogBuilder dialogBuilder = new QMUIDialog.CustomDialogBuilder(this);
        dialogBuilder.setLayout(R.layout.layout_import_private_key);
        dialog = dialogBuilder.create();

        TextView preivateKeyTv = dialog.findViewById(R.id.preivateKeyTv);
        if(!TextUtils.isEmpty(mWallet.getPrivateKey())){
            preivateKeyTv.setText(mWallet.getPrivateKey());
        }else {
            preivateKeyTv.setText("");
        }


        Button copyBtn = dialog.findViewById(R.id.copyBtn);
        copyBtn.setOnClickListener(this);

        dialog.show();

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
}
