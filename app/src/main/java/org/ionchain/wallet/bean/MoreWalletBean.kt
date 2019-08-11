package org.ionchain.wallet.bean

import org.sdk.wallet.bean.WalletBeanNew
import java.io.Serializable

/**
 * USER: binny  596928539@qq.com
 * DATE: 2018/9/13
 * 描述:
 */
class MoreWalletBean : Serializable {
    var wallets: List<WalletBeanNew>? = null

    companion object {

        private const val serialVersionUID = -9157094035840945437L
    }
}
