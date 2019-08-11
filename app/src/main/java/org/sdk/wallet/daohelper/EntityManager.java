package org.sdk.wallet.daohelper;


import org.sdk.wallet.greendaogen.DaoSession;
import org.sdk.wallet.greendaogen.TxRecordBeanDao;
import org.sdk.wallet.greendaogen.WalletBeanDao;
import org.sdk.wallet.greendaogen.WalletBeanNewDao;

public class EntityManager {
    private static EntityManager entityManager;

    /**
     * 创建 交易记录 表实例
     *
     * @param daoSession session
     * @return
     */
    public TxRecordBeanDao getTxRecordBeanDao(DaoSession daoSession) {

        return daoSession.getTxRecordBeanDao();
    }

    /**
     * 创建 WalletBeanNew 表实例
     *
     * @param daoSession session
     * @return
     */
    public WalletBeanNewDao getWalletDaoNew(DaoSession daoSession) {

        return daoSession.getWalletBeanNewDao();
    }

    /**
     * 创建 WalletBean 表实例
     * old table
     *
     * @param daoSession
     * @return
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
