package com.fast.lib.base;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.fast.lib.R;
import com.fast.lib.event.AppEvent;
import com.fast.lib.event.CommonEvent;
import com.fast.lib.event.DataRefreshEvent;
import com.fast.lib.listener.ActivtyDataRefreshListener;
import com.fast.lib.logger.Logger;
import com.fast.lib.okhttp.ResponseBean;
import com.fast.lib.okhttp.callback.ResultCallback;
import com.fast.lib.okhttp.request.OkHttpGetRequest;
import com.fast.lib.okhttp.request.OkHttpPostRequest;
import com.fast.lib.okhttp.request.OkHttpRequest;
import com.fast.lib.utils.ToastUtil;
import com.squareup.leakcanary.RefWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import okhttp3.Request;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public abstract class LibActivity extends AppCompatActivity implements View.OnClickListener,
		ActivtyDataRefreshListener,LibFragment.OnFragmentInteractionListener,
		BGASwipeBackHelper.Delegate,EasyPermissions.PermissionCallbacks {

	private static final String TAG = "LibraryActivity";
	public static final int DISMISS_ALERT_DIALOG_TYPE = 100000;
	public static final int DISMISS_PROGRESS_DIALOG_TYPE = 10001;
	public static final int SHOW_PROGRESS_DIALOG_TYPE = 10002;
	public static final int NETWORK_EXCEPTION_TYPE = 10003;
	public static final int NETWORK_SUCCESSFUL_TYPE = 10004;
	public static final int NETWORK_FAIL_TYPE = 10005;
	public static final int JSON_DATA_ERROR_TYPE = 10006;


	private AlertDialog mAlertDialog;
	private ProgressDialog mProgressDialog;

	protected BGASwipeBackHelper mSwipeBackHelper;



	private static class LibHandler extends Handler{
		private WeakReference<LibActivity> reference;
		public LibHandler(LibActivity libraryActivity) {
			reference = new WeakReference<LibActivity>(libraryActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try{
				switch (msg.what){
					case SHOW_PROGRESS_DIALOG_TYPE:

						if(reference.get().mProgressDialog == null)
							reference.get().initProgressDialog();

						if(msg.obj != null)
							reference.get().mProgressDialog.setMessage(String.valueOf(msg.obj));

						if(!reference.get().mProgressDialog.isShowing())
							reference.get().mProgressDialog.show();
						break;
					case DISMISS_ALERT_DIALOG_TYPE:
						if(reference.get().mAlertDialog != null && reference.get().mAlertDialog.isShowing())
							reference.get().mAlertDialog.dismiss();
						break;
					case DISMISS_PROGRESS_DIALOG_TYPE:
						if(reference.get().mProgressDialog != null && reference.get().mProgressDialog.isShowing())
							reference.get().mProgressDialog.dismiss();
						break;
					case NETWORK_EXCEPTION_TYPE:

						reference.get().dismissAlertDialog();
						reference.get().dismissProgressDialog();

						if(msg.obj == null)
							return;
						ToastUtil.showShortToast(String.valueOf(msg.obj));

						break;
					case NETWORK_SUCCESSFUL_TYPE:
						reference.get().aidHandleMessage(msg.what,msg.obj);
						break;
					case NETWORK_FAIL_TYPE:
						reference.get().aidHandleMessage(msg.what,msg.obj);
						break;
					default:
						reference.get().aidHandleMessage(msg.what,msg.obj);

				}
			}catch (Throwable e){
				Log.e(TAG,"handleMessage",e);
			}
		}
	}

	private LibHandler mHandler = new LibHandler(this);


	public void aidHandleMessage(int what,Object obj){};
	public void aidsendMessage(int what,Object obj){
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.obj = obj;
		mHandler.sendMessage(msg);

	}

	public void aidsendMessage(int what,int arg1,Object obj){
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.arg1 = arg1;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}

	public void aidsendMessage(int what,Object obj,long delayMillis){
		Message msg = mHandler.obtainMessage();
		msg.what = what;
		msg.obj = obj;
		mHandler.sendMessageDelayed(msg, delayMillis);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(enableSwipeBack())
		  initSwipeBackFinish();
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (isRegisterEventBus()) {
			EventBus.getDefault().register(this);
		}



	}

	public void showAlertDialog(String message){
		showAlertDialog("提示", message, "确定", "取消", DISMISS_ALERT_DIALOG_TYPE, DISMISS_ALERT_DIALOG_TYPE, null, null);
	}


	public void showAlertDialog(String title,String message,String sureString,String cancelString,final int sureType,final int cancelType,final Object sureObject,final Object cancelObject ){
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
			if(!TextUtils.isEmpty(title))
			     builder.setTitle(title);

					builder.setMessage(message)
					.setNegativeButton(sureString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							aidsendMessage(sureType,sureObject);

						}
					})
					.setPositiveButton(cancelString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							aidsendMessage(cancelType,cancelObject);

						}
					});

					/*.setNeutralButton("让我想想", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});*/
			mAlertDialog = builder.create();
			mAlertDialog.setCancelable(true);
			mAlertDialog.show();

			/*Button button = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
			button.setTextColor(getResources().getColor(R.color.black));*/


		}catch(Throwable e){
			Log.e(TAG,"showAlertDialog",e);
		}
	}



	public void showAlertDialog(String title,String message,String sureString,String cancelString,final int sureType,final int cancelType,final Object sureObject,final Object cancelObject,boolean cancelable ){
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
			if(!TextUtils.isEmpty(title))
				builder.setTitle(title);

			builder.setMessage(message)
					.setNegativeButton(sureString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							aidsendMessage(sureType,sureObject);

						}
					})
					.setPositiveButton(cancelString, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							aidsendMessage(cancelType,cancelObject);

						}
					});

					/*.setNeutralButton("让我想想", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});*/
			mAlertDialog = builder.create();
			mAlertDialog.setCancelable(cancelable);
			mAlertDialog.show();

			/*Button button = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
			button.setTextColor(getResources().getColor(R.color.black));*/


		}catch(Throwable e){
			Log.e(TAG,"showAlertDialog",e);
		}
	}


	private void initProgressDialog(){
		try{
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("正在加载，请稍后...");
			mProgressDialog.setCancelable(false);
		}catch (Throwable e){
			Log.e(TAG,"initProgressDialog",e);
		}
	}

	public void showProgressDialog(){
		try{
			showProgressDialog(null);
		}catch(Throwable e){
			Log.e(TAG,"showProgressDialog",e);
		}
	}

	public void showProgressDialog(String message){
		try{
			aidsendMessage(SHOW_PROGRESS_DIALOG_TYPE, message);
		}catch(Throwable e){
			Log.e(TAG,"showProgressDialog",e);
		}
	}

	public void dismissProgressDialog(){
		try {
			aidsendMessage(DISMISS_PROGRESS_DIALOG_TYPE, null);
		}catch (Throwable e){
			Log.e(TAG,"dismissProgressDialog",e);

		}
	}

	public void dismissAlertDialog(){
		try{
			aidsendMessage(DISMISS_ALERT_DIALOG_TYPE, null);
		}catch(Throwable e){
			Log.e(TAG,"dismissAlertDialog",e);
		}
	}


	public void sendHttpGet(String url,HashMap<String,String> map,HashMap<String,String> headers, final Type type,final int refreshType){
		try{

			new OkHttpGetRequest.Builder().url(url).params(map).headers(headers).get(new ResultCallback<String>() {
				@Override
				public void onError(Request request, Exception e) {
					Logger.e(TAG, e.getMessage() + "===" + e.getLocalizedMessage());
					String errorMsg = "网络错误";
					if (e instanceof UnknownHostException) {
						errorMsg = NETWORK_EXCEPTION_TYPE+":当前网络不可用，请检查网络再试";
						//aidsendMessage(NETWORK_EXCEPTION_TYPE, "当前网络不可用，请检查网络再试");
					} else if (e instanceof SocketTimeoutException) {
						errorMsg = NETWORK_EXCEPTION_TYPE+":请求超时，请稍后再试";
						//aidsendMessage(NETWORK_EXCEPTION_TYPE, "请求超时，请稍后再试");
					} else {
						errorMsg = NETWORK_EXCEPTION_TYPE+":网络异常，请稍后再试";
						//aidsendMessage(NETWORK_EXCEPTION_TYPE, "网络异常，请稍后再试");
					}

					//将错误信息返回上一层  上层可以不处理该回调的结果
					ResponseBean responseBean = new ResponseBean();
					responseBean.refreshType = refreshType;
					responseBean.obj = errorMsg;
					aidsendMessage(NETWORK_FAIL_TYPE, responseBean);
				}

				@Override
				public void onResponse(String response) {


					ResponseBean responseBean = new ResponseBean();
					responseBean.refreshType = refreshType;
					responseBean.obj = response;
					responseBean.mType = type;
					aidsendMessage(NETWORK_SUCCESSFUL_TYPE, responseBean);
				}
			});

		}catch (Throwable e){
			Log.e(TAG,"sendHttpGet",e);
		}
	}


	public void sendHttpPost(String url,HashMap<String,String> map,HashMap<String,String> headers,final Type type,final int refreshType){
		try{
			new OkHttpPostRequest.Builder()
					.url(url)
					.params(map)
					.headers(headers)
					.post(new ResultCallback<String>() {
						@Override
						public void onError(Request request, Exception e) {
							Logger.e(TAG, e.getMessage() + "===" + e.getLocalizedMessage());

							String errorMsg = "网络错误";
							if (e instanceof UnknownHostException) {
								errorMsg = NETWORK_EXCEPTION_TYPE+":当前网络不可用，请检查网络再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "当前网络不可用，请检查网络再试");
							} else if (e instanceof SocketTimeoutException) {
								errorMsg = NETWORK_EXCEPTION_TYPE+":请求超时，请稍后再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "请求超时，请稍后再试");
							} else {
								errorMsg = NETWORK_EXCEPTION_TYPE+":网络异常，请稍后再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "网络异常，请稍后再试");
							}

							//将错误信息返回上一层  上层可以不处理该回调的结果
							ResponseBean responseBean = new ResponseBean();
							responseBean.refreshType = refreshType;
							responseBean.obj = errorMsg;
							aidsendMessage(NETWORK_FAIL_TYPE, responseBean);
						}

						@Override
						public void onResponse(String response) {
							ResponseBean responseBean = new ResponseBean();
							responseBean.refreshType = refreshType;


							responseBean.obj = response;
							responseBean.mType = type;
							aidsendMessage(NETWORK_SUCCESSFUL_TYPE, responseBean);
						}
					});

		}catch (Throwable e){
			Log.e(TAG,"sendHttpPost",e);
		}
	}


	public void sendHttpPost(String url,String json,HashMap<String,String> headers,final Type type,final int refreshType){
		try{
			new OkHttpPostRequest.Builder()
					.url(url)
					.json(json)
					.headers(headers)
					.post(new ResultCallback<String>() {
						@Override
						public void onError(Request request, Exception e) {
//							Logger.e(e,TAG+"==sendHttpPost=="+request.toString());
							String errorMsg = "网络错误";
							if (e instanceof UnknownHostException) {
								errorMsg = NETWORK_EXCEPTION_TYPE+":当前网络不可用，请检查网络再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "当前网络不可用，请检查网络再试");
							} else if (e instanceof SocketTimeoutException) {
								errorMsg = NETWORK_EXCEPTION_TYPE+":请求超时，请稍后再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "请求超时，请稍后再试");
							} else {
								errorMsg = NETWORK_EXCEPTION_TYPE+":网络异常，请稍后再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "网络异常，请稍后再试");
							}

							//将错误信息返回上一层  上层可以不处理该回调的结果
							ResponseBean responseBean = new ResponseBean();
							responseBean.refreshType = refreshType;
							responseBean.obj = errorMsg;
							aidsendMessage(NETWORK_FAIL_TYPE, responseBean);
						}

						@Override
						public void onResponse(String response) {
							ResponseBean responseBean = new ResponseBean();
							responseBean.refreshType = refreshType;


							responseBean.obj = response;
							responseBean.mType = type;
							aidsendMessage(NETWORK_SUCCESSFUL_TYPE, responseBean);
						}
					});

		}catch (Throwable e){
			Logger.e(e,"sendHttpPost");
		}
	}


	public void sendHttpUpload(String url, HashMap<String, String> map, final Type type, final int refreshType, Pair<String, File>... files){
		try{
			new OkHttpRequest.Builder()
					.url(url)
					.params(map)
					.files(files)
					.upload(new ResultCallback<String>() {
						@Override
						public void onError(Request request, Exception e) {
							Logger.e(TAG+e.getMessage() + "===" + e.getLocalizedMessage());
							e.printStackTrace();
							String errorMsg = "网络错误";
							if (e instanceof UnknownHostException) {
								errorMsg = NETWORK_EXCEPTION_TYPE+":当前网络不可用，请检查网络再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "当前网络不可用，请检查网络再试");
							} else if (e instanceof SocketTimeoutException) {
								errorMsg = NETWORK_EXCEPTION_TYPE+":请求超时，请稍后再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "请求超时，请稍后再试");
							} else {
								errorMsg = NETWORK_EXCEPTION_TYPE+":网络异常，请稍后再试";
								//aidsendMessage(NETWORK_EXCEPTION_TYPE, "网络异常，请稍后再试");
							}

							//将错误信息返回上一层  上层可以不处理该回调的结果
							ResponseBean responseBean = new ResponseBean();
							responseBean.refreshType = refreshType;
							responseBean.obj = errorMsg;
							aidsendMessage(NETWORK_FAIL_TYPE, responseBean);
						}
						@Override
						public void onResponse(String response) {

							try{
								ResponseBean responseBean = new ResponseBean();
								responseBean.refreshType = refreshType;

								responseBean.obj = response;
								responseBean.mType = type;
								aidsendMessage(NETWORK_SUCCESSFUL_TYPE, responseBean);
							}catch (Throwable e){
								Logger.e(e,TAG);
							}



						}
						@Override
						public void inProgress(float progress) {
							super.inProgress(progress);
							try{
								Logger.i("progress==>"+progress);
								//showProgressDialog("正在上传"+progress*100+"%");

							}catch (Throwable e){
								Logger.e(e,TAG);
							}

						}
					});

		}catch (Throwable e){
			Log.e(TAG,"sendHttpUpload",e);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		RefWatcher refWatcher = LibApp.getRefWatcher(this);
		refWatcher.watch(this);

		// Stop method tracing that the activity started during onCreate()
		//如果activity含有在onCreate调用时创建的后台线程，或者是其他有可能导致内存泄漏的资源，
		// 则应该在OnDestroy()时进行资源清理，杀死后台线程。
		android.os.Debug.stopMethodTracing();


		if (isRegisterEventBus()) {
			EventBus.getDefault().unregister(this);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onRefresh(int refreshtype, Object obj) {
		aidsendMessage(refreshtype,obj);
	}

	@Override
	public void onClick(View v) {
		aidsendMessage(v.getId(),v);
	}

	@Override
	public void onFragmentInteraction(int type, Object obj) {
		aidsendMessage(type,obj);
	}



	public void transfer(Class<?> clazz) {
		try {
			Intent intent = new Intent( this, clazz );
			startActivityForResult( intent, 0 );
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}

	public void transfer(Class<?> clazz, int requestCode) {
		try {
			Intent intent = new Intent( this, clazz );
			startActivityForResult( intent, requestCode );
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}

	public void transfer(Class<?> clazz,String params, Serializable obj) {
		try {
			Intent intent = new Intent( this, clazz );
			intent.putExtra( params, obj );
			startActivity( intent);
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}

	public void transfer(Class<?> clazz, String params, Serializable obj, int requestCode) {
		try {
			Intent intent = new Intent( this, clazz );
			intent.putExtra( params, obj );
			startActivityForResult( intent, requestCode );
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}

	public void transfer(Class<?> clazz, String params,Serializable obj, String params1, Serializable obj1) {
		try {
			Intent intent = new Intent( this, clazz );
			intent.putExtra( params, obj );
			intent.putExtra( params1, obj1 );
			startActivityForResult( intent, 0 );
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}

	public void transfer(Class<?> clazz, String params,String obj,  String params1, Serializable obj1, int requestCode) {
		try {
			Intent intent = new Intent( this, clazz );
			intent.putExtra( params, obj );
			intent.putExtra( params1, obj1 );
			startActivityForResult( intent, requestCode );
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}


	public void transfer(Class<?> clazz, String params,Serializable obj, String params1, Serializable obj1, String params2, Serializable obj2) {
		try {
			Intent intent = new Intent( this, clazz );
			intent.putExtra( params, obj );
			intent.putExtra( params1, obj1 );
			intent.putExtra( params2, obj2 );
			startActivityForResult( intent, 0 );
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}

	public void transfer(Class<?> clazz, String params,Serializable obj, String params1, Serializable obj1, String params2, Serializable obj2,int requestCode) {
		try {
			Intent intent = new Intent( this, clazz );
			intent.putExtra( params, obj );
			intent.putExtra( params1, obj1 );
			intent.putExtra( params2, obj2 );
			startActivityForResult( intent,requestCode );
		} catch (Throwable e) {
			Logger.e(TAG,e);
		}
	}


	/**
	 * 是否注册事件分发
	 *
	 * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
	 */
	protected boolean isRegisterEventBus() {
		return false;
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventBusCome(CommonEvent event) {
		if (event != null) {
			receiveEvent(event);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventBusCome(DataRefreshEvent event) {
		if (event != null) {
			receiveEvent(event);
		}
	}


	/**
	 * 接收到分发到事件
	 *
	 * @param event 事件
	 */
	protected void receiveEvent(AppEvent event) {
		aidsendMessage(event.getCode(), event);
	}



	/**
	 * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
	 */
	private void initSwipeBackFinish() {

		if(!enableSwipeBack())
			return;


		mSwipeBackHelper = new BGASwipeBackHelper(this, this);

		// 「必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回」
		// 下面几项可以不配置，这里只是为了讲述接口用法。

		// 设置滑动返回是否可用。默认值为 true
		mSwipeBackHelper.setSwipeBackEnable(true);
		// 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
		mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
		// 设置是否是微信滑动返回样式。默认值为 true
		mSwipeBackHelper.setIsWeChatStyle(true);
		// 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
		mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow);
		// 设置是否显示滑动返回的阴影效果。默认值为 true
		mSwipeBackHelper.setIsNeedShowShadow(true);
		// 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
		mSwipeBackHelper.setIsShadowAlphaGradient(true);
		// 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
		mSwipeBackHelper.setSwipeBackThreshold(0.3f);
		// 设置底部导航条是否悬浮在内容上，默认值为 false
		mSwipeBackHelper.setIsNavigationBarOverlap(false);
	}

	public boolean enableSwipeBack(){
		return true;
	}

	/**
	 * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
	 *
	 * @return
	 */
	@Override
	public boolean isSupportSwipeBack() {
		return true;
	}

	/**
	 * 正在滑动返回
	 *
	 * @param slideOffset 从 0 到 1
	 */
	@Override
	public void onSwipeBackLayoutSlide(float slideOffset) {
	}

	/**
	 * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
	 */
	@Override
	public void onSwipeBackLayoutCancel() {
	}

	/**
	 * 滑动返回执行完毕，销毁当前 Activity
	 */
	@Override
	public void onSwipeBackLayoutExecuted() {
		mSwipeBackHelper.swipeBackward();
	}

	@Override
	public void onBackPressed() {

		if(!enableSwipeBack()){
			super.onBackPressed();
			return;
		}

		if(mSwipeBackHelper != null){
			// 正在滑动返回的时候取消返回按钮事件
			if (mSwipeBackHelper.isSliding()) {
				return;
			}
			mSwipeBackHelper.backward();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		// EasyPermissions handles the request result.
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		Logger.i(TAG+"onPermissionsGranted:" + requestCode + ":" + perms.size());
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
		Logger.i(TAG+"onPermissionsDenied:" + requestCode + ":" + perms.size());

		// (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
		// This will display a dialog directing them to enable the permission in app settings.
		if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
			new AppSettingsDialog.Builder(this).build().show();
		}
	}
}
