package com.fast.lib.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fast.lib.BuildConfig;
import com.fast.lib.comm.LibComm;
import com.fast.lib.comm.LibGlobal;
import com.fast.lib.logger.AndroidLogAdapter;
import com.fast.lib.logger.CsvFormatStrategy;
import com.fast.lib.logger.DiskLogAdapter;
import com.fast.lib.logger.FormatStrategy;
import com.fast.lib.logger.Logger;
import com.fast.lib.logger.PrettyFormatStrategy;
import com.fast.lib.network.LoggingInerceptor;
import com.fast.lib.okhttp.OkHttpClientManager;
import com.fast.lib.utils.LibDateUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;


public abstract class LibApp extends Application {

	private static final String TAG = "LibApp";

	private RefWatcher refWatcher;

    @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		if (LibComm.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}
		super.onCreate();
		
		LibGlobal.mContext = getApplicationContext();
		initDevTools();
		initLogger();
		initGson();
		initData();

		/**
		 * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
		 * 第一个参数：应用程序上下文
		 * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
		 */
		BGASwipeBackHelper.init(this, getProblemViewClassList());

	}


	private void initDevTools() {
		try {


			if (LeakCanary.isInAnalyzerProcess(this)) {
				// This process is dedicated to LeakCanary for heap analysis.
				// You should not init your app in this process.
				return;
			}

			if (BuildConfig.DEBUG)
				refWatcher = LeakCanary.install(this);
			else
				refWatcher = installLeakCanary();


			Stetho.initializeWithDefaults(this);
		} catch (Throwable e) {
			Logger.e(TAG, e);
		}
	}

	protected RefWatcher installLeakCanary() {
		return RefWatcher.DISABLED;
	}

	public static RefWatcher getRefWatcher(Context context) {
		LibApp application = (LibApp) context.getApplicationContext();
		return application.refWatcher;
	}



	private void initLogger(){
		try{
			FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
					.showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
					.methodCount(0)         // (Optional) How many method line to show. Default 2
					.methodOffset(0)        // (Optional) Skips some method invokes in stack trace. Default 5//        .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
					.tag(getLoggerTag())   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
					.build();
			Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
				@Override
				public boolean isLoggable(int priority, String tag) {
					return isloggable();
				}
			});

			FormatStrategy csvFormatStrategy = CsvFormatStrategy.newBuilder()
					.dateFormat(new SimpleDateFormat(LibDateUtils.FORMAT_YMDHM_CN))     // (Optional) How many method line to show. Default 2
					       // (Optional) Skips some method invokes in stack trace. Default 5//        .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
					.tag(getLoggerTag())   // (Optional) Custom tag for each log. Default PRETTY_LOGGER
					.build();
			Logger.addLogAdapter(new DiskLogAdapter(csvFormatStrategy));


		}catch (Throwable e){
			Logger.e(TAG,e);
		}
	}

	public abstract  String getLoggerTag();
	public abstract  boolean isloggable();
	public abstract List<Class<? extends View>> getProblemViewClassList();


	private void initData() {
		try {

			OkHttpClientManager.getInstance().setOkHttpClient(OkHttpClientManager.getInstance().getOkHttpClient().newBuilder()
					.addNetworkInterceptor(new StethoInterceptor())
					.addInterceptor(new LoggingInerceptor())
					.build());
		} catch (Throwable e) {
			Logger.e(TAG, e);
		}
	}

	private void initGson() {
		try {
			final int sdk = Build.VERSION.SDK_INT;
			if (sdk >= 23) {
				GsonBuilder gsonBuilder = new GsonBuilder()
						.excludeFieldsWithModifiers(
								Modifier.FINAL,
								Modifier.TRANSIENT,
								Modifier.STATIC);
				LibGlobal.mGson = gsonBuilder.create();
			} else {
				LibGlobal.mGson = new Gson();
			}
		} catch (Throwable e) {
			Logger.e(TAG, e);
		}
	}
	
	

}
