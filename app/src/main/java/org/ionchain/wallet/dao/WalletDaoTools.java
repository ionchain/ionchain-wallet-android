package org.ionchain.wallet.dao;


import com.orhanobut.logger.Logger;

import org.greenrobot.greendao.query.QueryBuilder;
import org.ionchain.wallet.bean.WalletBean;
import org.ionchain.wallet.greendao.gen.WalletBeanDao;

import java.util.List;

public class WalletDaoTools {

    public static WalletBean getWalletByName(String name) {
        WalletBean wallet = null;
        List<WalletBean> list = DaoManager.getInstance()
                .getSession()
                .getWalletBeanDao()
                .queryBuilder()
                .where(WalletBeanDao.Properties.Name.eq(name))
                .list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }
    public static WalletBean getWalletById(int  id) {
        WalletBean wallet = null;
        List<WalletBean> list = DaoManager.getInstance().getSession().getWalletBeanDao().queryBuilder().where(WalletBeanDao.Properties.Id.eq(id)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }

    public static WalletBean getWalletByAddress(String adress) {
        WalletBean wallet = null;
        List<WalletBean> list = DaoManager.getInstance().getSession().getWalletBeanDao().queryBuilder().where(WalletBeanDao.Properties.Address.eq(adress)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }


    /**
     * 查询 所有的钱包
     */
    public static List<WalletBean> getAllWallet() {
        List<WalletBean> walletList = null;
        try {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<WalletBean> qb = EntityManager.getInstance().getWalletDao().queryBuilder();
            walletList = qb.orderDesc(WalletBeanDao.Properties.Id).list();
//            walletList.add(0,getWalletTop());//将第一个添加到首位
//            walletList.remove(walletList.size()-1);
        } catch (Throwable e) {
            Logger.e(e, "getAllWallet");
        }
        return walletList;

    }


    /**
     * 更新数据
     */
    public static void updateWallet(WalletBean wallet) {

        try {
            EntityManager.getInstance().getWalletDao().update(wallet);
        } catch (Throwable e) {
            Logger.e(e, "getAllWallet");
        }
    }

    public static WalletBean getWalletByPrivateKey(String priavtekey) {
        WalletBean wallet = null;
        List<WalletBean> list1 = getAllWallet();
        List<WalletBean> list = DaoManager.getInstance().getSession().getWalletBeanDao()
                .queryBuilder()
                .where(WalletBeanDao.Properties.PrivateKey.eq(priavtekey.toLowerCase())).build()
                .list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }

    public static long saveWallet(WalletBean wallet) {
        //私钥不存储于数据库中
        long id = EntityManager.getInstance().getWalletDao().insertOrReplace(wallet);
        return id;
    }

    public static void deleteWallet(Long id) {
        //私钥不存储于数据库中
        EntityManager.getInstance().getWalletDao().deleteByKey(id);
    }

    public static void deleteWallet(WalletBean wallet) {
        //私钥不存储于数据库中
        EntityManager.getInstance().getWalletDao().delete(wallet);
    }

    //获取最新的 最老的钱包
    public static WalletBean getWalletTop() {
        //私钥不存储于数据库中
        WalletBean wallet = null;
        List<WalletBean> list = EntityManager.getInstance().getWalletDao().queryBuilder().limit(1).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }
    //获取最新的 最老的钱包
    public static WalletBean getCurrentWallet(String name) {
        WalletBean wallet = null;
        List<WalletBean> list = DaoManager.getInstance().getSession().getWalletBeanDao().queryBuilder().where(WalletBeanDao.Properties.Name.eq(name)).list();
        if (list.size() > 0) {
            wallet = list.get(0);
        }
        return wallet;
    }
}
