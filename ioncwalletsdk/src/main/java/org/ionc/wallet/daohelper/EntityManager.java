package org.ionc.wallet.daohelper;


import org.ionc.wallet.greendaogen.DaoSession;
import org.ionc.wallet.greendaogen.TxRecordBeanAllHelperDao;
import org.ionc.wallet.greendaogen.TxRecordBeanDao;
import org.ionc.wallet.greendaogen.TxRecordBeanInHelperDao;
import org.ionc.wallet.greendaogen.TxRecordBeanOutHelperDao;
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

    /**
     * 创建 TxRecordBeanAllHelper 表实例
     * old table
     *
     * @param daoSession
     * @return
     */
    public TxRecordBeanAllHelperDao getTxRecordBeanAllHelperDao(DaoSession daoSession) {
        return daoSession.getTxRecordBeanAllHelperDao();
    }   /**
     * 创建 TxRecordBeanAllHelper 表实例
     * old table
     *
     * @param daoSession
     * @return
     */
    public TxRecordBeanInHelperDao getTxRecordBeanInHelperDao(DaoSession daoSession) {
        return daoSession.getTxRecordBeanInHelperDao();
    }

    /**
     * 创建 TxRecordBeanAllHelper 表实例
     * old table
     *
     * @param daoSession
     * @return
     */
    public TxRecordBeanOutHelperDao getTxRecordBeanOutHelperDao(DaoSession daoSession) {
        return daoSession.getTxRecordBeanOutHelperDao();
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
