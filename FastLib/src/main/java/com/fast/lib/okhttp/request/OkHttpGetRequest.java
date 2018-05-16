package com.fast.lib.okhttp.request;

import android.text.TextUtils;


import com.fast.lib.logger.Logger;

import java.net.URLEncoder;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpGetRequest extends OkHttpRequest
{
    protected OkHttpGetRequest(String url, String tag, Map<String, String> params, Map<String, String> headers)
    {
        super(url, tag, params, headers);
    }


    @Override
    protected Request buildRequest()
    {
        if (TextUtils.isEmpty(url))
        {
            throw new IllegalArgumentException("url can not be empty!");
        }
        //append params , if necessary
        url = appendParams(url, params);
        Request.Builder builder = new Request.Builder();
        //add headers , if necessary
        appendHeaders(builder,headers);
        builder.url(url).tag(tag);
        Logger.i("url===>"+url);
        return builder.build();
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return null;
    }


    private String appendParams(String url, Map<String, String> params)
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
                    sb.append(key).append("=").append(URLEncoder.encode(params.get(key),"UTF-8")).append("&");
                }catch (Throwable e){
                    e.printStackTrace();
                }

            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
