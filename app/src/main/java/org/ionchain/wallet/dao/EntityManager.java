package org.ionchain.wallet.dao;

import org.ionchain.wallet.greendao.gen.WalletBeanDao;

public class EntityManager {
    private static EntityManager entityManager;

    /**
     * 创建User表实例
     *
     * @return
     */
    public WalletBeanDao getWalletDao(){
        return DaoManager.getInstance().getSession().getWalletBeanDao();
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
