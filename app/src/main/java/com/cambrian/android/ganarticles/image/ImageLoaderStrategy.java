package com.cambrian.android.ganarticles.image;

import android.widget.ImageView;

/**
 * 照片显示接口
 *
 * Created by S.J.Xiong on 2017/3/6.
 */

public interface ImageLoaderStrategy {

    void showImage(ImageView imageView, String url, ImageLoaderOptions options);

    void showImage(ImageView imageView, int drawable, ImageLoaderOptions options);
}
