package org.ionc.wallet.callback;


/**
 * USER: binny
 * DATE: 2018/9/14
 * 描述: 长按解绑设备
 */
public interface OnUnbindDeviceButtonClickedListener {
    void onUnbindButtonClick(String cksn, int position);
}
