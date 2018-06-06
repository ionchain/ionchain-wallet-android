package org.ionchain.wallet.ui.wallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.LibSPUtils;
import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.db.WalletDaoTools;
import org.ionchain.wallet.ui.MainActivity;
import org.ionchain.wallet.ui.account.WalletManageActivity;
import org.ionchain.wallet.ui.comm.BaseActivity;

import butterknife.BindView;

public class CreateWalletActivity extends BaseActivity implements TextWatcher {

    //判定是否需要刷新
    private boolean isAddMode = false;
    private Wallet nowWallet = null;
    @BindView(R.id.walletNameEt)
    AppCompatEditText walletNameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        isAddMode=intent.getBooleanExtra(Comm.JUMP_PARM_ISADDMODE,false);
        ApiWalletManager.printtest(isAddMode+"");
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @BindView(R.id.pwdEt)
    AppCompatEditText pwdEt;

    @BindView(R.id.resetPwdEt)
    AppCompatEditText resetPwdEt;

    @BindView(R.id.createBtn)
    Button createBtn;

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
//                            ApiWalletManager.getInstance().getMyWallet().setKeystore("/storage/emulated/0/ionchain/wallet/UTC--2018-05-31T18-19-51.779--dc39f3895c38f5999ba462fc10dfb1f78bdfecf2.json");
//                            ApiWalletManager.getInstance().getMyWallet().setPrivateKey("b71c1f7594fd8718c7ae06ea89c48e2e621990e5736c6f1087f04defa5c251a6");
//                            ApiWalletManager.getInstance().getMyWallet().setPassword("1234567");
//                            ApiWalletManager.getInstance().editPassWord("12345678", walletHandler);
//                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMyWallet().getKeystore());

                        } else {
                            Toast.makeText(CreateWalletActivity.this.getApplicationContext(), "钱包创建失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case WALLET_CREATE:
                        dismissProgressDialog();
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            long id = saveWallet();
                            ApiWalletManager.printtest(id+"");
                            Toast.makeText(CreateWalletActivity.this.getApplicationContext(), "钱包创建成功", Toast.LENGTH_SHORT).show();
                            //初始化用户跳转主页面
                            if(!isAddMode) startMain();


                        } else {
                            Toast.makeText(CreateWalletActivity.this.getApplicationContext(), "钱包创建失败", Toast.LENGTH_SHORT).show();
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
            Intent  intent = null;
            switch (what) {
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.createBtn:
                    Log.e("wallet", "xxxxxxx");
                    cerateWallet();
//                    Wallet wallet = new Wallet();
//                    wallet.setName("test");
//                    wallet.setPassword("1234567899xxxxxx");
//                    ApiWalletManager.getInstance(wallet, this.getApplicationContext()).init(walletHandler);

                    break;
                case R.id.importBtn:

                    intent = new Intent();
                    intent.putExtra(Comm.JUMP_PARM_ISADDMODE, isAddMode);
                    intent.setClass(CreateWalletActivity.this, ImprotWalletActivity.class);//从哪里跳到哪里
                    CreateWalletActivity.this.startActivity(intent);

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
        walletNameEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        resetPwdEt.addTextChangedListener(this);

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
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_create_wallet;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String walletnamestr = walletNameEt.getText().toString().trim();
        String pwdstr = pwdEt.getText().toString().trim();
        String resetpwdstr = resetPwdEt.getText().toString().trim();

        if (!TextUtils.isEmpty(walletnamestr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)) {
            createBtn.setEnabled(true);
        } else {
            createBtn.setEnabled(false);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void startMain() {
        Intent intent = new Intent(CreateWalletActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void cerateWallet() {
        try {
            String walletname = walletNameEt.getText().toString().trim();
            String resetpass = resetPwdEt.getText().toString().trim();
            String pass = pwdEt.getText().toString().trim();

            if (!resetpass.equals(pass)) {
                Toast.makeText(this.getApplicationContext(), "密码和重复密码必须相同", Toast.LENGTH_SHORT).show();
                return;
            }
            if (null != WalletDaoTools.getWalletByName(walletname)) {
                Toast.makeText(this.getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                return;
            }
            showProgressDialog("正在创建钱包请稍候");

            nowWallet = new Wallet();
            nowWallet.setName(walletname);
            nowWallet.setPassword(pass);
            ApiWalletManager.getInstance().createWallet(nowWallet,walletHandler);

            Log.e("wallet", "" + walletname + " " + resetpass + " " + pass);
        } catch (Exception e) {
            Log.e("wallet", "errr", e);
        }

    }

    private long saveWallet(){
        //保存钱包到本地数据库
       long id = WalletDaoTools.saveWallet(nowWallet);
       //首次创建模式修改当前其钱包的信息
       if( id>0 && !isAddMode){
           ApiWalletManager.getInstance().setMyWallet(nowWallet);
           LibSPUtils.put(CreateWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME,nowWallet.getName());
       }
       return id;
    }

}
