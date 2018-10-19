package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.DeviceBean

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 绑定设备回调接口
 */
interface OnUnbindDeviceCallback : OnLoadingView {
    /**
     * 绑定失败
     */
    fun onUnbindSuccess(dataBean: DeviceBean.DataBean)

    /**
     * 绑定失败
     */
    fun onUnbindFailure(result: String)
}
