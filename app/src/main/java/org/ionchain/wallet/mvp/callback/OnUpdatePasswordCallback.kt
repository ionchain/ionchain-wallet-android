package org.ionchain.wallet.mvp.callback

import org.ionchain.wallet.bean.WalletBean

interface OnUpdatePasswordCallback {

    fun onUpdatePasswordSuccess(wallet: WalletBean)

    fun onUpdatePasswordFailure(error: String)
}
