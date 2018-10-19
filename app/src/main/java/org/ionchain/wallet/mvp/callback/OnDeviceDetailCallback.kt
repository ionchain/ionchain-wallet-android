package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.DeviceBean

/**
 * USER: binny
 * DATE: 2018/9/15
 * 描述:
 */
interface OnDeviceDetailCallback {
    /**
     * 请求设备详情成功
     * @param deviceBean 设备信息
     */
    fun onDeviceDetailSuccess(deviceBean: DeviceBean.DataBean)

    /**
     * 请求设备详情失败
     */
    fun onDeviceDetailFailure()
}
