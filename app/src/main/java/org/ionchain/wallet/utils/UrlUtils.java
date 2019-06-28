package org.ionchain.wallet.utils;

import org.ionc.wallet.utils.LoggerUtils;

public class UrlUtils {

    //测试服
    private final static String HOST_DEBUG = "http://192.168.23.142";   //新服
    private final static String URL_TX_RECORD_FROM_GET_DEBUG = HOST_DEBUG + ":3001/v1/transactionFrom";//测试
    private final static String URL_TX_RECORD_TO_GET_DEBUG = HOST_DEBUG + ":3001/v1/transactionTo";//测试
    private final static String URL_TX_RECORD_ALL_GET_DEBUG = HOST_DEBUG + ":3001/v1/transaction";//测试
    private final static String URL_UPDATE_APK_DEBUG = HOST_DEBUG + ":3002/version";   //更新
    private final static String URL_USD_PRICE_DEBUG = HOST_DEBUG + ":3001/v1/version";   //美元
    public final static String URL_USD_EX_RATE_RMB_PRICE_DEBUG_V1 = HOST_DEBUG + ":3001/v1/currency";   //人民币兑美元汇率 版本1
    private final static String URL_USD_EX_RATE_RMB_PRICE_DEBUG = HOST_DEBUG + ":3001/v2/currency";   //人民币兑美元汇率


    //  正式服
    private final static String HOST = "http://walletapi.ionchain.org/api";   //新服
    private final static String URL_UPDATE_APK = HOST + "/version";   //版本信息
    private final static String URL_NODE_LIST = HOST + "/version/node";   //离子链节点

    private final static String HOST_PRICE = "http://explorer.ionchain.org";
    private final static String URL_USD_PRICE = HOST_PRICE + "/v1/version";   //美元价格
    private final static String URL_USD_EX_RATE_RMB_PRICE = HOST_PRICE + "/v2/currency";   //人民币兑美元汇率
    private final static String URL_TX_RECORD_ALL_GET = HOST_PRICE + "/v1/transaction";   //交易记录
    private final static String URL_TX_RECORD_FROM_GET = HOST_PRICE + "/v1/transactionFrom";//测试
    private final static String URL_TX_RECORD_TO_GET = HOST_PRICE + "/v1/transactionTo";//测试

    private final static String HOST_NODE_MAIN = "https://api.ionchain.org";


    //备用节点
//    public final static String HOST_NODE_MAIN_DEBUG = "http://192.168.23.200:8545";
//    public final static String HOST_NODE_MAIN_DEBUG = "http:///192.168.1.8:7545";
    private final static String HOST_NODE_MAIN_DEBUG = "http://172.16.10.88:8545";
//    public final static String HOST_NODE_MAIN_DEBUG = "http://192.168.23.142:7545";


    private final static String TAG = "UrlUtils";
    private final static boolean NODE_LIST_URL_DEBUG = false;
    /**
     * 主链接
     */
    private final static boolean NODE_HOST_DEBUG = false;
    /**
     * 离子币的美元价格
     */
    private final static boolean USD_PRICE_URL_DEBUG = false;
    /**
     * 美元兑各国法币的汇率
     */
    private final static boolean USD_EX_RATE_URL_DEBUG = false;
    /**
     * 交易记录--转出
     */
    private final static boolean TX_RECORD_TO_URL_DEBUG = false;
    /**
     * 交易记录--转入
     */
    private final static boolean TX_RECORD_FROM_URL_DEBUG = false;
    /**
     * 交易记录--全部
     */
    private final static boolean TX_RECORD_ALL_URL_DEBUG = false;
    /**
     * 更新APP
     */
    private final static boolean UPDATE_APK_URL_DEBUG = false;
    /**
     * 获取浏览器查看交易记录的地址
     */
    private final static boolean EXPLORER_URL_DEBUG = false;

    public static String getExplorerUrl() {
        String url;
        if (EXPLORER_URL_DEBUG) {
            url = "http://192.168.23.142:3001/v1/general?queryValue=";
        } else {
            url = "http://explorer.ionchain.org/v1/general?queryValue=";
        }
        LoggerUtils.i(TAG, "getExplorerUrl = " + url);
        return url;
    }

    /**
     * 再用
     *
     * @return 获取美元-人民币汇率信息接口
     */
    public static String getHostNode() {
        String url;
        if (NODE_HOST_DEBUG) {
            url = HOST_NODE_MAIN_DEBUG;
        } else {
            url = HOST_NODE_MAIN;
        }
        LoggerUtils.i(TAG, "getHostNode = " + url);
        return url;
    }

    /**
     * @return 钱包更新接口
     */
    public static String getUpdateApkUrl() {
        String url;
        if (UPDATE_APK_URL_DEBUG) {
            url = URL_UPDATE_APK_DEBUG;
        } else {
            url = URL_UPDATE_APK;
        }
        LoggerUtils.i(TAG, "getUpdateApkUrl = " + url);
        return url;
    }


    /**
     * @return 交易记录接口
     */
    public static String getTxRecordAllUrl() {
        String url;
        if (TX_RECORD_ALL_URL_DEBUG) {
            url = URL_TX_RECORD_ALL_GET_DEBUG;
        } else {
            url = URL_TX_RECORD_ALL_GET;
        }
        LoggerUtils.i(TAG, "getTxRecordAllUrl = " + url);
        return url;
    }

    /**
     * @return 交易记录接口
     */
    public static String getTxRecordFromUrl() {
        String url;
        if (TX_RECORD_FROM_URL_DEBUG) {
            url = URL_TX_RECORD_FROM_GET_DEBUG;
        } else {
            url = URL_TX_RECORD_FROM_GET;
        }
        LoggerUtils.i(TAG, "getTxRecordFromUrl = " + url);
        return url;
    }

    /**
     * @return 交易记录接口
     */
    public static String getTxRecordToUrl() {
        String url;
        if (TX_RECORD_TO_URL_DEBUG) {
            url = URL_TX_RECORD_TO_GET_DEBUG;
        } else {
            url = URL_TX_RECORD_TO_GET;
        }
        LoggerUtils.i(TAG, "getTxRecordToUrl = " + url);
        return url;
    }

    /**
     * @return 获取美元价格
     */
    public static String getUSDPriceUrl() {
        String url;
        if (USD_PRICE_URL_DEBUG) {
            url = URL_USD_PRICE_DEBUG;
        } else {
            url = URL_USD_PRICE;
        }
        LoggerUtils.i(TAG, "getUSDPriceUrl = " + url);
        return url;
    }

    /**
     * @return 获取美元-人民币汇率信息接口
     */
    public static String getUSDExRateUrl() {
        String url;
        if (USD_EX_RATE_URL_DEBUG) {
            url = URL_USD_EX_RATE_RMB_PRICE_DEBUG;
        } else {
            LoggerUtils.i("正式服：汇率" + URL_USD_EX_RATE_RMB_PRICE);
            url = URL_USD_EX_RATE_RMB_PRICE;
        }
        LoggerUtils.i(TAG, "getUSDExRateUrl = " + url);
        return url;
    }

    /**
     * @return 获取主链节点
     */
    public static String getNodeListUrl() {
        String url;
        if (NODE_LIST_URL_DEBUG) {
            url = URL_NODE_LIST;
        } else {
            url = URL_NODE_LIST;
        }
        LoggerUtils.i(TAG, "getNodeListUrl = " + url);
        return url;
    }


}
