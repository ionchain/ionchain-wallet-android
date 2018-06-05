package org.ionchain.wallet.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.LibSPUtils;
import com.fast.lib.utils.encrypt.base.TextUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.comm.utils.StorageUtils;
import org.ionchain.wallet.db.EntityManager;
import org.ionchain.wallet.db.WalletDaoTools;
import org.ionchain.wallet.ui.MainActivity;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletSelectActivity;

import butterknife.BindView;

/**
 * Created by siberiawolf on 17/4/28.
 */

public class WelcomeActivity extends BaseActivity {


    final int getEncodeKeys_network_type = 100;

    @BindView(R.id.rootLayout)
    RelativeLayout rootview;

    @BindView(R.id.topImgIv)
    ImageView topImgIv;

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
                            startMainActivity();
                            //  ApiWalletManager.getInstance(,WelcomeActivity.this.getApplicationContext());
                        } else {
                            Toast.makeText(Global.mContext, "钱包初始化失败", Toast.LENGTH_SHORT).show();
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
                case getEncodeKeys_network_type:
                    dismissProgressDialog();
                    if (obj == null)
                        return;


                    break;
            }
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setSystemBar(false);
        super.onCreate(savedInstanceState);
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
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        try {
            Animation aAnimation = new AlphaAnimation(0.0f, 1.0f);
            aAnimation.setDuration(2000);
            aAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationEnd(Animation animation) {


                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                    Wallet wallet = null;
                    String nowWalletName = (String) LibSPUtils.get(WelcomeActivity.this.getApplicationContext(), Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULL);
                    if (!TextUtils.isEmpty(nowWalletName)) {
                        wallet = WalletDaoTools.getWalletByName(nowWalletName);
                    }
                    if (null == wallet) {
                        wallet = new Wallet();
                    }
                    ApiWalletManager.getInstance(wallet, WelcomeActivity.this.getApplicationContext()).init(walletHandler);
                }
            });
            rootview.startAnimation(aAnimation);


        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }

    @Override
    public int getActivityMenuRes() {
        return 0;
    }

    @Override
    public int getHomeAsUpIndicatorIcon() {
        return 0;
    }

    @Override
    public int getActivityTitleContent() {
        return 0;
    }

    void startMainActivity() {
        try {
            Logger.i(TAG + "===>startMainActivity");
            Intent intent = null;
            if (TextUtils.isEmpty(ApiWalletManager.getInstance().getMyWallet().getName())) {
                Toast.makeText(Global.mContext, "1111", Toast.LENGTH_SHORT).show();
                 intent = new Intent(WelcomeActivity.this, CreateWalletSelectActivity.class);
            }
            else{
                Toast.makeText(Global.mContext, "2222", Toast.LENGTH_SHORT).show();
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();
        } catch (Throwable e) {
            Logger.e(TAG, e);
        }
    }
}
