package org.ionchain.wallet;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.fast.lib.base.LibApp;
import com.fast.lib.logger.Logger;
import com.lzy.okgo.OkGo;

import org.ionchain.wallet.bean.UserModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.utils.SPUtils;

import java.util.List;


public class App extends LibApp {

    String TAG = "App";

//    public static final String UPDATE_STATUS_ACTION = "org.ionchain.wallet.action.UPDATE_STATUS";

    public static  Handler mHandler = new Handler(Looper.getMainLooper());
//
//    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
////
//    }


    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        Stetho.initializeWithDefaults(this);
        OkGo.getInstance().init(this);
        WalletManager.getInstance().initWeb3j(this);
    }




    private void initData(){
        try{


            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            int heapSize = activityManager.getMemoryClass();
            Logger.i("heapSize==>"+heapSize);

            String userinfo = SPUtils.get(Global.mContext,Comm.user,"").toString();
            if(!TextUtils.isEmpty(userinfo)){
                Global.user = Global.mGson.fromJson(userinfo,UserModel.class);
            }


        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public String getLoggerTag() {
        return "ionchainlog";
    }

    @Override
    public boolean isloggable() {
        return BuildConfig.DEBUG;
    }

    @Override
    public List<Class<? extends View>> getProblemViewClassList() {
        return null;
    }


}
