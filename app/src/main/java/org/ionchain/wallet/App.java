package org.ionchain.wallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.formatter.message.json.DefaultJsonFormatter;
import com.elvishew.xlog.formatter.stacktrace.DefaultStackTraceFormatter;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.facebook.stetho.Stetho;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import org.ionc.wallet.daohelper.MyOpenHelper;
import org.ionc.wallet.greendaogen.DaoMaster;
import org.ionc.wallet.greendaogen.DaoSession;
import org.ionc.wallet.sdk.IONCWalletSDK;
import org.ionchain.wallet.crasher.CrashHandler;
import org.ionchain.wallet.helper.ActivityHelper;
import org.ionchain.wallet.view.activity.MainActivity;
import org.ionchain.wallet.qrcode.DisplayUtil;
import org.ionchain.wallet.qrcode.activity.ZXingLibrary;
import org.ionchain.wallet.utils.SPUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

import static org.ionc.wallet.constant.ConstanParams.DB_NAME;
import static org.ionchain.wallet.BuildConfig.APP_DEBUG;
import static org.ionchain.wallet.constant.ConstantCoinType.COIN_TYPE_USD;

/**
 * Created by binny on 2018/11/29.
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {
    @SuppressLint("StaticFieldLeak")
    public static App mAppInstance;
    public static String mCoinType = COIN_TYPE_USD;
    public static final Handler APP_HANDLE = new Handler(Looper.getMainLooper());
    public static int[] sRandomHeader = {
            R.mipmap.random_header_more_1, R.mipmap.random_header_more_2, R.mipmap.random_header_more_3, R.mipmap.random_header_more_4, R.mipmap.random_header_more_5, R.mipmap.random_header_more_6, R.mipmap.random_header_more_7, R.mipmap.random_header_more_8
    };
    public static int[] sRandomHeaderMore = {
            R.mipmap.random_header_1, R.mipmap.random_header_2, R.mipmap.random_header_3, R.mipmap.random_header_4, R.mipmap.random_header_5, R.mipmap.random_header_6, R.mipmap.random_header_7, R.mipmap.random_header_8
    };

    public static String currentLanguage = "";

    public static Printer globalFilePrinter;

    private static final long MAX_TIME = 1000 * 60 * 60 * 24 * 2; // two days

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;
        if (APP_DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
        OkGo.getInstance().init(this);
        initOKGO();
        initXlog();

        IONCWalletSDK.getInstance().initIONCWalletSDK(this, initDb());
        ZXingLibrary.initDisplayOpinion(this);
        CrashHandler.getInstance().init(this);
        initDisplayOpinion();
        registerActivityLifecycleCallbacks(this);
    }

    /**
     * @return 初始化数据库
     */
    private DaoSession initDb() {
        MyOpenHelper devOpenHelper = new MyOpenHelper(this, DB_NAME, null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        return mDaoMaster.newSession();
    }


    @Override
    protected void attachBaseContext(Context base) {
        mAppInstance = this;
        SPUtils.initSP(this);
        super.attachBaseContext(base);
        MultiDex.install(this);
        mCoinType = SPUtils.getInstance().getCoinType();//币种信息，默认人民币
    }

    private void initDisplayOpinion() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
    }

    private void initOKGO() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);        //log打印级别，决定了log显示的详细程度
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


    /**
     * @return 当前语言环境
     */
    public static boolean isCurrentLanguageZN() {
        String language = Locale.getDefault().toString();
        return language.contains("zh_CN");
    }

    /**
     * @return 当前语言环境
     */
    public static boolean isCurrentLanguageEN() {
        return Locale.getDefault().toString().contains("en");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        LoggerUtils.i(activity.getClass().getSimpleName() + "----onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        LoggerUtils.i( activity.getClass().getSimpleName() + "----onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
//        LoggerUtils.i(activity.getClass().getSimpleName() + "----onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
//        LoggerUtils.i( activity.getClass().getSimpleName() + "----onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        LoggerUtils.i( activity.getClass().getSimpleName() + "----onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//        LoggerUtils.i( activity.getClass().getSimpleName() + "----onActivitySaveInstanceState");

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        LoggerUtils.i(activity.getClass().getSimpleName() + "----onActivityDestroyed");
    }

    public static void skipToMain() {
        Intent intent = new Intent(ActivityHelper.getHelper().currentActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityHelper.getHelper().currentActivity().startActivity(intent);
    }

    /**
     * Initialize XLog.
     */
    private void initXlog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                        : LogLevel.NONE)
                .tag(getString(R.string.global_tag))                   // Specify TAG, default: "X-LOG"
                // .t()                                                // Enable thread info, disabled by default
                // .st(2)                                              // Enable stack trace info with depth 2, disabled by default
                // .b()                                                // Enable border, disabled by default
                .jsonFormatter(new DefaultJsonFormatter())               // Default: DefaultJsonFormatter
                // .xmlFormatter(new MyXmlFormatter())                 // Default: DefaultXmlFormatter
                // .throwableFormatter(new MyThrowableFormatter())     // Default: DefaultThrowableFormatter
                // .threadFormatter(new MyThreadFormatter())           // Default: DefaultThreadFormatter
                .stackTraceFormatter(new DefaultStackTraceFormatter())   // Default: DefaultStackTraceFormatter
                // .borderFormatter(new MyBoardFormatter())            // Default: DefaultBorderFormatter
                // .addObjectFormatter(AnyClass.class,                 // Add formatter for specific class of object
                //     new AnyClassObjectFormatter())                  // Use Object.toString() by default
                .addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
                        "blacklist1", "blacklist2", "blacklist3"))
                // .addInterceptor(new WhitelistTagsFilterInterceptor( // Add whitelist tags filter
                //     "whitelist1", "whitelist2", "whitelist3"))
                // .addInterceptor(new MyInterceptor())                // Add a log interceptor
                .build();

        Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log
        Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
                .Builder(new File(Environment.getExternalStorageDirectory(), "ionc_wallet_log").getPath())       // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                // .backupStrategy(new MyBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
                // .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // Default: NeverCleanStrategy()
                .flattener(new ClassicFlattener())                     // Default: DefaultFlattener
                .build();

        XLog.init(                                                 // Initialize XLog
                config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
                filePrinter);

        // For future usage: partial usage in MainActivity.
        globalFilePrinter = filePrinter;
    }

}
