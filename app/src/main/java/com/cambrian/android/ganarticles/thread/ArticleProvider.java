package com.cambrian.android.ganarticles.thread;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.cambrian.android.ganarticles.BuildConfig;
import com.cambrian.android.ganarticles.network.BaseNetWork;

import java.io.IOException;

/**
 * 进行download 多线程操作的类
 * Created on 2017/3/3.
 */

public class ArticleProvider {
    private DownloadTask mTask;
    private DownloadCallback<ArticleResult> mCallback;

    public ArticleProvider(DownloadCallback<ArticleResult> callback) {
        mCallback = callback;
    }

    public void startDownload(String urlString) {
        cancelDownload();
        mTask = new DownloadTask();
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlString);
    }

    public void cancelDownload() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, ArticleResult> {


        @Override
        protected void onPreExecute() {
            if (mCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()
                        || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    mCallback.updateFromDownload(null);
                    cancel(true);
                }
            }
        }

        @Override
        protected ArticleResult doInBackground(String... params) {
            ArticleResult result = null;
            if (!isCancelled() && params != null && params.length > 0) {
                try {
                    String url = params[0];
                    if (BuildConfig.DEBUG) {
                        Log.i("download ", "doInBackground: " + url);
                    }
                    String resultString = new BaseNetWork().getUrlResultString(url);
                    if (!TextUtils.isEmpty(resultString)) {
                        result = new ArticleResult(resultString);
                    } else {
                        throw new IOException("No response received");
                    }
                } catch (IOException e) {
                    result = new ArticleResult(e);
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(ArticleResult result) {
            if (result != null && mCallback != null) {
                mCallback.finishDownloading();
                mCallback.updateFromDownload(result);
            }
        }

        @Override
        protected void onCancelled(ArticleResult result) {
            super.onCancelled();
        }
    }
}
