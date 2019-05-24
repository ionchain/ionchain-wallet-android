package org.ionc.wallet.daohelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.ionc.wallet.greendaogen.DaoMaster;
import org.ionc.wallet.greendaogen.NodeBeanLocalDao;
import org.ionc.wallet.greendaogen.WalletBeanDao;
import org.ionc.wallet.greendaogen.WalletBeanNewDao;

public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
 
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
 
        //把需要管理的数据库表DAO作为最后一个参数传入到方法中
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
 
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }
 
            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        },  WalletBeanNewDao.class, WalletBeanDao.class, NodeBeanLocalDao.class);
    }
}
