package com.cambrian.android.ganarticles.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by S.J.Xiong.
 */

public class DimenUtil {
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
