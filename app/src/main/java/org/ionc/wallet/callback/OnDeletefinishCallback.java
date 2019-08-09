package org.ionc.wallet.callback;

/**
 * 删除钱包的回调
 * 根据业务可扩展;删除成功或者失败
 */
public interface OnDeletefinishCallback {
    /**
     * 删除完成
     */
    void onDeleteFinish();
}
