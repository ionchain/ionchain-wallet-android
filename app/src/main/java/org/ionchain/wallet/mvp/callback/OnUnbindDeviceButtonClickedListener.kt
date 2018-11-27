package org.ionchain.wallet.mvp.callback

/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 长按解绑设备
 */
interface OnUnbindDeviceButtonClickedListener {
    fun onUnbindButtonClick(cksn: String, position: Int)
}
