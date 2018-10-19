package org.ionchain.wallet.bean

import java.io.Serializable

class UserModel : Serializable {
    /**
     * userId : 26
     * userName : 15618273172
     * tel : 15618273172
     * inviteCode : EPX9NP
     * coin : 30
     */

    var userId: Int = 0
    var userName: String? = null
    var tel: String? = null
    var inviteCode: String? = null
    var coin: Int = 0

    companion object {


        private const val serialVersionUID = 1280463101463565123L
    }
}
