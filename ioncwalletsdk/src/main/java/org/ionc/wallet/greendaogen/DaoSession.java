package org.ionc.wallet.greendaogen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import org.ionc.wallet.bean.WalletBean;
import org.ionc.wallet.bean.WalletBeanNew;
import org.ionc.wallet.bean.NodeBeanLocal;

import org.ionc.wallet.greendaogen.WalletBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanNewDao;
import org.ionc.wallet.greendaogen.NodeBeanLocalDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig walletBeanDaoConfig;
    private final DaoConfig walletBeanNewDaoConfig;
    private final DaoConfig nodeBeanLocalDaoConfig;

    private final WalletBeanDao walletBeanDao;
    private final WalletBeanNewDao walletBeanNewDao;
    private final NodeBeanLocalDao nodeBeanLocalDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        walletBeanDaoConfig = daoConfigMap.get(WalletBeanDao.class).clone();
        walletBeanDaoConfig.initIdentityScope(type);

        walletBeanNewDaoConfig = daoConfigMap.get(WalletBeanNewDao.class).clone();
        walletBeanNewDaoConfig.initIdentityScope(type);

        nodeBeanLocalDaoConfig = daoConfigMap.get(NodeBeanLocalDao.class).clone();
        nodeBeanLocalDaoConfig.initIdentityScope(type);

        walletBeanDao = new WalletBeanDao(walletBeanDaoConfig, this);
        walletBeanNewDao = new WalletBeanNewDao(walletBeanNewDaoConfig, this);
        nodeBeanLocalDao = new NodeBeanLocalDao(nodeBeanLocalDaoConfig, this);

        registerDao(WalletBean.class, walletBeanDao);
        registerDao(WalletBeanNew.class, walletBeanNewDao);
        registerDao(NodeBeanLocal.class, nodeBeanLocalDao);
    }
    
    public void clear() {
        walletBeanDaoConfig.clearIdentityScope();
        walletBeanNewDaoConfig.clearIdentityScope();
        nodeBeanLocalDaoConfig.clearIdentityScope();
    }

    public WalletBeanDao getWalletBeanDao() {
        return walletBeanDao;
    }

    public WalletBeanNewDao getWalletBeanNewDao() {
        return walletBeanNewDao;
    }

    public NodeBeanLocalDao getNodeBeanLocalDao() {
        return nodeBeanLocalDao;
    }

}
