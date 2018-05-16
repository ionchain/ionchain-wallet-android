package org.ionchain.wallet.comm.utils;

import com.fast.lib.utils.LibScreenUtils;

import org.ionchain.wallet.comm.constants.Global;

/**
 * Created by siberiawolf on 17/8/14.
 */

public class ScreenUtils extends LibScreenUtils {

    public static int getScreenWidth() {
        return getScreenWidth(Global.mContext);
    }
    public static int getScreenHeight() {
        return getScreenHeight(Global.mContext);
    }
}
