package org.ionchain.wallet.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;

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
    /**
     * 获取状态通知栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        Log.i("frame.top", "getStatusBarHeight: " + frame.top);
        return frame.top;
    }

}

