package org.sdk.wallet.daohelper;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;
import org.sdk.wallet.utils.LoggerUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用于数据库升级时数据迁移
 * from https://github.com/yuweiguocn/GreenDaoUpgradeHelper
 * <p>
 * please call {@link #migrate(SQLiteDatabase, Class[])} or {@link #migrate(Database, Class[])}
 */
public class MigrationHelper {

    public static boolean DEBUG = false;
    private static String TAG = "MigrationHelper";
    private static final String SQLITE_MASTER = "sqlite_master";
    private static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";

    private static WeakReference<ReCreateAllTableListener> weakListener;

    public interface ReCreateAllTableListener {
        void onCreateAllTables(Database db, boolean ifNotExists);

        void onDropAllTables(Database db, boolean ifExists);
    }

    public static void migrate(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        printLog("【The Old Database Version】" + db.getVersion());
        Database database = new StandardDatabase(db);
        migrate(database, daoClasses);
    }

    public static void migrate(SQLiteDatabase db, ReCreateAllTableListener listener, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        weakListener = new WeakReference<>(listener);
        migrate(db, daoClasses);
    }

    public static void migrate(Database database, ReCreateAllTableListener listener, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        weakListener = new WeakReference<>(listener);
        migrate(database, daoClasses);
    }

    public static void migrate(Database database, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        printLog("【Generate temp table】start");
        generateTempTables(database, daoClasses);
        printLog("【Generate temp table】complete");

        ReCreateAllTableListener listener = null;
        if (weakListener != null) {
            listener = weakListener.get();
        }

        if (listener != null) {
            //走监听器
            listener.onDropAllTables(database, true);
            printLog("【Drop all table by listener】");
            listener.onCreateAllTables(database, false);
            printLog("【Create all table by listener】");
        } else {
            dropAllTables(database, true, daoClasses);
            createAllTables(database, false, daoClasses);
        }
        printLog("【Restore data】start");
        restoreData(database, daoClasses);
        printLog("【Restore data】complete");
    }

    private static void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {
            String tempTableName = null;

            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            //获取表的名字
            String tableName = daoConfig.tablename;

            if (!isTableExists(db, false, tableName)) {
                printLog("【New Table】" + tableName); //新表
                continue;
            }
            try {
                //创建临时表
                tempTableName = daoConfig.tablename.concat("_TEMP");
                StringBuilder dropTableStringBuilder = new StringBuilder();
                //临时表如果存在，删除
                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");
                db.execSQL(dropTableStringBuilder.toString());
                //创建一个新的临时表
                StringBuilder insertTableStringBuilder = new StringBuilder();

                //创建表语句
                insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
                //复制旧表中的数据
                insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
                printLog("【Table】" + tableName +"\n ---Columns-->"+getColumnsStr(daoConfig));
                printLog("【Generate temp table】" + tempTableName);
            } catch (SQLException e) {
                Log.e(TAG, "【Failed to generate temp table】" + tempTableName, e);
            }
        }
    }

    /**
     * 判断表是否存在
     *
     * @param db        数据库
     * @param isTemp    是否是缓存
     * @param tableName 表名字
     * @return 是否存在
     */
    private static boolean isTableExists(Database db, boolean isTemp, String tableName) {
        if (db == null || TextUtils.isEmpty(tableName)) {
            return false;
        }
        String dbName = isTemp ? SQLITE_TEMP_MASTER : SQLITE_MASTER;
        String sql = "SELECT COUNT(*) FROM " + dbName + " WHERE type = ? AND name = ?";
        Cursor cursor = null;
        int count = 0;
        try {

            //判断表是否为空
            cursor = db.rawQuery(sql, new String[]{"table", tableName});
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count > 0;
    }


    private static String getColumnsStr(DaoConfig daoConfig) {
        if (daoConfig == null) {
            return "no columns";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < daoConfig.allColumns.length; i++) {
            builder.append(daoConfig.allColumns[i]);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }


    private static void dropAllTables(Database db, boolean ifExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "dropTable", ifExists, daoClasses);
        printLog("【Drop all table by reflect】");
    }

    private static void createAllTables(Database db, boolean ifNotExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        reflectMethod(db, "createTable", ifNotExists, daoClasses);
        printLog("【Create all table by reflect】");
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private static void reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {
        if (daoClasses.length < 1) {
            return;
        }
        try {
            for (Class cls : daoClasses) {
                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
        for (int i = 0; i < daoClasses.length; i++) {

            DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
            String tempTableName = daoConfig.tablename.concat("_TEMP");

            if (!isTableExists(db, true, tempTableName)) {
                continue;
            }
            String tableName = daoConfig.tablename;

            try {
                // get all columns from tempTable, take careful to use the columns list

                //新表的字段
                List<ColumnInfo> columnInfoList = ColumnInfo.getColuns(db, tableName);
                //旧表的字段
                List<ColumnInfo> tempColumnInfoList = ColumnInfo.getColuns(db, tempTableName);

                ArrayList<String> selectColumns = new ArrayList<>(columnInfoList.size());
                ArrayList<String> intoColumns = new ArrayList<>(columnInfoList.size());

                for (ColumnInfo oldColumnInfo : tempColumnInfoList) {
                    if (columnInfoList.contains(oldColumnInfo)) {
                        String column = '`' + oldColumnInfo.name + '`';
                        intoColumns.add(column);
                        selectColumns.add(column);
                    }
                }
                // NOT NULL columns list
                for (ColumnInfo columnInfo : columnInfoList) {
                    if (columnInfo.notnull && !tempColumnInfoList.contains(columnInfo)) {
                        String column = '`' + columnInfo.name + '`';
                        intoColumns.add(column);

                        String value;
                        if (columnInfo.dfltValue != null) {
                            value = "'" + columnInfo.dfltValue + "' AS ";
                        } else {
                            value = "'' AS ";
                        }
                        selectColumns.add(value + column);
                    }
                }

                if (intoColumns.size() != 0) {
                    StringBuilder insertTableStringBuilder = new StringBuilder();
                    insertTableStringBuilder.append("REPLACE INTO ").append(tableName).append(" (");
                    insertTableStringBuilder.append(TextUtils.join(",", intoColumns));
                    insertTableStringBuilder.append(") SELECT ");
                    insertTableStringBuilder.append(TextUtils.join(",", selectColumns));
                    insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                    db.execSQL(insertTableStringBuilder.toString());
                    printLog("【Restore data】 to " + tableName);
                }
                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
                db.execSQL(dropTableStringBuilder.toString());
                printLog("【Drop temp table】" + tempTableName);
            } catch (SQLException e) {
                LoggerUtils.e(TAG, "【Failed to restore data from temp table 】" + tempTableName + e.getMessage());
            }
        }
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new ArrayList<>();
        }
        return columns;
    }

    private static void printLog(String info) {
        LoggerUtils.i(TAG, info);
    }

    /**
     * 列的属性 ,如字段的类型、长度、是否为空
     */
    private static class ColumnInfo {
        int cid;
        String name;
        String type;
        boolean notnull;
        String dfltValue;
        boolean pk;

        @Override
        public boolean equals(Object o) {
            return this == o
                    || o != null
                    && getClass() == o.getClass()
                    && name.equals(((ColumnInfo) o).name);
        }

        @Override
        public String toString() {
            return "ColumnInfo{" +
                    "cid=" + cid +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", notnull=" + notnull +
                    ", dfltValue='" + dfltValue + '\'' +
                    ", pk=" + pk +
                    '}';
        }

        private static List<ColumnInfo> getColuns(Database db, String tableName) {
            String sql = "PRAGMA table_info(" + tableName + ")";
            printLog(sql);
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor == null)
                return new ArrayList<>();
            ColumnInfo columnInfo;
            List<ColumnInfo> columnInfos = new ArrayList<>();
            while (cursor.moveToNext()) {
                columnInfo = new ColumnInfo();
                columnInfo.cid = cursor.getInt(0);
                columnInfo.name = cursor.getString(1);
                columnInfo.type = cursor.getString(2);
                columnInfo.notnull = cursor.getInt(3) == 1;
                columnInfo.dfltValue = cursor.getString(4);
                columnInfo.pk = cursor.getInt(5) == 1;
                columnInfos.add(columnInfo);
                // printLog(tableName + "：" + columnInfo);
            }
            cursor.close();
            return columnInfos;
        }
    }
}
