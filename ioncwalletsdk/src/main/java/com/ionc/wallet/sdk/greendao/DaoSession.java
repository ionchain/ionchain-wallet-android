package com.ionc.wallet.sdk.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.ionc.wallet.sdk.bean.WalletBean;

import com.ionc.wallet.sdk.greendao.WalletBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig walletBeanDaoConfig;

    private final WalletBeanDao walletBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        walletBeanDaoConfig = daoConfigMap.get(WalletBeanDao.class).clone();
        walletBeanDaoConfig.initIdentityScope(type);

        walletBeanDao = new WalletBeanDao(walletBeanDaoConfig, this);

        registerDao(WalletBean.class, walletBeanDao);
    }
    
    public void clear() {
        walletBeanDaoConfig.clearIdentityScope();
    }

    public WalletBeanDao getWalletBeanDao() {
        return walletBeanDao;
    }

}