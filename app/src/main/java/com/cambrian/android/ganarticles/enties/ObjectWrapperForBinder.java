package com.cambrian.android.ganarticles.enties;

import android.os.Binder;

/**
 * android binder will create global JNI reference for java object and release this global JNI
 * reference when there are no reference for this java object.
 * binder will save this global JNI reference in the Binder object.
 * <p/>
 * this method ONLY work unless the two activities run in the same process,
 * otherwise throw ClassCastException
 * <p/>
 * 封装实体类以便储存在 bundle 中传输
 *
 * Created by S.J.Xiong on 2017/3/9.
 */

public class ObjectWrapperForBinder extends Binder {

    private final Object mData;

    public ObjectWrapperForBinder(Object data) {
        mData = data;
    }

    public Object getData() {
        return mData;
    }
}
