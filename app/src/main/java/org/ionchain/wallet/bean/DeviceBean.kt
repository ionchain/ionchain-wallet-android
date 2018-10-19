package org.ionchain.wallet.bean

import java.io.Serializable

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述: 设备信息
 */
class DeviceBean : Serializable {
    /**
     * success : 0
     * message : ok
     * data : {"id":3,"name":"共享单车","cksn":"4eeb01d54c4c11a1480cdabd1497a943","system":"Android","created_at":"2018-09-13","image_url":""}
     */

    var success: Int = 0
    var message: String? = null
    var data: DataBean? = null

    class DataBean : Serializable {
        /**
         * id : 3
         * name : 共享单车
         * cksn : 4eeb01d54c4c11a1480cdabd1497a943
         * system : Android
         * created_at : 2018-09-13
         * image_url :
         */

        var id: Int = 0
        var name: String? = null
        var cksn: String? = null
        var system: String? = null
        var created_at: String? = null
        var image_url: String? = null
    }

    companion object {

        private const val serialVersionUID = -1659155236033075557L
    }
}
