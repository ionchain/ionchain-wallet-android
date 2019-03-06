package org.ionchain.wallet.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ionc.wallet.sdk.utils.Logger;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import org.json.JSONObject;

import java.io.File;

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
    public static void get(String url, StringCallback callback, Object cancelTag) {
        OkGo.<String>get(url)
                .tag(cancelTag)// 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(callback);
    }

    /**
     * post 请求获取数据
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
     * post 请求获取数据
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
        T t;
        try {
            t = gson.fromJson(jsonStr, cls);
            return t;
        } catch (JsonSyntaxException e) {
            Logger.e(e.getMessage());
            return null;
        }
    }

    /**
     * @param url 下载的URL
     * @param downloadListener 下载监听
     * @return 下载任务
     */
    public static DownloadTask downloader(String url, DownloadListener downloadListener) {
        GetRequest<File> request = OkGo.<File>get(url);
        DownloadTask task = OkDownload.request("task_download_apk", request)
                .save()
                .register(downloadListener);
        return task;
    }
}
