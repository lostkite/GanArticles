package com.cambrian.android.ganarticles.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * <p>
 * Created by S.J.Xiong.
 */

public class DateUtil {

    private DateUtil() {
    }

    /**
     * 获取最近可用的日期
     *
     * @return {@link Date}
     */
    public static Date getAvailableDate() {
        // 获取当前时间点
        Calendar calendar = Calendar.getInstance();
        // 去掉周五周六
        while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return calendar.getTime();
    }

    /**
     * 获取参数前一天的日期
     *
     * @param date Date
     * @return {@link Date}
     */
    public static Date getLastDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }
}
