package com.fast.lib.glideimageloader;

import android.graphics.Bitmap;

/**
 * Created by siberiawolf on 17/08/2016.
 */
public interface BitmapLoadingListener {
    void onSuccess(Bitmap b);

    void onError();
}
