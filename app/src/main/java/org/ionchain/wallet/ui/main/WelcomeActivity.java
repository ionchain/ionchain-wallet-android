package org.ionchain.wallet.ui.main;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fast.lib.utils.LibSPUtils;
import com.fast.lib.utils.encrypt.base.TextUtils;

import org.ionchain.wallet.R;
import org.ionchain.wallet.comm.api.ApiWalletManager;
import org.ionchain.wallet.comm.api.constant.ApiConstant;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.comm.api.resphonse.ResponseModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.mvp.view.activity.MainActivity;
import org.ionchain.wallet.ui.base.AbsBaseActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletSelectActivity;

/**
 * Created by siberiawolf on 17/4/28.
 */

public class WelcomeActivity extends AbsBaseActivity {

    RelativeLayout welcome_layout;


    @SuppressLint("HandlerLeak")
    Handler walletHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ApiConstant.WalletManagerType resulit = ApiConstant.WalletManagerType.codeOf(msg.what);
            if (null == resulit) return;
            ResponseModel<String> responseModel = (ResponseModel<String>) msg.obj;
            switch (resulit) {
                case MANAGER_INIT://此处可以改为回调
                    if (responseModel.code.equals(ApiConstant.WalletManagerErrCode.SUCCESS.name())) {
                        if (TextUtils.isEmpty(ApiWalletManager.getInstance().getMainWallet().getName())) {
                            transfer(CreateWalletSelectActivity.class);
                        } else {
                            transfer(MainActivity.class);
                        }
                        finish();
                    } else {
                        Toast.makeText(Global.mContext, "钱包初始化失败", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void initData() {
        Animation aAnimation = new AlphaAnimation(0.0f, 1.0f);
        aAnimation.setDuration(2000);
        aAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {

                WalletBean wallet = null;

                //从SP中读取 首页展示的钱包的名字
                String nowWalletName = (String) LibSPUtils.get(Global.mContext, Comm.LOCAL_SAVE_NOW_WALLET_NAME, Comm.NULL);
                if (!TextUtils.isEmpty(nowWalletName)) {
                    wallet = WalletDaoTools.getWalletByName(nowWalletName);
                    //处理钱包被删除以后的逻辑
                    if (null == wallet) {
                        wallet = new WalletBean();
                        wallet.setName(Comm.NULLWALLETNAME);
                        wallet.setAddress(Comm.NULLWALLETNAME);
                    }
                }
                if (null == wallet) {
                    wallet = new WalletBean();
                }

                ApiWalletManager.getInstance(Global.mContext).init(walletHandler, wallet);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {

            }
        });
        welcome_layout.startAnimation(aAnimation);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        welcome_layout = findViewById(R.id.welcome_layout);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }


}
