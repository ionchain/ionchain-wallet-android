package com.fast.lib.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.fast.lib.okhttp.request.OkHttpPostRequest;
import com.fast.lib.okhttp.request.OkHttpRequest;
import com.fast.lib.utils.ToastUtil;
import com.squareup.leakcanary.RefWatcher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.Request;

/**
 * Created by siberiawolf on 15/12/16.
 */
public class LibFragment extends Fragment implements View.OnClickListener,ActivtyDataRefreshListener {


    private static final String TAG = "LibraryFragment";
    public static final int DISMISS_ALERT_DIALOG_TYPE = 100000;
    public static final int DISMISS_PROGRESS_DIALOG_TYPE = 10001;
    public static final int SHOW_PROGRESS_DIALOG_TYPE = 10002;
    public static final int NETWORK_EXCEPTION_TYPE = 10003;
    public static final int NETWORK_SUCCESSFUL_TYPE = 10004;
    public static final int NETWORK_FAIL_TYPE = 10005;
    public static final int JSON_DATA_ERROR_TYPE = 10006;


    private AlertDialog mAlertDialog;
    private ProgressDialog mProgressDialog;

    private OnFragmentInteractionListener mListener;



    private static class LibHandler extends Handler{
        private WeakReference<LibFragment> reference;
        public LibHandler(LibFragment libraryFragment) {
            reference = new WeakReference<LibFragment>(libraryFragment);
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
                    Logger.e(e,TAG);
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


    public void showAlertDialog(String message){
        showAlertDialog("提示", message, "确定", "取消", DISMISS_ALERT_DIALOG_TYPE, DISMISS_ALERT_DIALOG_TYPE, null, null);
    }


    public void showAlertDialog(String title,String message,String sureString,String cancelString,final int sureType,final int cancelType,final Object sureObject,final Object cancelObject ){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
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
            mAlertDialog.show();

			/*Button button = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
			button.setTextColor(getResources().getColor(R.color.black));*/


        }catch(Throwable e){
            Logger.e(e,TAG);
        }
    }

    private void initProgressDialog(){
        try{
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载，请稍后...");
            mProgressDialog.setCancelable(true);
        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }

    public void showProgressDialog(){
        try{
            showProgressDialog(null);
        }catch(Throwable e){
            Logger.e(e,TAG);
        }
    }

    public void showProgressDialog(String message){
        try{
            aidsendMessage(SHOW_PROGRESS_DIALOG_TYPE, message);
        }catch(Throwable e){
            Logger.e(e,TAG);
        }
    }

    public void dismissProgressDialog(){
        try {
            aidsendMessage(DISMISS_PROGRESS_DIALOG_TYPE, null);
        }catch (Throwable e){
            Logger.e(e,TAG);

        }
    }

    public void dismissAlertDialog(){
        try{
            aidsendMessage(DISMISS_ALERT_DIALOG_TYPE, null);
        }catch(Throwable e){
            Logger.e(e,TAG);
        }
    }


    public void sendHttpGet(String url,HashMap<String,String> map,HashMap<String,String> headers, final Type type,final int refreshType){
        try{
            new OkHttpRequest.Builder().url(url).params(map).headers(headers).get(new ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {
                    Logger.e(e, e.getMessage() + "===" + e.getLocalizedMessage());
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
            Logger.e(e,TAG);
        }
    }


    public void sendHttpPost(String url,HashMap<String,String> map,HashMap<String,String> headers,final Type type,final int refreshType){

        try{
            new OkHttpRequest.Builder().url(url).params(map).headers(headers).post(new ResultCallback<String>() {
                @Override
                public void onError(Request request, Exception e) {
                    Logger.e(TAG, e.getMessage() + "===" + e.getLocalizedMessage());

                    if (e instanceof UnknownHostException) {
                        aidsendMessage(NETWORK_EXCEPTION_TYPE, "当前网络不可用，请检查网络再试");
                    } else if (e instanceof SocketTimeoutException) {
                        aidsendMessage(NETWORK_EXCEPTION_TYPE, "请求超时，请稍后再试");
                    } else {
                        aidsendMessage(NETWORK_EXCEPTION_TYPE, "网络异常，请稍后再试");
                    }

                    //将错误信息返回上一层  上层可以不处理该回调的结果
                    ResponseBean responseBean = new ResponseBean();
                    responseBean.refreshType = refreshType;
                    responseBean.obj = e;
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
            Logger.e(e,TAG);
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
            Logger.e(e,TAG);
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
                            Logger.e(e,request.toString());
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
                                Logger.e(TAG,e);
                            }



                        }
                        @Override
                        public void inProgress(float progress) {
                            super.inProgress(progress);
                            try{
                                Logger.i(TAG+"progress==>"+progress);
                                //showProgressDialog("正在上传"+progress*100+"%");

                            }catch (Throwable e){
                                Logger.e(TAG,e);
                                Logger.e(TAG,e);
                            }

                        }
                    });

        }catch (Throwable e){
            Logger.e(e,TAG);
        }
    }



    @Override
    public void onRefresh(int refreshtype, Object obj) {
        aidsendMessage(refreshtype,obj);
    }

    @Override
    public void onClick(View v) {
        aidsendMessage(v.getId(),v);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int type,Object obj) {
        if (mListener != null) {
            mListener.onFragmentInteraction(type,obj);
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RefWatcher refWatcher = LibApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int type, Object obj);
    }



    public void transfer(Class<?> clazz) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            startActivityForResult( intent, 0 );
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void transfer(Class<?> clazz, int requestCode) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            startActivityForResult( intent, requestCode );
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void transfer(Class<?> clazz,String params, Serializable obj) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            intent.putExtra( params, obj );
            startActivityForResult( intent, 0 );
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void transfer(Class<?> clazz, String params, Serializable obj, Bundle options) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            intent.putExtra( params, obj );
            startActivityForResult( intent, 0 ,options);
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void transfer(Class<?> clazz, String params, Serializable obj, int requestCode) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            intent.putExtra( params, obj );
            startActivityForResult( intent, requestCode );
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void transfer(Class<?> clazz, String params,Serializable obj, String params1, Serializable obj1) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            intent.putExtra( params, obj );
            intent.putExtra( params1, obj1 );
            startActivityForResult( intent, 0 );
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void transfer(Class<?> clazz, String params,String obj,  String params1, Serializable obj1, int requestCode) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            intent.putExtra( params, obj );
            intent.putExtra( params1, obj1 );
            startActivityForResult( intent, requestCode );
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }


    public void transfer(Class<?> clazz, String params,Serializable obj, String params1, Serializable obj1, String params2, Serializable obj2) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            intent.putExtra( params, obj );
            intent.putExtra( params1, obj1 );
            intent.putExtra( params2, obj2 );
            startActivityForResult( intent, 0 );
        } catch (Throwable e) {
            Logger.e(e,TAG);
        }
    }

    public void transfer(Class<?> clazz, String params,Serializable obj, String params1, Serializable obj1, String params2, Serializable obj2,int requestCode) {
        try {
            Intent intent = new Intent( getActivity(), clazz );
            intent.putExtra( params, obj );
            intent.putExtra( params1, obj1 );
            intent.putExtra( params2, obj2 );
            startActivityForResult( intent,requestCode );
        } catch (Throwable e) {
            Logger.e(e,TAG);
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



}
