package org.ionchain.wallet.utils;

import org.ionchain.wallet.App;
import org.ionchain.wallet.R;

public class ColorUtils {
    public static int getTxColorFailure() {
        return App.mAppInstance.getResources().getColor(R.color.tx_color_failure);
    }
}
