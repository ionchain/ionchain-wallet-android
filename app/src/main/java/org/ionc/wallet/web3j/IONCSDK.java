package org.ionc.wallet.web3j;

import android.content.Context;
import android.os.Handler;

import org.ionc.wallet.db.greendaogen.DaoSession;
import org.ionc.wallet.utils.LoggerUtils;

import java.io.File;

public class IONCSDK {
    public static DaoSession mDaoSession;
    public static Context appContext;
    public static Handler mHandler;
    public static String keystoreDir;
    /**
     * 初始化钱包
     */
    public static void init(Context context, DaoSession daoSession) {
        mDaoSession = daoSession;
        appContext = context.getApplicationContext();
        mHandler = new Handler(appContext.getMainLooper());
        keystoreDir = appContext.getCacheDir().getPath() + "/ionchain/keystore";
        //创建keystore路径
        File file = new File(keystoreDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        LoggerUtils.i("文件创建成功file =" + file.getPath());
    }
}
