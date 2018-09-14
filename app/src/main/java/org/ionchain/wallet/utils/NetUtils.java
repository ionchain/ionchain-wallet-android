package org.ionchain.wallet.utils;

import com.fast.lib.logger.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;

import org.json.JSONObject;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述:
 */
public class NetUtils {
    /**
     * get请求获取数据
     *
     * @param url
     */
    public static void get(String url, HttpParams params, StringCallback callback, Object cancelTag) {
        OkGo.<String>get(url)
                .params(params)// 请求方式和请求url
                .tag(cancelTag)// 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(callback);
    }

    /**
     * get请求获取数据
     *
     * @param url
     */
    public static void post(String url, HttpParams params, StringCallback callback, Object cancelTag) {
        OkGo.<String>post(url)                            // 请求方式和请求url
                .tag(cancelTag)// 请求的 tag, 主要用于取消对应的请求
                .params(params)
                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(callback);
    }

    /**
     * get请求获取数据
     *
     * @param url
     * @param json
     * @param callback
     * @param cancelTag
     */
    public static void post(String url, JSONObject json, StringCallback callback, Object cancelTag) {
        OkGo.<String>post(url)                            // 请求方式和请求url
                .tag(cancelTag)// 请求的 tag, 主要用于取消对应的请求
                .upJson(json)
                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(callback);
    }

    /**
     * 转成bean
     *
     * @param jsonStr json
     * @param cls     Bean
     * @return bean
     */
    public static <T> T gsonToBean(String jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(jsonStr, cls);
            return t;
        } catch (JsonSyntaxException e) {
            Logger.e(e.getMessage());
            return null;
        }
    }
}
