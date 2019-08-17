package org.ionc.ionclib.web3j;

import android.content.Context;
import android.os.Handler;

import org.ionc.ionclib.db.greendaogen.DaoSession;
import org.ionc.ionclib.utils.LoggerUtils;

import java.io.File;

public class IONCSDK {
    private static DaoSession sDaoSession;
    private static Context sAppContext;
    private static Handler sHandler;
    private static String sKeystoreDir;
    /**
     * 初始化钱包
     */
    public static void init(Context context, DaoSession daoSession) {
        sDaoSession = daoSession;
        sAppContext = context.getApplicationContext();
        sHandler = new Handler(sAppContext.getMainLooper());
        sKeystoreDir = sAppContext.getCacheDir().getPath() + "/ionchain/keystore";
        //创建keystore路径
        File file = new File(sKeystoreDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        LoggerUtils.i("文件创建成功file =" + file.getPath());
    }

    public static Handler getHandler() {
        return sHandler;
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getKeystoreDir() {
        return sKeystoreDir;
    }
}
