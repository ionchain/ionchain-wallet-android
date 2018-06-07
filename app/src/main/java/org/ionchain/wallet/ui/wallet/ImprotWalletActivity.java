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
import android.widget.TextView;
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
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.comm.ScanActivity;
import org.ionchain.wallet.ui.comm.WebViewActivity;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ImprotWalletActivity extends BaseActivity implements TextWatcher {

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    private Wallet nowWallet;
    private boolean isAddMode = false;

    @BindView(R.id.contentEt)
    AppCompatEditText contentEt;

    @BindView(R.id.pwdEt)
    AppCompatEditText pwdEt;


    @BindView(R.id.repwdEt)
    AppCompatEditText repwdEt;

    @BindView(R.id.importBtn)
    Button importBtn;

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

                    case WALLET_IMPORT:
                        dismissProgressDialog();
                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                            long id = saveWallet();
                            ApiWalletManager.printtest(id + "");
                            Toast.makeText(ImprotWalletActivity.this.getApplicationContext(), "钱包已经导入", Toast.LENGTH_SHORT).show();
                            //初始化用户跳转主页面
                            if (!isAddMode) startMain();

                        } else {
                            Toast.makeText(ImprotWalletActivity.this.getApplicationContext(), "钱包导入失败", Toast.LENGTH_SHORT).show();
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

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try {

            switch (what) {
                case R.id.navigationBack:
                    finish();
                    break;
                case R.id.scanBtn:
                    requestCodeQRCodePermissions();
                    break;
                case R.id.importBtn:
                    importWallet();

                    break;
                case R.id.linkUrlTv:
                    transfer(WebViewActivity.class, Comm.SERIALIZABLE_DATA, "https://www.baidu.com", Comm.SERIALIZABLE_DATA1, "条款");
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
        setContentView(R.layout.activity_import_wallet);

    }

    @Override
    protected void setListener() {

        setOnClickListener(R.id.importBtn);
        setOnClickListener(R.id.linkUrlTv);

        contentEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        repwdEt.addTextChangedListener(this);


    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 999) {
            String reslut = data.getStringExtra("result");
            contentEt.setText(reslut);

        }
    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_import_wallet;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.mipmap.ic_arrow_back;
    }

    @Override
    public int getActivityTitleContent() {
        return R.string.activity_import_wallet;
    }


    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.VIBRATE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            transfer(ScanActivity.class, 999);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String contentstr = contentEt.getText().toString().trim();
        String pwdstr = pwdEt.getText().toString().trim();
        String resetpwdstr = repwdEt.getText().toString().trim();

        if (!TextUtils.isEmpty(contentstr) && !TextUtils.isEmpty(pwdstr) && !TextUtils.isEmpty(resetpwdstr)) {
            importBtn.setEnabled(true);
        } else {
            importBtn.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void importWallet() {
        try {
            String content = contentEt.getText().toString().trim();
            String resetpass = repwdEt.getText().toString().trim();
            String pass = pwdEt.getText().toString().trim();

            if (!resetpass.equals(pass)) {
                Toast.makeText(this.getApplicationContext(), "密码和重复密码必须相同", Toast.LENGTH_SHORT).show();
                return;
            }
            Wallet extisWallet = WalletDaoTools.getWalletByPrivateKey(content);
            if (null != extisWallet) {
                Toast.makeText(this.getApplicationContext(), "该私钥已经导入 钱包名称：" + extisWallet.getName(), Toast.LENGTH_SHORT).show();
                return;
            }


            Integer nameInex = (Integer) LibSPUtils.get(this.getApplicationContext(), Comm.LOCAL_SAVE_WALLET_INDEX, 1);
            String walletname = "新增钱包" + nameInex;
            nowWallet = new Wallet();
            nowWallet.setName(walletname);
            nowWallet.setPassword(pass);
            nowWallet.setPrivateKey(content);
            ApiWalletManager.getInstance().importWallet(nowWallet, walletHandler);
            Log.e("wallet", "" + walletname + " " + resetpass + " " + pass);
            showProgressDialog("正在导入钱包请稍候");
        } catch (Exception e) {
            Log.e("wallet", "errr", e);
        }

    }

    private long saveWallet(){
        //保存钱包到本地数据库
        long id = WalletDaoTools.saveWallet(nowWallet);
        Integer index = (Integer) LibSPUtils.get(ImprotWalletActivity.this.getApplicationContext(),Comm.LOCAL_SAVE_WALLET_INDEX,1);
        LibSPUtils.put(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_WALLET_INDEX,index+1);
        //首次创建模式修改当前其钱包的信息
        if( id>0 && !isAddMode){
            ApiWalletManager.getInstance().setMyWallet(nowWallet);

        }
        return id;
    }


    public void startMain() {
        //第一次导入的 钱包跳主页面
        if (TextUtils.isEmpty((String) LibSPUtils.get(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULL))) {
            LibSPUtils.put(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, ApiWalletManager.getInstance().getMyWallet().getName());
            Intent intent = new Intent(ImprotWalletActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
    }
}
