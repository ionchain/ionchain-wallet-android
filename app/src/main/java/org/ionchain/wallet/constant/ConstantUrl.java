package org.ionchain.wallet.constant;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 链接
 */
public class ConstantUrl {
    /**
     * 离子链节点
     */
    public final static String IONC_CHAIN_NODE = "http://192.168.23.71:8545";
    /**
     * 获取设备列表
     */
    public final static String DEVICES_GET = "http://sendrobot.ionchain.org/api/wallet/v1/devices";
    /**
     * 设备详情
     */
    public final static String DEVICES_DETAIL = "http://sendrobot.ionchain.org/api/wallet/v1/devices/";
    /**
     * 绑定设备
     */
    public final static String DEVICES_BIND_POST = "http://sendrobot.ionchain.org/api/wallet/v1/devices/bind";
    /**
     * 解绑设备
     */
    public final static String DEVICES_UNBIND_POST = "http://sendrobot.ionchain.org/api/wallet/v1/devices/unbind";
}
