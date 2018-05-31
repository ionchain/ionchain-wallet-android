package org.ionchain.wallet.ui.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.ui.MainActivity;
import org.ionchain.wallet.ui.comm.BaseActivity;

import static org.ionchain.wallet.comm.api.constant.ApiConstant.WalletManagerType.MANAGER_INIT;

public class CreateWalletActivity extends BaseActivity {


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
                    case MANAGER_INIT:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            //创建钱包
//                            ApiWalletManager.getInstance().getMyWallet().setPassword("123123123xxxxxx");
//                            ApiWalletManager.getInstance().createWallet(walletHandler);
//                            ApiWalletManager.printtest("OKOKOKOKOKOKOKO");
                            //导入钱包
//                            ApiWalletManager.getInstance().getMyWallet().setPrivateKey("b71c1f7594fd8718c7ae06ea89c48e2e621990e5736c6f1087f04defa5c251a6");
//                            ApiWalletManager.getInstance().getMyWallet().setPassword("123456");
//                            ApiWalletManager.getInstance().importWallet(walletHandler);
                            //修改密码
                            ApiWalletManager.getInstance().getMyWallet().setKeystore("/storage/emulated/0/ionchain/wallet/UTC--2018-05-31T18-19-51.779--dc39f3895c38f5999ba462fc10dfb1f78bdfecf2.json");
                            ApiWalletManager.getInstance().getMyWallet().setPrivateKey("b71c1f7594fd8718c7ae06ea89c48e2e621990e5736c6f1087f04defa5c251a6");
                            ApiWalletManager.getInstance().getMyWallet().setPassword("1234567");
                            ApiWalletManager.getInstance().editPassWord("12345678",walletHandler);
                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMyWallet().getKeystore());

                        } else {
                            ApiWalletManager.printtest("ERRRRRR");
                        }
                        break;
                    case WALLET_CREATE:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMyWallet().getKeystore());
                            //取余额度
                            ApiWalletManager.getInstance().getMyWallet().setAddress("0xc21e95a78a224da9c69354de4470012aba1f1711");
                            ApiWalletManager.getInstance().reLoadBlance(walletHandler);

                        } else {
                            ApiWalletManager.printtest("ERRRRRR");
                        }
                        break;
                    case WALLET_BALANCE:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMyWallet().getBalance());
                        } else {
                            ApiWalletManager.printtest("ERRRRRR");
                        }
                        break;
                    case WALLET_IMPORT:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMyWallet().getPublickey());
                        } else {
                            ApiWalletManager.printtest("ERRRRRR");
                        }
                        break;
                    case WALLET_EDIT_PASS:
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMyWallet().getKeystore());
                        } else {
                            ApiWalletManager.printtest("ERRRRRR");
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
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.createBtn:
                    Intent intent = new Intent( this, MainActivity.class );
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

//                    Wallet wallet = new Wallet();
//                    wallet.setName("test");
//                    wallet.setPassword("1234567899xxxxxx");
//                    ApiWalletManager.getInstance(wallet, this.getApplicationContext()).init(walletHandler);

                    break;
                case R.id.importBtn:

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

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_wallet_create);

    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.createBtn);
        setOnClickListener(R.id.importBtn);

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.drawable.qmui_icon_topbar_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_create_wallet;
    }
}
