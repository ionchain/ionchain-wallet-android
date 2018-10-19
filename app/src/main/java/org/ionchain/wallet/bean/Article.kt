package org.ionchain.wallet.bean

import java.io.Serializable

class Article : Serializable {
    var id: Int = 0
    var title: String? = null
    var content: String? = null
    var imageUrl: String? = null
    var createTime: String? = null
    var viewCount: Int = 0
    var praiseCount: Int = 0
    var isRecommend: Int = 0
    var isPraise: Int = 0
    var url: String? = null

    companion object {

        private const val serialVersionUID = 829702727192931754L
    }
}
