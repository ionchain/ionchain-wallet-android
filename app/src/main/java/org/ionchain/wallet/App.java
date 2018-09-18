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
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.ionchain.wallet.bean.UserModel;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.manager.WalletManager;
import org.ionchain.wallet.utils.SPUtils;

import java.util.List;


public class App extends LibApp {

    String TAG = "App";


    public static  Handler mHandler = new Handler(Looper.getMainLooper());

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
        initData();
        Stetho.initializeWithDefaults(this);
        OkGo.getInstance().init(this);
        WalletManager.getInstance().initWeb3j(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
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
