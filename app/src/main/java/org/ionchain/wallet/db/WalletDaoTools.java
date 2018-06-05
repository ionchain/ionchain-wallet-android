package org.ionchain.wallet.db;

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

    public static long saveWallet(Wallet wallet) {
       long id = EntityManager.getInstance().getWalletDao().insertOrReplace(wallet);
       return id;
    }
    
}
