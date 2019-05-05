package org.ionchain.wallet;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import org.ionc.qrcode.activity.ZXingLibrary;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionchain.wallet.crasher.CrashHandler;
import org.ionchain.wallet.utils.LocalManageUtil;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

import static org.ionc.wallet.utils.Logger.initLogger;
import static org.ionchain.wallet.BuildConfig.LOG_DEBUG;

/**
 * Created by binny on 2018/11/29.
 */
public class App extends Application {
    public static Context mContext;
    public static boolean SDK_Debug = false;
    public static Handler mHandler = new  Handler(Looper.getMainLooper());
    public static int[] sRandomHeader = {
            R.mipmap.random_header_more_1, R.mipmap.random_header_more_2, R.mipmap.random_header_more_3, R.mipmap.random_header_more_4, R.mipmap.random_header_more_5, R.mipmap.random_header_more_6, R.mipmap.random_header_more_7, R.mipmap.random_header_more_8
    };
    public static int[] sRandomHeaderMore = {
            R.mipmap.random_header_1, R.mipmap.random_header_2, R.mipmap.random_header_3, R.mipmap.random_header_4, R.mipmap.random_header_5, R.mipmap.random_header_6, R.mipmap.random_header_7, R.mipmap.random_header_8
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Stetho.initializeWithDefaults(this);
        OkGo.getInstance().init(this);
        initOKGO();
        initLogger(LOG_DEBUG);
        IONCWalletSDK.getInstance().initIONCWalletSDK(this);
        ZXingLibrary.initDisplayOpinion(this);
        CrashHandler.getInstance().init(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalManageUtil.setLocal(base));
        MultiDex.install(this);
    }

    private void initOKGO() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(5, TimeUnit.SECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(5, TimeUnit.SECONDS);   //全局的连接超时时间


        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0);                            //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0


    }
    /**
     * 保留两位小数 四舍五入
     *
     * @param f 原始浮点型数
     * @return 两位小数
     */
    public static float scale(float f) {
        BigDecimal b = new BigDecimal(f);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() * 100;
    }
}
