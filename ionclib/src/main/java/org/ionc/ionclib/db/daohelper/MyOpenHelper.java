package org.ionc.ionclib.db.daohelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.ionc.ionclib.db.greendaogen.DaoMaster;
import org.ionc.ionclib.db.greendaogen.TxRecordBeanDao;
import org.ionc.ionclib.db.greendaogen.WalletBeanNewDao;
import org.ionc.ionclib.utils.LoggerUtils;

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
                TxRecordBeanDao.createTable(db, ifNotExists);
//                WalletBeanDao.createTable(db, ifNotExists);
                WalletBeanNewDao.createTable(db, ifNotExists);
            }
 
            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                LoggerUtils.i("dbtest",db.toString());
                DaoMaster.dropAllTables(db, ifExists);
            }
        },  WalletBeanNewDao.class, TxRecordBeanDao.class);
    }
}
