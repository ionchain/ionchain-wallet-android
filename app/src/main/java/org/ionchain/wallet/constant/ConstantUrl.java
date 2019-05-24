package org.ionchain.wallet.constant;

/**
 * USER: binny 596928539@qq.com
 * DATE: 2018/9/13
 * 描述: 链接
 */
public class ConstantUrl {

    /**
     * 设备host
     */
    private static final String URL_DEVICES_HOST = "http://sendrobot.ionchain.org";
    /**
     * 获取设备列表
     */
    public final static String URL_DEVICES_GET = URL_DEVICES_HOST + "/api/wallet/v1/devices";
    /**
     * 设备详情
     */
    public final static String URL_DEVICES_DETAIL = URL_DEVICES_HOST + "/api/wallet/v1/devices/";
    /**
     * 绑定设备
     */
    public final static String URL_DEVICES_BIND_POST = URL_DEVICES_HOST + "/api/wallet/v1/devices/bind";
    /**
     * 解绑设备
     */
    public final static String URL_DEVICES_UNBIND_POST = URL_DEVICES_HOST + "/api/wallet/v1/devices/unbind";


    public final static String URL_TX_RECORD_GET = "http://explorer.blockchainbrother.com/v1/transaction";
//    public final static String URL_TX_RECORD_GET = "http://192.168.23.164:3001/v1/transaction";//测试

//    public final static String URL_UPDATE_APK = "http://walletapi.ionchain.org/api/version";

//    public final static String URL_UPDATE_APK = "http://192.168.23.142:3002/version";   //test
//    public final static String URL_USD_PRICE = "http://192.168.23.142:3001/v1/version";   //test
//    public final static String URL_USD_EX_RATE_RMB_PRICE = "http://192.168.23.142:3001/v1/currency";   //test


    //
    private final static String HOST = "http://walletapi.ionchain.org/api";   //新服
    public final static String URL_UPDATE_APK = HOST + "/version";   //版本信息
    public final static String URL_USD_PRICE = HOST + "/v1/version";   //美元价格
    public final static String URL_USD_EX_RATE_RMB_PRICE = HOST + "/v1/currency";   //人民币兑美元汇率
    public final static String URL_NODE_LIST = HOST + "/version/node";   //离子链节点

}
