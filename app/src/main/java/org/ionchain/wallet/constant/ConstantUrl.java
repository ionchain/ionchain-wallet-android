package org.ionchain.wallet.constant;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 链接
 */
public class ConstantUrl {

    /**
     * 设备host
     */
    private static final String DEVICES_HOST = "http://sendrobot.ionchain.org";
    /**
     * 获取设备列表
     */
    public final static String DEVICES_GET = DEVICES_HOST + "/api/wallet/v1/devices";
    /**
     * 设备详情
     */
    public final static String DEVICES_DETAIL = DEVICES_HOST + "/api/wallet/v1/devices/";
    /**
     * 绑定设备
     */
    public final static String DEVICES_BIND_POST = DEVICES_HOST + "/api/wallet/v1/devices/bind";
    /**
     * 解绑设备
     */
    public final static String DEVICES_UNBIND_POST = DEVICES_HOST + "/api/wallet/v1/devices/unbind";


    public final static String TX_RECODER_URL_GET = "http://explorer.blockchainbrother.com/v1/transaction";

    public final static String UPDATE_APK = DEVICES_HOST + "/api/wallet/v1/versions";

}
