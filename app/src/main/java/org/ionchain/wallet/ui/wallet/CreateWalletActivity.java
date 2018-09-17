package org.ionchain.wallet.ui.wallet;

import android.Manifest;
import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fast.lib.utils.ToastUtil;

import org.ionchain.wallet.R;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.callback.OnCreateWalletCallback;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.ui.base.AbsBaseActivity;
import org.ionchain.wallet.ui.comm.WebViewActivity;
import org.ionchain.wallet.utils.SoftKeyboardUtil;

import java.io.File;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static org.ionchain.wallet.utils.RandomUntil.getNum;

public class CreateWalletActivity extends AbsBaseActivity implements TextWatcher, OnCreateWalletCallback {

    final int REQUEST_CODE_CREATE_PERMISSIONS = 1;
    //判定是否需要刷新
    private boolean isAddMode = false;
    private WalletBean mCreateWallet = null;


    private RelativeLayout toolbarlayout;
    private ImageView back;
    private AppCompatEditText walletNameEt;
    private AppCompatEditText pwdEt;
    private AppCompatEditText resetPwdEt;
    private CheckBox checkbox;
    private TextView linkUrlTv;
    private Button createBtn;
    private TextView importBtn;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-17 19:48:43 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        toolbarlayout = (RelativeLayout) findViewById(R.id.toolbarlayout);
        back = (ImageView) findViewById(R.id.back);
        walletNameEt = (AppCompatEditText) findViewById(R.id.walletNameEt);
        pwdEt = (AppCompatEditText) findViewById(R.id.pwdEt);
        resetPwdEt = (AppCompatEditText) findViewById(R.id.resetPwdEt);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        linkUrlTv = (TextView) findViewById(R.id.linkUrlTv);
        createBtn = (Button) findViewById(R.id.createBtn);
        importBtn = (TextView) findViewById(R.id.importBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCodeCreatePermissions();
            }
        });
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(ImportWalletActivity.class, Comm.JUMP_PARM_ISADDMODE, isAddMode);
            }
        });
        linkUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip(WebViewActivity.class, Comm.SERIALIZABLE_DATA, Comm.URL_AGREE, Comm.SERIALIZABLE_DATA1, "条款");
            }
        });
        walletNameEt.addTextChangedListener(this);
        pwdEt.addTextChangedListener(this);
        resetPwdEt.addTextChangedListener(this);
    }

//
//    @SuppressLint("HandlerLeak")
//    Handler walletHandler = new Handler() {
//        /**
//         * @param msg
//         */
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            try {
//                ApiConstant.WalletManagerType resulit = ApiConstant.WalletManagerType.codeOf(msg.what);
//                if (null == resulit) return;
//                ResponseModel<String> responseModel = (ResponseModel<String>) msg.obj;
//                switch (resulit) {
//                    case MANAGER_INIT:
//                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//
//                            //创建钱包
////                            ApiWalletManager.getInstance().getMainWallet().setPassword("123123123xxxxxx");
////                            ApiWalletManager.getInstance().createWallet(walletHandler);
////                            ApiWalletManager.printtest("OKOKOKOKOKOKOKO");
//                            //导入钱包
////                            ApiWalletManager.getInstance().getMainWallet().setPrivateKey("b71c1f7594fd8718c7ae06ea89c48e2e621990e5736c6f1087f04defa5c251a6");
////                            ApiWalletManager.getInstance().getMainWallet().setPassword("123456");
////                            ApiWalletManager.getInstance().importWallet(walletHandler);
//                            //修改密码
////                            ApiWalletManager.getInstance().getMainWallet().setKeystore("/storage/emulated/0/ionchain/wallet/UTC--2018-05-31T18-19-51.779--dc39f3895c38f5999ba462fc10dfb1f78bdfecf2.json");
////                            ApiWalletManager.getInstance().getMainWallet().setPrivateKey("b71c1f7594fd8718c7ae06ea89c48e2e621990e5736c6f1087f04defa5c251a6");
////                            ApiWalletManager.getInstance().getMainWallet().setPassword("1234567");
////                            ApiWalletManager.getInstance().editPassWord("12345678", walletHandler);
////                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMainWallet().getKeystore());
//
//                        } else {
//                            Toast.makeText(CreateWalletActivity.this.getApplicationContext(), "钱包创建失败", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case WALLET_CREATE:
//
//                        /*
//                         * 创建钱包
//                         * */
//                        dismissProgressDialog();
//                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//                            long id = saveWallet();
//                            ApiWalletManager.printtest(id + "");
//                            Toast.makeText(CreateWalletActivity.this.getApplicationContext(), "钱包创建成功", Toast.LENGTH_SHORT).show();
//                            //一个主钱包的 都没有的情况 添加导入钱包 第一个都做为默认主钱包
//                            String nowWalletName = (String) LibSPUtils.get(CreateWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULL);
//                            if (nowWalletName.equals(Comm.NULLWALLET)) {
//                                ApiWalletManager.getInstance().setMainWallet(mCreateWallet);
//                                LibSPUtils.put(CreateWalletActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, mCreateWallet.getName());
//                            }
//                            //初始化用户跳转主页面
//                            if (!isAddMode) {
//                                startMain();
//                            } else {
//                                finish();
//                                SoftKeyboardUtil.hideSoftKeyboard(CreateWalletActivity.this);
//                            }
//                        } else {
//                            Toast.makeText(CreateWalletActivity.this.getApplicationContext(), "钱包创建失败", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case WALLET_BALANCE:
//                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMainWallet().getBalance());
//                        } else {
//                            ApiWalletManager.printtest("ERRRRRR");
//                        }
//                        break;
//                    case WALLET_IMPORT:
//                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMainWallet().getPublickey());
//                        } else {
//                            ApiWalletManager.printtest("ERRRRRR");
//                        }
//                        break;
//                    case WALLET_EDIT_PASS:
//                        if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
//                            ApiWalletManager.printtest(ApiWalletManager.getInstance().getMainWallet().getKeystore());
//                        } else {
//                            ApiWalletManager.printtest("ERRRRRR");
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
    protected void handleIntent() {
        super.handleIntent();
        Intent intent = getIntent();
        isAddMode = intent.getBooleanExtra(Comm.JUMP_PARM_ISADDMODE, false);
        ApiWalletManager.printtest(isAddMode + "");
    }

    @Override
    protected void initView() {
        findViews();
        mImmersionBar.titleBar(R.id.toolbarlayout).statusBarDarkFont(true).execute();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_create;
    }

    @Override
    protected void initData() {

    }

//    @Override
//    protected void initData(Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    public int getActivityMenuRes() {
//        return 0;
//    }
//
//    @Override
//    public int getHomeAsUpIndicatorIcon() {
//        return R.mipmap.arrow_back_blue;
//    }
//
//    @Override
//    public int getActivityTitleContent() {
//        return R.string.activity_create_wallet;
//    }

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

    @AfterPermissionGranted(REQUEST_CODE_CREATE_PERMISSIONS)
    private void requestCodeCreatePermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "创建钱包需要的权限", REQUEST_CODE_CREATE_PERMISSIONS, perms);
        } else {
            String walletname = walletNameEt.getText().toString().trim();
            String resetpass = resetPwdEt.getText().toString().trim();
            String pass = pwdEt.getText().toString().trim();//获取密码

            if (!resetpass.equals(pass)) {
                ToastUtil.showShortToast("密码和重复密码必须相同");
                return;
            }

            /*
             * 从数据库比对，重复检查
             * */
            if (null != WalletDaoTools.getWalletByName(walletname)) {
                Toast.makeText(this.getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                return;
            }

            WalletManager.getInstance().createBip39Wallet(walletname, pass, this);
//            cerateWallet();

        }
    }

    private void cerateWallet() {
        try {
            //创建默认目录
            File file = new File(ApiWalletManager.DEF_WALLET_PATH);
            if (!file.exists()) {
                boolean crate = file.mkdirs();
            }
            String walletname = walletNameEt.getText().toString().trim();
            String resetpass = resetPwdEt.getText().toString().trim();
            String pass = pwdEt.getText().toString().trim();//获取密码

            if (!resetpass.equals(pass)) {
                ToastUtil.showShortToast("密码和重复密码必须相同");
                return;
            }

            /*
             * 从数据库比对，重复检查
             * */
            if (null != WalletDaoTools.getWalletByName(walletname)) {
                Toast.makeText(this.getApplicationContext(), "该名称的钱包已经存在，请换一个钱包名称", Toast.LENGTH_SHORT).show();
                return;
            }
            mCreateWallet = new WalletBean();
            mCreateWallet.setName(walletname);
            mCreateWallet.setPassword(pass);//设置密码
            mCreateWallet.setIconIdex(getNum(7));//设置随机的头像

            WalletManager.getInstance().createBip39Wallet(walletname, pass, this);
//            showProgressDialog("正在创建钱包请稍候");
            Log.e("wallet", "" + walletname + " " + resetpass + " " + pass);
        } catch (Exception e) {
            Log.e("wallet", "errr", e);
        }

    }

    @Override
    public void onCreateSuccess(WalletBean walletBean) {
        Log.i(TAG, "onCreateSuccess: " + walletBean);
        SoftKeyboardUtil.hideSoftKeyboard(this);
        skip(MainActivity.class);
    }

    @Override
    public void onCreateFailure(String result) {
        Log.i(TAG, "onCreateFailure: " + result);
        SoftKeyboardUtil.hideSoftKeyboard(this);
    }
}
