package org.ionc.ionclib.constant;

/**
 * 错误码,从1000开始
 */
public class ConstantErrorCode {
    //检查更新的错误码
    public static final String ERROR_CODE_NET_WORK_UPDATE = "检查更新网络错误";//
    //设别相关的错误码
    public static final String ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND = "设备绑定网络错误";//
    public static final String ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND = "设备解除绑定网络错误";//
    public static final String ERROR_CODE_NET_WORK_ERROR_DEVICE_BIND_NULL = "设备绑数据解析错误";//
    public static final String ERROR_CODE_NET_WORK_ERROR_DEVICE_UNBIND_NULL = "设备解除绑数据解析错误";//

    public static final String ERROR_CODE_NET_WORK_ERROR_TX_RECORD = "交易记录请求失败";//
    public static final String ERROR_CODE_BALANCE_IONC = "2000";//获取离子链上余额失败
    public static final String ERROR_CODE_BALANCE_ETH = "2001";//获取以太坊余额失败

    public static final String ERROR_CREATE_WALLET_FAILURE = "ERROR_CREATE_WALLET_FAILURE"; //创建钱包失败
    public static final String ERROR_KEYSTORE = "ERROR_KEYSTORE";
    public static final String ERROR_TRANSCATION_FAILURE = "TRANSCATION_FAILURE";
}
