package com.cambrian.android.ganarticles.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * String 类型和其他类型的一些互换
 * Created by S.J.Xiong on 2017/3/5.
 */

public class StringUtil {
    private StringUtil(){}

    // TODO: 2017/3/11 移除进base fragment
    public static class Message {
        public static final String LOADING = "loading for data";
        public static final String NET_ERROR = "网络连接错误，请检查网络配置";
        public static final String NULL_STARRED = "尚未收藏干货文章，快去阅读吧";
    }

    /**
     * 将 String 的 list 转换成以 ";" 分隔的字符串
     * @param stringList list
     * @return string
     */
    public static String changeListToString(List<String> stringList) {
        String imageUrls = "";
        for (String s : stringList) {
            imageUrls += (s + ";");
        }
        return imageUrls;
    }

    /**
     * 将 String 分隔，存入 String list
     * string 以 ";" 分隔
     * @param imageUrls string
     * @return List<String>
     */
    public static List<String> changeStringToList(String imageUrls) {
        List<String> strings = new ArrayList<>();
        // 将第二个参数的序列全部添加到第一个Collection中
        Collections.addAll(strings, imageUrls.split(";"));
        return strings;
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param bytes 要转换的字节数组
     * @return 转换后的结果
     */
    static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append("%");
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
}
