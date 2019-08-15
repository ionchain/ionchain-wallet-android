package org.ionc.wallet.constant;

/**
 * USER: binny 596928539@qq.com
 * DATE: 2018/9/13
 * 描述: 链接
 */
public class ConstantUrl {

    /**
     * 设备host
     */
    private static final String URL_DEVICES_HOST = "http://em-api.ionchain.org";
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

    /**
     * 离子链节点
     */
//    public final static String IONC_CHAIN_NODE = "http://45.77.91.52:8545";
//    public final static String IONC_CHAIN_NODE = "http://207.148.82.225:8545";
//    public final static String IONC_CHAIN_NODE = "HTTP://192.168.2.1:7545"; //ganache
//    public final static String IONC_CHAIN_NODE = "http://66.42.61.229:8545";
    public final static String ETH_CHAIN_NODE = "HTTP://192.168.23.129:7545";
    public final static String INFURA_NODE = "https://ropsten.infura.io/v3/b828c01d687643c5a386a147af819c79";//测试环境

    public final static String TX_RECODER_URL_GET = "http://explorer.blockchainbrother.com/v1/transaction";
}
