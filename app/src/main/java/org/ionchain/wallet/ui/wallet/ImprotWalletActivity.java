package org.ionchain.wallet.ui.wallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnCreateWalletCallback;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.comm.ScanActivity;
import org.ionchain.wallet.ui.comm.WebViewActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;
import org.ionchain.wallet.utils.StringUtils;

import java.io.File;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static org.ionchain.wallet.utils.RandomUntil.getNum;

public class ImprotWalletActivity extends BaseActivity implements TextWatcher, OnCreateWalletCallback {

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_IMPORT_PERMISSIONS = 2;

    private WalletBean nowWallet;
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
                            //一个主钱包的 都没有的情况 添加导入钱包 第一个都做为默认主钱包
                            String nowWalletName = (String) LibSPUtils.get(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULL);
                            if (nowWalletName.equals(Comm.NULLWALLET)) {
                                ApiWalletManager.getInstance().setMainWallet(nowWallet);
                                LibSPUtils.put(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, nowWallet.getName());
                            }
                            if (!isAddMode) {
                                startMain();
                            } else {
                                finish();
                                SoftKeyboardUtil.hideSoftKeyboard(ImprotWalletActivity.this);
                            }
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
        Intent intent = getIntent();
        isAddMode = intent.getBooleanExtra(Comm.JUMP_PARM_ISADDMODE, false);
        ApiWalletManager.printtest(isAddMode + "");

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
                    requestCodeImprotPermissions();
                    break;
                case R.id.linkUrlTv:
                    transfer(WebViewActivity.class, Comm.SERIALIZABLE_DATA, Comm.URL_AGREE, Comm.SERIALIZABLE_DATA1, "条款");
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
        mImmersionBar.titleBar(getViewById(R.id.toolbarlayout))
                .statusBarDarkFont(true)
                .init();
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
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 999) {
            String result = data.getStringExtra("result");
            contentEt.setText(result);
        }
    }

    @Override
    public int getActivityMenuRes() {
        return R.menu.menu_import_wallet;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return R.mipmap.arrow_back_blue;
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

    @AfterPermissionGranted(REQUEST_CODE_IMPORT_PERMISSIONS)
    private void requestCodeImprotPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "导入钱包需要的权限", REQUEST_CODE_IMPORT_PERMISSIONS, perms);
        } else {
            importWallet();
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
            //创建默认目录
            File file = new File(ApiWalletManager.DEF_WALLET_PATH);
            if (!file.exists()) {
                boolean crate = file.mkdirs();
            }
            String content = contentEt.getText().toString().trim();
            String resetpass = repwdEt.getText().toString().trim();
            String pass = pwdEt.getText().toString().trim();
            if (StringUtils.isEmpty(content)) {
                ToastUtil.showToastLonger("私钥为空！");
                return;
            }
            if (content.length() != 64) {
                ToastUtil.showToastLonger("无效私钥！");
                return;
            }
            WalletBean wallet = WalletDaoTools.getWalletByPrivateKey(content);
            if (null != wallet) {
                Toast.makeText(this, "该钱包已存在,钱包名 : " + wallet.getName(), Toast.LENGTH_LONG).show();
                return;
            }
            if (!resetpass.equals(pass)) {
                Toast.makeText(this.getApplicationContext(), "密码和重复密码必须相同", Toast.LENGTH_SHORT).show();
                return;
            }


//            nowWallet = new WalletBean();
//            nowWallet.setName(walletname);
//            nowWallet.setPassword(pass);
//            nowWallet.setPrivateKey(content.toLowerCase());
//            nowWallet.setIconIdex(getNum(7));
//            ApiWalletManager.getInstance().importWallet(nowWallet, walletHandler);
            WalletManager.getInstance().importPrivateKey(content, pass, this);
//            Log.e("wallet", "" + walletname + " " + resetpass + " " + pass);
            showProgressDialog("正在导入钱包请稍候");
        } catch (Exception e) {
            Log.e("wallet", "errr", e);
        }

    }

    private long saveWallet() {
        //保存钱包到本地数据库
        long id = WalletDaoTools.saveWallet(nowWallet);
        Integer index = (Integer) LibSPUtils.get(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_WALLET_INDEX, 1);
        LibSPUtils.put(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_WALLET_INDEX, index + 1);
        //首次创建模式修改当前其钱包的信息
        if (id > 0 && !isAddMode) {
            ApiWalletManager.getInstance().setMainWallet(nowWallet);

        }
        return id;
    }


    public void startMain() {
        //第一次导入的 钱包跳主页面
        String res = (String) LibSPUtils.get(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULL);
        if (TextUtils.isEmpty(res)) {
            LibSPUtils.put(ImprotWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, ApiWalletManager.getInstance().getMainWallet().getName());
            Intent intent = new Intent(ImprotWalletActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onCreateSuccess(WalletBean walletBean) {
        walletBean.setIconIdex(getNum(7));
        WalletDaoTools.saveWallet(walletBean);
        dismissProgressDialog();
        ToastUtil.showToastLonger("导入成功!");
    }

    @Override
    public void onCreateFailure(String result) {
        Log.i(TAG, "onCreateFailure: " + result);
        dismissProgressDialog();
        ToastUtil.showToastLonger("导入成失败");
    }
}
