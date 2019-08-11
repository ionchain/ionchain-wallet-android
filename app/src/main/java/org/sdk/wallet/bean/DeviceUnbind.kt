package org.sdk.wallet.bean

import java.io.Serializable

/**
 * USER: binny  596928539@qq.com
 * DATE: 2018/9/13
 * 描述:
 */
class DeviceUnbind : Serializable {
    /**
     * success : 2005
     * message : 钱包地址不存在
     */

    var success: Int = 0
    var message: String? = null

    companion object {
        private const val serialVersionUID = 7396183758899566166L
    }
}
