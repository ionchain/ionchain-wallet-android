package org.ionchain.wallet.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fast.lib.logger.Logger;

import org.ionchain.wallet.R;
import org.ionchain.wallet.ui.MainActivity;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.wallet.CreateWalletProtocolActivity;

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

    @Override
    public void handleMessage(int what, Object obj) {
        super.handleMessage(what, obj);
        try{

            switch (what){
                case R.id.navigationBack:

                    finish();
                    break;
                case getEncodeKeys_network_type:
                    dismissProgressDialog();
                    if(obj == null)
                        return;



                    break;
            }
        }catch (Throwable e){
            Logger.e(TAG,e);
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
        try{
            Animation aAnimation = new AlphaAnimation(0.0f, 1.0f);
            aAnimation.setDuration(2000);
            aAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationEnd(Animation animation) {

                    startMainActivity();

                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }
            });
            rootview.startAnimation(aAnimation);





        }catch(Throwable e){
            Logger.e(TAG,e);
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

    void startMainActivity(){
        try{
            Logger.i(TAG+"===>startMainActivity");
            Intent intent = new Intent(WelcomeActivity.this, CreateWalletProtocolActivity.class);
            startActivity(intent);
            finish();
        }catch(Throwable e){
            Logger.e(TAG,e);
        }
    }
}
