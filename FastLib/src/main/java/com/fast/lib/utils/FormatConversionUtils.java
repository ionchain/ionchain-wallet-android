package com.fast.lib.utils;

import android.content.Context;
import android.net.Uri;

/**
 * Created by siberiawolf on 16/4/11.
 */
public class FormatConversionUtils {

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }
}
