package org.ionchain.wallet.db;

import com.fast.lib.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;
import org.ionchain.wallet.comm.api.model.Wallet;
import org.ionchain.wallet.greendao.gen.WalletDao;

import java.util.List;

public class WalletDaoTools {

    public static Wallet getWalletByName(String name) {
        Wallet wallet = null;
        List<Wallet> list = EntityManager.getInstance().getWalletDao().queryBuilder().where(WalletDao.Properties.Name.eq(name)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }
    public static Wallet getWalletByAddress(String adress) {
        Wallet wallet = null;
        List<Wallet> list = EntityManager.getInstance().getWalletDao().queryBuilder().where(WalletDao.Properties.Address.eq(adress)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }


    /**
     * 查询 所有的钱包
     */
    public static List<Wallet> getAllWallet() {
        List<Wallet> walletList = null;
        try {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<Wallet> qb = EntityManager.getInstance().getWalletDao().queryBuilder();
            walletList = qb.list();
        } catch (Throwable e) {
            Logger.e(e, "getAllWallet");
        }
        return walletList;

    }


    /**
     * 更新数据
     */
    public static void updateWallet(Wallet wallet) {

        try {
            EntityManager.getInstance().getWalletDao().update(wallet);
        } catch (Throwable e) {
            Logger.e(e, "getAllWallet");
        }
    }

    public static Wallet getWalletByPrivateKey(String priavtekey) {
        Wallet wallet = null;
        List<Wallet> list = EntityManager.getInstance().getWalletDao().queryBuilder().where(WalletDao.Properties.PrivateKey.eq(priavtekey)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }

    public static long saveWallet(Wallet wallet) {
        //私钥不存储于数据库中
        wallet.setPrivateKey(wallet.getPrivateKey());
        wallet.setPassword(wallet.getPassword());
        long id = EntityManager.getInstance().getWalletDao().insertOrReplace(wallet);
        return id;
    }

    public static void deleteWallet(Long id) {
        //私钥不存储于数据库中
        EntityManager.getInstance().getWalletDao().deleteByKey(id);
    }

    public static void deleteWallet(Wallet wallet) {
        //私钥不存储于数据库中
        EntityManager.getInstance().getWalletDao().delete(wallet);
    }

    //获取最新的 最老的钱包
    public static Wallet getWalletTop() {
        //私钥不存储于数据库中
        Wallet wallet = null;
        List<Wallet> list = EntityManager.getInstance().getWalletDao().queryBuilder().orderAsc(WalletDao.Properties.Id).limit(1).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }
}
