package com.cambrian.android.ganarticles.image;

import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

/**
 * 策略模式，用 glide 实现 {@link ImageLoaderStrategy}
 * Created by S.J.Xiong on 2017/3/6.
 */

public class GlideImageLoaderStrategy implements ImageLoaderStrategy {
    @Override
    public void showImage(ImageView imageView, String url, ImageLoaderOptions options) {
        DrawableTypeRequest dtr = Glide.with(imageView.getContext()).load(url);
        loadOptions(dtr, options).into(imageView);
    }

    @Override
    public void showImage(ImageView imageView, int drawable, ImageLoaderOptions options) {
        DrawableTypeRequest dtr = Glide.with(imageView.getContext()).load(drawable);
        loadOptions(dtr, options).into(imageView);
    }

    private DrawableTypeRequest loadOptions(DrawableTypeRequest dtr, ImageLoaderOptions options) {
        if (options == null) {
            return dtr;
        }
        if (options.getPlaceHolder() != -1) {
            dtr.placeholder(options.getPlaceHolder());
        }
        if (options.getErrorDrawable() != -1) {
            dtr.error(options.getErrorDrawable());
        }
        if (options.isCrossFade()) {
            dtr.crossFade();
        }
        if (options.isSkipMemoryCache()) {
            dtr.skipMemoryCache(options.isSkipMemoryCache());
        }
        if (options.getAnimator() != null) {
            dtr.animate(options.getAnimator());
        }
        if (options.getSize() != null) {
            dtr.override(options.getSize().reWidth, options.getSize().reHeight);
        }
        if (options.isCenterCrop()) {
            dtr.centerCrop();
        }
        if (options.isFitCenter()) {
            dtr.fitCenter();
        }
        return dtr;
    }
}
