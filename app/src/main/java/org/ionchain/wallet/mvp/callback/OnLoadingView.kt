package org.ionchain.wallet.mvp.callback

/**
 * author  binny
 * date 5/8
 *
 *
 * 正在加载
 */
interface OnLoadingView {
    /**
     * 正在加载
     */
    fun onLoadStart()

    /**
     * 加载完成
     */
    fun onLoadFinish()
}
