package org.ionchain.wallet.comm.helper;

import android.util.Pair;

import com.fast.lib.logger.Logger;

import org.ionchain.wallet.comm.constants.Global;
import org.ionchain.wallet.utils.ParamsUtils;
import org.ionchain.wallet.ui.comm.BaseActivity;
import org.ionchain.wallet.ui.comm.BaseFragment;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;


public class RequestHelper {
    private static String TAG = "RequestHelper";



    public static void sendHttpPost(BaseActivity activity, String url, HashMap<String, String> map, Type type, int refreshType) {
        try {
            if (map == null)
                map = new HashMap<>();
            HashMap<String, String> headerMap = new HashMap<>();
            ParamsUtils.parseParams(activity, url, map, headerMap);
            activity.sendHttpPost(url, map, headerMap, type, refreshType);
        } catch (Throwable e) {
            Logger.e(TAG, "sendDdkHttpPost", e);
        }
    }

    public static void sendHttpPostJson(BaseActivity activity, String url, HashMap<String, String> map, Type type, int refreshType) {
        try {
            if (map == null)
                map = new HashMap<>();
            HashMap<String, String> headerMap = new HashMap<>();
            ParamsUtils.parseParams(activity, url, map, headerMap);

            activity.sendHttpPost(url, Global.mGson.toJson(map), headerMap, type, refreshType);
        } catch (Throwable e) {
            Logger.e(TAG, "sendDdkHttpPost", e);
        }
    }
    public static void sendHttpPostJson(BaseFragment activity, String url, HashMap<String, String> map, Type type, int refreshType) {
        try {
            if (map == null)
                map = new HashMap<>();
            HashMap<String, String> headerMap = new HashMap<>();
            ParamsUtils.parseParams(activity.getContext(), url, map, headerMap);

            activity.sendHttpPost(url, Global.mGson.toJson(map), headerMap, type, refreshType);
        } catch (Throwable e) {
            Logger.e(TAG, "sendDdkHttpPost", e);
        }
    }

    public static void sendHttpPost(BaseFragment fragment, String url, HashMap<String,String> map, Type type, int refreshType){
        try{
            if(map == null)
                map = new HashMap<>();
            HashMap<String,String> headerMap = new HashMap<>();
            ParamsUtils.parseParams(fragment.getActivity(),url,map,headerMap);
            fragment.sendHttpPost(url,map,headerMap,type,refreshType);

        }catch (Throwable e){
            Logger.e(TAG, "sendDdkHttpPost", e);
        }
    }



    public static void sendHttpGet(BaseActivity activity, String url, HashMap<String, String> map, Type type, int refreshType) {
        try {
            if (map == null)
                map = new HashMap<>();
            HashMap<String, String> headerMap = new HashMap<>();
            ParamsUtils.parseParams(activity, url, map, headerMap);
            activity.sendHttpGet(url, map, headerMap, type, refreshType);
        } catch (Throwable e) {
            Logger.e(e,TAG+"sendTaHttpGet");
        }
    }

    public static void sendHttpGet(BaseFragment fragment, String url, HashMap<String,String> map, Type type, int refreshType){
        try{
            if(map == null)
                map = new HashMap<>();
            HashMap<String,String> headerMap = new HashMap<>();
            ParamsUtils.parseParams(fragment.getActivity(),url,map,headerMap);
            fragment.sendHttpGet(url,map,headerMap,type,refreshType);

        }catch (Throwable e){
            Logger.e(e,TAG+"sendTaHttpGet");
        }
    }

    public static void sendHttpUpload(BaseActivity activity, String url, HashMap<String,String> map, Type type, int refreshType, Pair<String, File >... files){
        try{
            if(map == null)
                map = new HashMap<>();
            HashMap<String,String> headerMap = new HashMap<>();
            ParamsUtils.parseParams(activity,url,map,headerMap);

            activity.sendHttpUpload(url,map,type,refreshType,files);

        }catch (Throwable e){
            Logger.e(TAG, "sendTaHttpGet", e);
        }
    }


}
