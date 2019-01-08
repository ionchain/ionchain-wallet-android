package com.ionc.wallet.sdk.dao;


import com.ionc.wallet.sdk.IONCWalletSDK;
import com.ionc.wallet.sdk.greendaogen.DaoMaster;
import com.ionc.wallet.sdk.greendaogen.DaoSession;

import static com.ionc.wallet.sdk.constant.ConstanParams.DB_NAME;

public class DaoManager {
    private static DaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private DaoManager() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(IONCWalletSDK.AppContext, DB_NAME, null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public static DaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new DaoManager();
        }
        return mInstance;
    }
}