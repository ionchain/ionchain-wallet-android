package org.ionc.wallet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.core.content.SharedPreferencesCompat;

import java.util.Locale;

import static org.ionc.wallet.constant.ConstantCoinType.COIN_TYPE_USD;

/**
 * 首选项工具类
 *
 * @author wkp111
 */
public class SPUtils {
    private final String TAG_LANGUAGE_KEY = "language_select";
    private final String COIN_TYPE_KEY = "COIN_TYPE_KEY";
    private static SharedPreferences mSharedPreferences;
    private static volatile SPUtils instance;
    private Locale systemCurrentLocal = Locale.getDefault();

    private static Context mContext;

    /**
     * 获取首选项
     *
     * @param c
     * @return SharedPreferences sp
     */

    public static void initSP(Context c) {
        mContext = c;
    }

    private static SharedPreferences getSP() {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences("ionc_wallet_config", Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    private SPUtils() {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences("ionc_wallet_config", Context.MODE_PRIVATE);
        }
    }

    public static SPUtils getInstance() {
        if (instance == null) {
            synchronized (SPUtils.class) {
                if (instance == null) {
                    instance = new SPUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 获取首选项中String类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(String)
     */
    public  String getString(String key, String defaultValue) {
        SharedPreferences sp = getSP();
        return sp.getString(key, defaultValue);
    }

    /**
     * 获取首选项中String类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(String)
     */
    public  String getString(String key) {
        SharedPreferences sp = getSP();
        return sp.getString(key, "");
    }

    /**
     * 获取首选项中boolean类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(boolean)
     */
    public  boolean getBoolean(String key) {
        SharedPreferences sp = getSP();
        boolean b = sp.getBoolean(key, false);
        return b;
    }

    /**
     * 获取首选项中boolean类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(boolean)
     */
    public  boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sp = getSP();
        boolean b = sp.getBoolean(key, defaultValue);
        return b;
    }

    /**
     * 获取首选项中int类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(int)
     */
    public static int getInt(String key, int defaultValue) {
        SharedPreferences sp = getSP();
        return sp.getInt(key, defaultValue);
    }

    /**
     * 获取首选项中int类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(int)
     */
    public static int getInt(String key) {
        SharedPreferences sp = getSP();
        return sp.getInt(key, 0);
    }

    /**
     * 获取首选项中long类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(long)
     */
    public  long getLong(String key, int defaultValue) {
        SharedPreferences sp = getSP();
        return sp.getLong(key, defaultValue);
    }

    /**
     * 获取首选项中long类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(long)
     */
    public  long getLong(String key) {
        SharedPreferences sp = getSP();
        return sp.getLong(key, 0);
    }

    /**
     * 获取首选项中float类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(float)
     */
    public  float getFloat(String key, int defaultValue) {
        SharedPreferences sp = getSP();
        return sp.getFloat(key, defaultValue);
    }

    /**
     * 获取首选项中float类型值，对应键值为key
     *
     * @param key
     * @return key对应的值(float)
     */
    public  float getFloat(String key) {
        SharedPreferences sp = getSP();
        return sp.getFloat(key, 0);
    }

    /**
     * 向首选项中编辑数据(键值对)(值可以是String boolean int)
     *
     * @param key
     * @param value
     */
    public  void put(String key, Object value) {
        SharedPreferences sp = getSP();
        Editor editor = sp.edit();

        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (int) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (float) value);
        }

        editor.apply();
    }

    /**
     * 移除key对应值
     *
     * @param key
     */
    public static void remove(String key) {
        SharedPreferences sp = getSP();
        Editor editor = sp.edit().remove(key);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * 清除所有内容
     */
    public static void clear() {
        SharedPreferences sp = getSP();
        Editor editor = sp.edit().clear();
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public void saveLanguage(int select) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(TAG_LANGUAGE_KEY, select);
        edit.apply();
    }

    public int getSelectLanguage() {
        return mSharedPreferences.getInt(TAG_LANGUAGE_KEY, 0);
    }

    public void saveCoinType(String select) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(COIN_TYPE_KEY, select);
        edit.apply();
    }

    public String getCoinType() {
        return mSharedPreferences.getString(COIN_TYPE_KEY, COIN_TYPE_USD);
    }


    public Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }
}
