package org.ionc.wallet.daohelper;


import org.ionc.wallet.greendaogen.DaoSession;
import org.ionc.wallet.greendaogen.WalletBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanNewDao;

public class EntityManager {
    private static EntityManager entityManager;

    /**
     * 创建 WalletBeanNew 表实例
     *
     * @return
     * @param daoSession session
     */
    public WalletBeanNewDao getWalletDaoNew(DaoSession daoSession) {
        
        return daoSession.getWalletBeanNewDao();
    }

    /**
     * 创建 WalletBean 表实例
     *   old table
     * @return
     * @param daoSession
     */
    public WalletBeanDao getWalletDaoOld(DaoSession daoSession) {
        return daoSession.getWalletBeanDao();
    }

    /**
     * 创建单例
     *
     * @return
     */
    public static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }
}
