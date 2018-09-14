package org.ionchain.wallet.callback;

/**
 * author  binny
 * date 5/8
 * <p>
 * 正在加载
 */
public interface ILoadingView {
    /**
     * 正在加载
     */
    void onLoadStart();

    /**
     * 加载完成
     */
    void onLoadFinish();
}
