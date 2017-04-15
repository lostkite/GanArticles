package com.cambrian.android.ganarticles.thread;

import android.net.NetworkInfo;

/**
 * Created on 2017/3/3.
 */

public interface DownloadCallback<T> {

    /**
     * 检测当前网络状态
     *
     * @return {@link NetworkInfo}
     */
    NetworkInfo getActiveNetworkInfo();

    /**
     * 根据结果更新界面
     *
     * @param result 返回值
     */
    void updateFromDownload(T result);

    /**
     * 无论成功与否都会调用
     */
    void finishDownloading();
}
