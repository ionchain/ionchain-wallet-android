package org.sdk.wallet.callback;

import java.math.BigInteger;

public interface OnGasPriceCallback {
    void onGasPriceSuccess(BigInteger gasPrice);

    void onGasRiceFailure(String error);
}
