package org.ionchain.wallet.bean

import java.io.Serializable

/**
 * USER: binny
 * DATE: 2018/9/13
 * 描述:
 */
class MoreWalletBean : Serializable {
    var wallets: List<WalletBean>? = null

    companion object {

        private const val serialVersionUID = -9157094035840945437L
    }
}
