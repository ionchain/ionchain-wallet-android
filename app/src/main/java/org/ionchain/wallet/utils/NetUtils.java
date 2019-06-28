package org.ionchain.wallet.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.App;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.R;
import org.ionchain.wallet.widget.dialog.version.VersionInfoDialog;
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
     * @param cacheKey
     * @param url
     */
    public static void get(String cacheKey, String url, HttpParams params, StringCallback callback, Object cancelTag) {
        OkGo.<String>get(url)
                .params(params)// 请求方式和请求url
                .tag(cancelTag)// 请求的 tag, 主要用于取消对应的请求
                .cacheKey(cacheKey)            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(callback);
    }

    /**
     * get请求获取数据
     *
     * @param cacheKey
     * @param url
     */
    public static void get(String cacheKey, String url, StringCallback callback, Object cancelTag) {
        OkGo.<String>get(url)
                .tag(cancelTag)// 请求的 tag, 主要用于取消对应的请求
                .cacheKey(cacheKey)            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(callback);
    }



    /**
     * post 请求获取数据
     *
     * @param cacheKey
     * @param url
     */
    public static void post(String cacheKey, String url, HttpParams params, StringCallback callback, Object cancelTag) {
        OkGo.<String>post(url)                            // 请求方式和请求url
                .tag(cancelTag)// 请求的 tag, 主要用于取消对应的请求
                .params(params)
                .cacheKey(cacheKey)            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
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
            LoggerUtils.e(e.getMessage());
            return null;
        }
    }

    /**
     * @param url              下载的URL
     * @param downloadCallback 下载回调
     * @return 下载任务
     */
    public static DownloadTask downloadTask(String url, final DownloadCallback downloadCallback) {
        GetRequest<File> request = OkGo.<File>get(url);
        DownloadTask task = OkDownload.request("task_download_apk", request)
                .save()
                .register(new DownloadListener("apk_download") {
                    @Override
                    public void onStart(Progress progress) {
                        downloadCallback.onDownloadStart(progress);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        downloadCallback.onDownloadProgress(progress);
                    }

                    @Override
                    public void onError(Progress progress) {
                        downloadCallback.onDownloadError(progress);
                    }

                    @Override
                    public void onFinish(File file, Progress progress) {
                        downloadCallback.onDownloadFinish(progress);
                    }

                    @Override
                    public void onRemove(Progress progress) {
                    }
                });
        return task;
    }

    public interface DownloadCallback {
        void onDownloadStart(Progress progress);

        void onDownloadProgress(Progress progress);

        void onDownloadError(Progress progress);

        void onDownloadFinish(Progress progress);

//        void onDownloadRemove(Progress progress);
    }

    public static void appInstaller(Activity activity, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            /* Android N 写法*/
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            LoggerUtils.i("appid ", BuildConfig.APPLICATION_ID);
            Uri contentUri = FileProvider.getUriForFile(App.mAppInstance, BuildConfig.APPLICATION_ID + ".provider", new File(path));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            /* Android N之前的老版本写法*/
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
    }

    /**
     * @param activity 上下文
     * @param url
     * @param msg      内容
     * @param new_code 版本号
     * @param listener 按钮事件
     */
    public static void downloadShowDialog(Activity activity, String url, String msg, String new_code, VersionInfoDialog.OnVersionDialogBtnClickedListener listener) {
        VersionInfoDialog versionInfoDialog = new VersionInfoDialog(activity, url, listener);
        versionInfoDialog.setTitleName(activity.getString(R.string.update_msg_title, new_code));
        versionInfoDialog.setSureBtnName(activity.getString(R.string.dialog_btn_download));
        versionInfoDialog.setVersionInfo(msg);
        versionInfoDialog.setCancelable(false);
        versionInfoDialog.show();
    }
}
