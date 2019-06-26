package org.ionc.wallet.daohelper;


import org.ionc.wallet.greendaogen.CurrentPageNumDao;
import org.ionc.wallet.greendaogen.DaoSession;
import org.ionc.wallet.greendaogen.TxRecordBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanNewDao;

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

    public CurrentPageNumDao getCurrentPageNumDao(DaoSession daoSession) {
        return daoSession.getCurrentPageNumDao();
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
