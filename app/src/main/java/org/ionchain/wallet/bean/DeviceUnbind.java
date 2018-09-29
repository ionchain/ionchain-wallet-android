package org.ionchain.wallet.bean;

import java.io.Serializable;

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述:
 */
public class DeviceUnbind implements Serializable {


    private static final long serialVersionUID = 7396183758899566166L;
    /**
     * success : 2005
     * message : 钱包地址不存在
     */

    private int success;
    private String message;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
