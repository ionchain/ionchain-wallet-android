package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.DeviceBean

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 获取设备列表
 */
interface OnDeviceListCallback : OnLoadingView {
    /**
     * 获取设备列表成功
     *
     * @param list 设备列表
     */
    fun onDeviceListSuccess(list: List<DeviceBean.DataBean>)

    /**
     * 获取设备列表失败
     *
     * @param errorMessage 失败信息
     */
    fun onDeviceListFailure(errorMessage: String)
}
