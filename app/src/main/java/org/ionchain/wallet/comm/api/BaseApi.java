package org.ionchain.wallet.comm.api;


import android.text.TextUtils;

import org.ionchain.wallet.comm.api.conf.ApiConfig;
import org.ionchain.wallet.comm.api.request.ViewParm;
import org.ionchain.wallet.comm.constants.Comm;
import org.ionchain.wallet.comm.helper.RequestHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseApi {

    /**
     * 格式化 请求参数
     *
     * @param pamrs
     */
    public static void formatParms(HashMap<String, String> pamrs) {
        pamrs.put("appkey", ApiConfig.API_APP_KEY);
        pamrs.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(pamrs.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            //升序排序
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> mapping : list) {
            buffer.append(mapping.getKey());
            buffer.append(Comm.IS);
            buffer.append(mapping.getValue());
            buffer.append(Comm.SPLIT);
        }
        buffer.append("appsecret");
        buffer.append(Comm.IS);
        buffer.append(ApiConfig.API_APP_SECRET);
        String sign = md5(buffer.toString());
        pamrs.put("sign", sign);

    }


    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void postJson(String url, HashMap<String, String> map, ViewParm viewParm) {
        //BaseActivity activity, String url, HashMap<String, String> map, Type type, int refreshType
        formatParms(map);
        if (null != viewParm.getBaseActivity())
            RequestHelper.sendHttpPostJson(viewParm.getBaseActivity(), url, map, viewParm.type, viewParm.refreshType);
        else if (null != viewParm.getBaseFragment()) {
            RequestHelper.sendHttpPostJson(viewParm.getBaseFragment(), url, map, viewParm.type, viewParm.refreshType);
        }
    }

}
