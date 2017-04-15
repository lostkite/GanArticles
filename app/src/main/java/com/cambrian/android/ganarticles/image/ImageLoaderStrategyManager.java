package com.cambrian.android.ganarticles.image;

import android.widget.ImageView;

/**
 * 策略模式， Manager 类
 * Created by S.J.Xiong on 2017/3/7.
 */

public class ImageLoaderStrategyManager implements ImageLoaderStrategy {
    private static final ImageLoaderStrategyManager INSTANCE = new ImageLoaderStrategyManager();
    private ImageLoaderStrategy imageLoader;
    private ImageLoaderStrategyManager() {
        // 默认使用 Glide
        imageLoader = new GlideImageLoaderStrategy();
    }

    public static ImageLoaderStrategyManager getInstance() {
        return INSTANCE;
    }

    public void setImageLoader(ImageLoaderStrategy imageLoader) {
        if (imageLoader != null) {
            this.imageLoader = imageLoader;
        }
    }

    @Override
    public void showImage(ImageView imageView, String url, ImageLoaderOptions options) {
        imageLoader.showImage(imageView, url, options);
    }

    @Override
    public void showImage(ImageView imageView, int drawable, ImageLoaderOptions options) {
        imageLoader.showImage(imageView, drawable, options);
    }
}
