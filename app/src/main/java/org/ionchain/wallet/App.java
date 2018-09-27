package org.ionchain.wallet;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.ionchain.wallet.manager.WalletManager;



public class App extends Application {

    String TAG = "App";


    public static  Handler mHandler = new Handler(Looper.getMainLooper());

    public static  Context mContext;



    public static int[] sRandomHeader = {
            R.mipmap.random_header_more_1,
            R.mipmap.random_header_more_2,
            R.mipmap.random_header_more_3,
            R.mipmap.random_header_more_4,
            R.mipmap.random_header_more_5,
            R.mipmap.random_header_more_6,
            R.mipmap.random_header_more_7,
            R.mipmap.random_header_more_8,
    };
    public static int[] sRandomHeaderMore = {
            R.mipmap.random_header_1,
            R.mipmap.random_header_2,
            R.mipmap.random_header_3,
            R.mipmap.random_header_4,
            R.mipmap.random_header_5,
            R.mipmap.random_header_6,
            R.mipmap.random_header_7,
            R.mipmap.random_header_8,
    };
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Stetho.initializeWithDefaults(this);
        OkGo.getInstance().init(this);
        WalletManager.getInstance().initWeb3j(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

//    @Override
//    public String getLoggerTag() {
//        return "ionchainlog";
//    }
//
//    @Override
//    public boolean isloggable() {
//        return BuildConfig.DEBUG;
//    }
//
//    @Override
//    public List<Class<? extends View>> getProblemViewClassList() {
//        return null;
//    }


}
