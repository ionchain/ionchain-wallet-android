package com.fast.lib.glideimageloader;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.request.target.Target;

/**
 * Created by siberiawolf on 17/08/2016.
 */
public interface LoaderListener {
    void onSuccess(Object resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource);

    void onError();
}
