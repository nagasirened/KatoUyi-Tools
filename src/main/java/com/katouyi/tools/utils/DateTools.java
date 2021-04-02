package com.katouyi.tools.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTools {

    /**
     * 获取当前时间的秒数
     */
    static Supplier<Integer> getCurrentSecond = () -> Integer.valueOf("" + DateUtil.currentSeconds());

    /**
     * 时间字符串转为秒的Integer,一天的开始, "2021/03/02"
     */
    static Function<String, Integer> dayStartStringToIntConverter = item ->
    {
        String timer = item + " 00:00:00";
        return Integer.valueOf("" + (new Date(timer).getTime() / 1000));
    };

    /**
     * 时间字符串转为秒的Integer,一天的结束, "2021/03/02"
     */
    static Function<String, Integer> dayEndStringToIntConverter = item ->
    {
        String timer = item + " 23:59:59";
        return Integer.valueOf("" + (new Date(timer).getTime() / 1000));
    };

    /**
     * 将Integer类型的秒数转为 yyyy-MM-dd
     */
    static Function<Integer, String> secondToDateString = item ->
        DateTime.of(Long.valueOf(item + "000")).toDateStr();

    /**
     *
     */
    static Function<Integer, String> secondToDateStringXie = item ->
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(DateTime.of(Long.valueOf(item + "000")));
    };

}
