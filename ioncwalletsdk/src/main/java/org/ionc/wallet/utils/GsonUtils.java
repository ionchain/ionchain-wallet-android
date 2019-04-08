package org.ionc.wallet.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonUtils {

    /**
     * 转成bean
     *
     * @param jsonStr json
     * @param cls     Bean
     * @return bean
     */
    public static <T> T gsonToBean(String jsonStr, Class<T> cls) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(jsonStr, cls);
            return t;
        } catch (JsonSyntaxException e) {
            Logger.e(e.getMessage());
            return null;
        }
    }
}
