package org.ionc.ionclib.callback;

public interface OnContractCoinTransferCallback {
    /**
     * @param position
     * @param balance  余额
     */
    void onContractCoinTransferSuccess(String balance);

    /**
     * @param position
     * @param error    失败
     */
    void onContractCoinTransferFailure(String error);

}
