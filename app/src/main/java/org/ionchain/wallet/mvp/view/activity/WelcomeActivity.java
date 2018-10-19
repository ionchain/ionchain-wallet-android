package org.ionchain.wallet.mvp.view.activity;

import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import org.ionchain.wallet.R;
import org.ionchain.wallet.dao.WalletDaoTools;
import org.ionchain.wallet.mvp.view.activity.createwallet.CreateWalletSelectActivity;
import org.ionchain.wallet.mvp.view.base.AbsBaseActivity;

import static org.ionchain.wallet.constant.ConstantParams.FROM_WELCOME;

/**
 * Created by siberiawolf on 17/4/28.
 */

public class WelcomeActivity extends AbsBaseActivity {

    RelativeLayout welcome_layout;


    @Override
    protected void initData() {
        Animation aAnimation = new AlphaAnimation(0.0f, 1.0f);
        aAnimation.setDuration(2000);
        aAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                if (WalletDaoTools.getAllWallet() != null && WalletDaoTools.getAllWallet().size() > 0) {
                    skip(MainActivity.class);
                } else {
                    Intent intent = new Intent(getMActivity(),CreateWalletSelectActivity.class);
                    intent.putExtra(FROM_WELCOME,true);
                    startActivity(intent);
                }
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
    protected void handleIntent(Intent intent) {

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
