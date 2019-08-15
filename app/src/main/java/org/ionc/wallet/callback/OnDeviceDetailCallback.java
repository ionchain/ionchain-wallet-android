package org.ionc.wallet.callback;


import org.ionc.wallet.bean.DeviceBean;

/**
 * USER: binny
 * DATE: 2018/9/15
 * 描述:
 */
public interface OnDeviceDetailCallback {
    /**
     * 请求设备详情成功
     *
     * @param deviceBean 设备信息
     */
    void onDeviceDetailSuccess(DeviceBean.DataBean deviceBean);

    /**
     * 请求设备详情失败
     */
    void onDeviceDetailFailure();
}
