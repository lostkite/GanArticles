package com.cambrian.android.ganarticles.enties;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/2/2.
 */

public class Article {

    @NonNull
    private final String mId;
    @NonNull
    private final String mDesc;
    @NonNull
    private final TypeEnum mType;
    @NonNull
    private final String mUrl;

    @Nullable
    private final List<String> mImageUrls;
    @Nullable
    private final String mPublishedTime;
    private boolean mStarred;

    public static class Builder {
        private String id;
        private String desc;
        private TypeEnum type;
        private String url;

        private List<String> imageUrls = new ArrayList<>();
        private String publishedTime = " ";
        private boolean starred;

        public Builder(String id, String desc, TypeEnum type, String url) {
            this.id = id;
            this.desc = desc;
            this.type = type;
            this.url = url;
        }

        public Builder imageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            return this;
        }

        public Builder publishedTime(String publishedTime) {
            this.publishedTime = publishedTime;
            return this;
        }

        public Builder isStarred(boolean starred) {
            this.starred = starred;
            return this;
        }

        public Article build() {
            return new Article(this);
        }
    }

    private Article(Builder builder) {
        mId = builder.id;
        mDesc = builder.desc;
        mType = builder.type;
        mUrl = builder.url;
        mImageUrls = builder.imageUrls;
        mPublishedTime = builder.publishedTime;
        mStarred = builder.starred;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getDesc() {
        return mDesc;
    }

    @NonNull
    public TypeEnum getType() {
        return mType;
    }

    @NonNull
    public String getUrl() {
        return mUrl;
    }

    @Nullable
    public List<String> getImageUrls() {
        return mImageUrls;
    }

    @Nullable
    public String getPublishedTime() {
        return mPublishedTime;
    }

    public void setStarred(boolean starred) {
        mStarred = starred;
    }

    public boolean isStarred() {
        return mStarred;
    }
}
