package org.ionchain.wallet.db;

import org.ionchain.wallet.greendao.gen.WalletDao;

public class EntityManager {
    private static EntityManager entityManager;

    /**
     * 创建User表实例
     *
     * @return
     */
    public WalletDao getWalletDao(){
        return DaoManager.getInstance().getSession().getWalletDao();
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
