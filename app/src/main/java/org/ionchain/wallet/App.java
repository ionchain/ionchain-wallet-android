package org.ionchain.wallet;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.fast.lib.base.LibApp;
import com.fast.lib.glideimageloader.ImageLoadConfig;
import com.fast.lib.logger.Logger;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.comm.utils.SPUtils;
import org.ionchain.wallet.comm.utils.StorageUtils;
import org.ionchain.wallet.config.ImgLoader;
import org.ionchain.wallet.model.UserModel;

import java.util.List;


public class App extends LibApp {

    String TAG = "App";

    public static final String UPDATE_STATUS_ACTION = "org.ionchain.wallet.action.UPDATE_STATUS";

    private Handler handler;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                layout.setPrimaryColorsId(R.color.white,R.color.main_color);//全局设置主题颜色
                return new ClassicsHeader(context);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.windowBackgroundColor,R.color.black);//全局设置主题颜色
                layout.setEnableAutoLoadmore(true);

                return new ClassicsFooter(context);
            }
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        initData();
        Stetho.initializeWithDefaults(this);
        OkGo.getInstance().init(this);
    }

    private void initImageLoader() {
        StorageUtils.getIndividualCacheDirectory(Global.mContext, Comm.SDCARD_IMG_ROOT);
        ImgLoader.defConfig = new ImageLoadConfig.Builder().
                setCropType(ImageLoadConfig.CENTER_CROP).
                setAsBitmap(true).
                setColorFilter(0).
                setPlaceHolderResId(R.mipmap.def_img_lib).
                setErrorResId(R.mipmap.def_img_lib).
                setDiskCacheStrategy(ImageLoadConfig.DiskCache.SOURCE).
                setPrioriy(ImageLoadConfig.LoadPriority.HIGH).build();

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
