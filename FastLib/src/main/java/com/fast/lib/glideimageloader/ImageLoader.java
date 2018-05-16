package com.fast.lib.glideimageloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.fast.lib.R;
import com.fast.lib.logger.Logger;
import com.fast.lib.utils.ToastUtil;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by siberiawolf on 17/08/2016.
 */
public class ImageLoader {

    private static String TAG = "ImageLoader=>";

    //默认配置
    public static ImageLoadConfig defConfig = new ImageLoadConfig.Builder().
            setCropType(ImageLoadConfig.CENTER_CROP).
            setAsBitmap(true).
            setColorFilter(0).
            setPlaceHolderResId(R.mipmap.bga_pp_ic_holder_dark).
            setErrorResId(R.mipmap.bga_pp_ic_holder_dark).
            setDiskCacheStrategy(ImageLoadConfig.DiskCache.SOURCE).
            setPrioriy(ImageLoadConfig.LoadPriority.HIGH).build();

    /**
     * 加载String类型的资源
     * SD卡资源："file://"+ Environment.getExternalStorageDirectory().getPath()+"/test.jpg"<p/>
     * assets资源："file:///android_asset/f003.gif"<p/>
     * raw资源："Android.resource://com.frank.glide/raw/raw_1"或"android.resource://com.frank.glide/raw/"+R.raw.raw_1<p/>
     * drawable资源："android.resource://com.frank.glide/drawable/news"或load"android.resource://com.frank.glide/drawable/"+R.drawable.news<p/>
     * ContentProvider资源："content://media/external/images/media/139469"<p/>
     * http资源："http://img.my.csdn.net/uploads/201508/05/1438760757_3588.jpg"<p/>
     * https资源："https://img.alicdn.com/tps/TB1uyhoMpXXXXcLXVXXXXXXXXXX-476-538.jpg_240x5000q50.jpg_.webp"<p/>
     *
     * @param view
     * @param imageUrl
     * @param config
     * @param listener
     */
    public static void loadStringRes(ImageView view, String imageUrl, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, imageUrl, config, listener);
    }

    public static void loadFile(ImageView view, File file, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, file, config, listener);
    }

    public static void loadResId(ImageView view, Integer resourceId, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, resourceId, config, listener);
    }

    public static void loadUri(ImageView view, Uri uri, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, uri, config, listener);
    }

    public static void loadGif(ImageView view, String gifUrl, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, gifUrl, ImageLoadConfig.parseBuilder(config).setAsGif(true).build(), listener);
    }

    public static void loadTarget(Context context, Object objUrl, ImageLoadConfig config, final LoaderListener listener) {
        load(context, null, objUrl, config, listener);
    }

    private static void load(Context context, ImageView view, Object objUrl, ImageLoadConfig config, final LoaderListener listener) {
        if (null == objUrl) {
            ToastUtil.showShortToast("image url is null");
            return;
        } else if (null == config) {
            config = defConfig;
        }
        try {
            RequestOptions options = new RequestOptions();
            if (config.isAsBitmap()) {  //bitmap 类型
                if (config.getCropType() == ImageLoadConfig.CENTER_CROP) {
                    options.centerCrop();
                } else {
                    options.fitCenter();
                }
                //transform bitmap
                if (config.isRoundedCorners()) {
                    int rounded = config.getRounded();
                    RoundedCornersTransformation.CornerType cornerType = RoundedCornersTransformation.CornerType.ALL;

                    if(config.getCornerType() != null)
                        cornerType = config.getCornerType();

                    rounded = (int)Resources.getSystem().getDisplayMetrics().density*rounded;
                    options.apply(bitmapTransform(new MultiTransformation<>(new CenterCrop(), new RoundedCornersTransformation(rounded, 0,cornerType))));
                }else if(config.isCropCircle() && config.isGrayscale()){
                    options.apply(bitmapTransform(new MultiTransformation<>(new CropCircleTransformation(),new GrayscaleTransformation())));
                }else if (config.isCropCircle()) {
                    options.apply(bitmapTransform(new CropCircleTransformation()));
                } else if (config.isGrayscale()) {
                    options.apply(bitmapTransform(new GrayscaleTransformation()));
                } else if (config.isBlur()) {
                    options.apply(bitmapTransform(new BlurTransformation(8, 8)));
                } else if (config.getColorFilter() != null && config.getColorFilter() != 0) {
                    options.apply(bitmapTransform(new ColorFilterTransformation(config.getColorFilter())));
                }

                if(config.getSize() != null && config.isCropTransformation())
                    options.apply(bitmapTransform(new CropTransformation(config.getSize().getWidth(),config.getSize().getHeight(), CropTransformation.CropType.TOP)));
            }
            //缓存设置
            options.diskCacheStrategy(config.getDiskCacheStrategy().getStrategy()).
                    skipMemoryCache(config.isSkipMemoryCache()).
                    priority(config.getPrioriy().getPriority());
            options.dontAnimate();
            if (null != config.getTag()) {
                options.signature(new ObjectKey(config.getTag()));
            } else {
                options.signature(new ObjectKey(objUrl.toString()));
            }

            if (null != config.getErrorResId()) {
                options.error(config.getErrorResId());
            }
            if (null != config.getPlaceHolderResId()) {
                options.placeholder(config.getPlaceHolderResId());
            }
            if (null != config.getSize()) {
                options.override(config.getSize().getWidth(), config.getSize().getHeight());
            }

            RequestBuilder<Drawable> builder = Glide.with(context)
                    .load(objUrl)
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (!e.getMessage().equals("divide by zero")) {
                                if(listener != null)
                                listener.onError();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if(listener != null)
                            listener.onSuccess(resource,model,target,dataSource,isFirstResource);
                            return false;
                        }
                    });
            builder.into(view);



        } catch (Exception e) {
            Logger.e(TAG,e);
            view.setImageResource(config.getErrorResId());
        }
    }



    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    public static void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }
    /**
     * 清除所有缓存
     * @param context
     */
    public static void cleanAll(Context context) {
        clearDiskCache(context);
        Glide.get(context).clearMemory();
    }



}
