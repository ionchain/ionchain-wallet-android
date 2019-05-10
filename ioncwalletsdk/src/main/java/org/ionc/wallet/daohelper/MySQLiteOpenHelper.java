package org.ionc.wallet.daohelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.ionc.wallet.greendaogen.DaoMaster;
import org.ionc.wallet.greendaogen.WalletBeanDao;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    /**
     * @param context 上下文
     * @param name    原来定义的数据库的名字   新旧数据库一致
     * @param factory 可以null
     */
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion 更新数据库的时候自己调用
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.d("flag", "-----调用了");
        //具体的数据转移在MigrationHelper2类中
        /**
         *  将db传入     将gen目录下的所有的Dao.类传入
         */
       MigrationHelper2.migrate(db, WalletBeanDao.class);
    }
}
