package org.ionchain.wallet.model;

import java.io.Serializable;

/**
 * USER: binny
 * DATE: 2018/9/12
 * 描述: 我的设备实体类
 */
public class DeviceBean implements Serializable {
    private String mDeviceType;//设备类型
    private String mDeviceId;//设备id
    private String mEarnings;//收益

    public String getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(String deviceType) {
        mDeviceType = deviceType;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    public String getEarnings() {
        return mEarnings;
    }

    public void setEarnings(String earnings) {
        mEarnings = earnings;
    }
}
