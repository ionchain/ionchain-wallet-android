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
}
