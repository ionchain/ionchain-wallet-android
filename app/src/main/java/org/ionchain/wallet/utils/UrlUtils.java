package org.ionchain.wallet.utils;

import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.constant.ConstantUrl;

public class UrlUtils {
    private static boolean debug = BuildConfig.DEBUG;
//    private static boolean debug = false;
    /**
     * @return 获取主链节点
     */
    public static String getNodeListUrl() {
        if (debug) {
            return ConstantUrl.URL_NODE_LIST_DEBUG;
        } else {
            return ConstantUrl.URL_NODE_LIST;
        }
        
    }

    /**
     * @return 钱包更新接口
     */
    public static String getUpdateApkUrl() {
        if (debug) {
            return ConstantUrl.URL_UPDATE_APK_DEBUG;
        } else {
            return ConstantUrl.URL_UPDATE_APK;
        }
    }


    /**
     * @return 交易记录接口
     */
    public static String getTxRecordUrl() {
        if (debug) {
            return ConstantUrl.URL_TX_RECORD_GET_DEBUG;
        } else {
            return ConstantUrl.URL_TX_RECORD_GET;
        }
    }

    /**
     * @return 获取美元价格
     */
    public static String getUSDPriceUrl() {
        if (debug) {
            return ConstantUrl.URL_USD_PRICE_DEBUG;
        } else {
            return ConstantUrl.URL_USD_PRICE;
        }
    }

    /**
     * @return 获取美元-人民币汇率信息接口
     */
    public static String getUSDExRateRMBUrl() {
        if (debug) {
            return ConstantUrl.URL_USD_EX_RATE_RMB_PRICE_DEBUG;
        } else {
            return ConstantUrl.URL_USD_EX_RATE_RMB_PRICE;
        }
    }

}
