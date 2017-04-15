package com.cambrian.android.ganarticles.image;

import com.bumptech.glide.request.animation.ViewPropertyAnimation;

/**
 * 图片显示设置参数
 * Created by S.J.Xiong on 2017/3/6.
 */

public class ImageLoaderOptions {
    private int placeHolder;
    private ViewPropertyAnimation.Animator animator;
    private ImageReSize size;
    private int errorDrawable;
    private boolean isCrossFade;
    private boolean isSkipMemoryCache;
    private boolean isCenterCrop;
    private boolean isFitCenter;


    private ImageLoaderOptions(Builder builder) {
        this.placeHolder = builder.placeHolder;
        this.size = builder.size;
        this.errorDrawable = builder.errorDrawable;
        this.isCrossFade = builder.isCrossFade;
        this.isSkipMemoryCache = builder.isSkipMemoryCache;
        this.animator = builder.animator;
        this.isCenterCrop = builder.isCenterCrop;
        this.isFitCenter = builder.isFitCenter;
    }

    public static class Builder {
        private int placeHolder = -1;
        private ImageReSize size = null;
        private int errorDrawable = -1;
        private boolean isCrossFade = false;
        private boolean isSkipMemoryCache = false;
        private ViewPropertyAnimation.Animator animator = null;
        private boolean isCenterCrop = false;
        private boolean isFitCenter = false;

        public Builder() {
        }

        public Builder placeHolder(int drawable) {
            this.placeHolder = drawable;
            return this;
        }

        public Builder reSize(ImageReSize size) {
            this.size = size;
            return this;
        }

        public Builder animator(ViewPropertyAnimation.Animator animator) {
            this.animator = animator;
            return this;
        }

        public Builder errorDrawable(int errorDrawable) {
            this.errorDrawable = errorDrawable;
            return this;
        }

        public Builder isCrossFade(boolean isCrossFade) {
            this.isCrossFade = isCrossFade;
            return this;
        }

        public Builder isSkipMemoryCache(boolean isSkipMemoryCache) {
            this.isSkipMemoryCache = isSkipMemoryCache;
            return this;
        }

        public Builder isCenterCrop(boolean isCenterCrop) {
            this.isCenterCrop = isCenterCrop;
            return this;
        }

        public Builder isFitCenter(boolean isFitCenter) {
            this.isFitCenter = isFitCenter;
            return this;
        }

        public ImageLoaderOptions build() {
            return new ImageLoaderOptions(this);
        }
    }

    public int getPlaceHolder() {
        return placeHolder;
    }

    public ViewPropertyAnimation.Animator getAnimator() {
        return animator;
    }

    public ImageReSize getSize() {
        return size;
    }

    public int getErrorDrawable() {
        return errorDrawable;
    }

    public boolean isCrossFade() {
        return isCrossFade;
    }

    public boolean isSkipMemoryCache() {
        return isSkipMemoryCache;
    }

    public boolean isCenterCrop() {
        return isCenterCrop;
    }

    public boolean isFitCenter() {
        return isFitCenter;
    }

    public class ImageReSize {
        int reWidth = 0;
        int reHeight = 0;

        public ImageReSize(int reWidth, int reHeight) {
            if (reWidth <= 0) {
                reWidth = 0;
            }
            if (reHeight <= 0) {
                reHeight = 0;
            }
            this.reWidth = reWidth;
            this.reHeight = reHeight;
        }
    }

}
