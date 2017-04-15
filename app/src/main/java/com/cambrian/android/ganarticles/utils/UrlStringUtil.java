package com.cambrian.android.ganarticles.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.cambrian.android.ganarticles.utils.StringUtil.bytesToHexString;
import static java.lang.String.format;

/**
 * url 对应的 String 工具类
 * Created by S.J.Xiong on 2017/3/5.
 */

public class UrlStringUtil {
    private static String valueHolder;
    private UrlStringUtil() {
    }

    /**
     * 应用中使用的url 对应的string
     */
    private static final class URL_STRING {
        static final String URL_SEARCH =
                "http://gank.io/api/search/query/%s/category/all/count/10/page/%d";
        static final String URL_TODAY_ARTICLE = "http://gank.io/api/day/%s";
        static final String URL_SINGLE_TYPE_ARTICLE = "http://gank.io/api/data/%s/10/%d";
        static final String URL_IMAGE_PLACE_HOLDER_SUFFIX = "%s?imageView2/2/h/350";
    }


    public static String getUrlString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String dateString = dateFormat.format(date);
        return format(URL_STRING.URL_TODAY_ARTICLE, dateString);
    }

    public static String getUrlString(String type, int times) {
        String hex = bytesToHexString(type.getBytes());
        return format(Locale.US, URL_STRING.URL_SINGLE_TYPE_ARTICLE, hex, times);
    }

    public static String getSearchUrlString(String value, int times) {
        String hex = bytesToHexString(value.getBytes());
        valueHolder = hex;
        return format(Locale.getDefault(), URL_STRING.URL_SEARCH, hex, times);
    }

    public static String getSearchUrlString(int times) {
        return format(Locale.getDefault(), URL_STRING.URL_SEARCH, valueHolder, times);
    }

    public static String getProjectUrl() {
        return "https://github.com/lostkite/GanArticles";
    }

    public static String getScaledImageUrlString(String imageUrl) {
        return format(Locale.getDefault(), URL_STRING.URL_IMAGE_PLACE_HOLDER_SUFFIX, imageUrl);
    }
}
