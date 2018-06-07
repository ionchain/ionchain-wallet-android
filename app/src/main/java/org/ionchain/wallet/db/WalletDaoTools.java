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
        if( list.size()>0 ){
            wallet = list.get(0);
        }
        return wallet;
    }


    /** 查询 所有的钱包 */
    public static List<Wallet> getAllWallet()
    {
        List<Wallet> walletList = null;
        try{
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            QueryBuilder<Wallet> qb = EntityManager.getInstance().getWalletDao().queryBuilder();
            walletList = qb.list();
        }catch (Throwable e){
            Logger.e(e,"getAllWallet");
        }
        return walletList;

    }

    public static Wallet getWalletByPrivateKey(String priavtekey) {
        Wallet wallet = null;
        List<Wallet> list = EntityManager.getInstance().getWalletDao().queryBuilder().where(WalletDao.Properties.PrivateKey.eq(priavtekey)).list();
        if( list.size()>0 ){
            wallet = list.get(0);
        }
        return wallet;
    }

    public static long saveWallet(Wallet wallet) {
       long id = EntityManager.getInstance().getWalletDao().insertOrReplace(wallet);
       return id;
    }
    
}
