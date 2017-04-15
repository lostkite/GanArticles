package com.cambrian.android.ganarticles.thread;

/**
 * 返回结果封装类
 * Created by S.J.Xiong on 2017/3/6.
 */

public class ArticleResult {
    private String mResultValue;
    private Exception mException;

    public ArticleResult(String resultValue) {
        mResultValue = resultValue;
    }

    public ArticleResult(Exception exception) {
        mException = exception;
    }

    public String getResultValue() {
        return mResultValue;
    }

    public Exception getException() {
        return mException;
    }
}
