package org.ionc.wallet.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;

public class AnimationUtils {
    /**
     * 点击动画效果
     *
     * @param view 的透明度变化
     */
    public static void setViewAlphaAnimation(View view) {
        AlphaAnimation alphaAni = new AlphaAnimation(0.05f, 1.0f);
        alphaAni.setDuration(100);
        view.startAnimation(alphaAni);
    }
}
