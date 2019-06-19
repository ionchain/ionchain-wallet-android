package org.ionchain.wallet.utils;

import org.ionc.wallet.utils.LoggerUtils;
import org.ionchain.wallet.BuildConfig;
import org.ionchain.wallet.constant.ConstantUrl;

public class UrlUtils {
//    private static boolean AppDebug = false;
    private static boolean AppDebug = true;

    /**
     * @return 获取主链节点
     */
    public static String getNodeListUrl() {
        if (AppDebug) {
            return ConstantUrl.URL_NODE_LIST;
        } else {
            LoggerUtils.i("正式服：主链" + ConstantUrl.URL_NODE_LIST);
            return ConstantUrl.URL_NODE_LIST;
        }

    }

    /**
     * @return 钱包更新接口
     */
    public static String getUpdateApkUrl() {
        if (AppDebug) {
            return ConstantUrl.URL_UPDATE_APK_DEBUG;
        } else {
            LoggerUtils.i("正式服：更新" + ConstantUrl.URL_UPDATE_APK);
            return ConstantUrl.URL_UPDATE_APK;
        }
    }


    /**
     * @return 交易记录接口
     */
    public static String getTxRecordUrl() {
        if (AppDebug) {
            return ConstantUrl.URL_TX_RECORD_GET_DEBUG;
        } else {
            LoggerUtils.i("正式服：记录" + ConstantUrl.URL_TX_RECORD_GET);
            return ConstantUrl.URL_TX_RECORD_GET;
        }
    }

    /**
     * @return 获取美元价格
     */
    public static String getUSDPriceUrl() {
        if (AppDebug) {
            return ConstantUrl.URL_USD_PRICE_DEBUG;
        } else {
            LoggerUtils.i("正式服：美元" + ConstantUrl.URL_USD_PRICE);
            return ConstantUrl.URL_USD_PRICE;
        }
    }

    /**
     * @return 获取美元-人民币汇率信息接口
     */
    public static String getUSDExRateRMBUrl() {
        if (AppDebug) {
            return ConstantUrl.URL_USD_EX_RATE_RMB_PRICE_DEBUG;
        } else {
            LoggerUtils.i("正式服：汇率" + ConstantUrl.URL_USD_EX_RATE_RMB_PRICE);

            return ConstantUrl.URL_USD_EX_RATE_RMB_PRICE;
        }
    }

    /**
     * @return 获取美元-人民币汇率信息接口
     */
    public static String getHostNode() {
        if (BuildConfig.APP_DEBUG) {
            return ConstantUrl.HOST_NODE_MAIN_DEBUG;
        } else {
            LoggerUtils.i("正式服：汇率" + ConstantUrl.URL_USD_EX_RATE_RMB_PRICE);

            return ConstantUrl.HOST_NODE_MAIN;
        }
    }

}
