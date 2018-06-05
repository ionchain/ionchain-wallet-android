package org.ionchain.wallet.db;

import org.ionchain.wallet.greendao.gen.WalletDao;

public class EntityManager {
    private static EntityManager entityManager;
    public WalletDao walletDao;

    /**
     * 创建User表实例
     *
     * @return
     */
    public WalletDao getWalletDao(){
        walletDao = DaoManager.getInstance().getSession().getWalletDao();
        return walletDao;
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
