package org.ionchain.wallet.comm.utils;

import android.content.Context;
import android.text.TextUtils;

import com.fast.lib.logger.Logger;
import com.fast.lib.utils.MD5;

import org.ionchain.wallet.comm.constants.Global;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by siberiawolf on 17/5/15.
 */

public class ParamsUtils {

    private static final String TAG = ParamsUtils.class.getName();

    public static void parseParams(Context context, String url, HashMap<String, String> apiParams, HashMap<String,String> headerParams){
        try{





            //生成签名
            TreeMap<String,String> paramMap = new TreeMap<String,String>();

            Set<Map.Entry<String, String>> paramSet = apiParams.entrySet();
            for (Map.Entry<String, String> param : paramSet) {
                paramMap.put(param.getKey(), param.getValue());
            }

            apiParams.clear();
            apiParams.putAll(signTopRequestNew(paramMap));

            Logger.i("请求url==>" + appendParams(url,apiParams));
            Logger.i("请求参数==>" + Global.mGson.toJson(apiParams));

        }catch (Throwable e){
            Logger.e(TAG,e);
        }
    }


    private static String appendParams(String url, Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder();
        if(url.indexOf("?") == -1){
            sb.append(url + "?");
        }else{
            sb.append(url + "&");
        }

        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                try{
                    sb.append(key).append("=").append(params.get(key)).append("&");
                }catch (Throwable e){
                    e.printStackTrace();
                }

            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }



    /**
     * 给请求做MD5签名。  传参 改动过的方法 只适合本应用
     *
     * @param sortedParams
     *            所有字符型的TOP请求参数
     * @return 签名
     * @throws IOException
     */
    public static HashMap<String,String> signTopRequestNew(TreeMap<String,String> sortedParams)
            throws IOException {
        // 第一步：把字典按Key的字母顺序排序,参数使用TreeMap已经完成排序

        Set<Map.Entry<String, String>> paramSet = sortedParams.entrySet();
        HashMap<String,String> paramMap = new HashMap<String,String>();
        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> param : paramSet) {

            if (!TextUtils.isEmpty(param.getKey()) && !TextUtils.isEmpty(param.getValue())) {
                query.append(param.getKey()).append("=").append(param.getValue()).append("&");
                paramMap.put(param.getKey(), param.getValue());
            }
        }

        String querymap = query.toString();
        paramMap.put("sign", MD5.md5(querymap));

        return paramMap;


    }



}
