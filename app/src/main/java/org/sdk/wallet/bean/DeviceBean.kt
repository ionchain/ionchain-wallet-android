package org.sdk.wallet.bean

import java.io.Serializable

/**
 * USER: binny  596928539@qq.com
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
        override fun toString(): String {
            return "DataBean(id=$id, name=$name, cksn=$cksn, system=$system, created_at=$created_at, image_url=$image_url)"
        }

    }

    companion object {

        private const val serialVersionUID = -1659155236033075557L
    }
}
