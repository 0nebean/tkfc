package com.tkfc.core.toolkit;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateBetween;
import cn.hutool.core.date.DateUnit;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.YesterdayEnum;
import com.tkfc.core.function.SerializableClosure;
import com.tkfc.core.throwable.base.Assert;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具类
 * 日期类型的工具类，该类均为静态方法，直接调用
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
public class DateUtil {

    private static final SimpleDateFormat FULL_TIME_FORMAT_TEMPLATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_TIME_FORMAT_TEMPLATE = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat LOG_TIME_FORMAT_TEMPLATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final SimpleDateFormat FILE_NAME_TIME_FORMAT_TEMPLATE = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");

    private static final int PRESS_DATA_YEAR = 2023;
    // 月份从0 1 2 3 计算
    private static final int PRESS_DATA_MONTH = 3;
    private static final int PRESS_DATA_DAY = 16;


    //=================================== cover date ===================================

    /**
     * 转换时间 成 '多久前' 中文版
     *
     * @param date 时间
     * @return '多久前'
     */
    @SuppressWarnings("all")
    public static String coverTimestampToTimesAgoCh(Date date) {
        String shortString;
        if (date == null) {
            return null;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        long delayTime = (now - date.getTime()) / 1000;
        if (delayTime > 365 * 24 * 60 * 60) {
            shortString = (int) (delayTime / (365 * 24 * 60 * 60)) + "年前";
        } else if (delayTime > 24 * 60 * 60) {
            shortString = (int) (delayTime / (24 * 60 * 60)) + "天前";
        } else if (delayTime > 60 * 60) {
            shortString = (int) (delayTime / (60 * 60)) + "小时前";
        } else if (delayTime > 60) {
            shortString = (int) (delayTime / (60)) + "分前";
        } else if (delayTime > 1) {
            shortString = delayTime + "秒前";
        } else {
            shortString = "1秒前";
        }
        return shortString;
    }

    /**
     * 转换时间 成 '多久前'
     *
     * @param date 时间
     * @return '多久前'
     */
    @SuppressWarnings("all")
    public static String coverTimestampToTimesAgo(Date date) {
        String shortString;
        if (date == null) {
            return null;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        long delayTime = (now - date.getTime()) / 1000;
        if (delayTime > 365 * 24 * 60 * 60) {
            shortString = (int) (delayTime / (365 * 24 * 60 * 60)) + "years ago";
        } else if (delayTime > 24 * 60 * 60) {
            shortString = (int) (delayTime / (24 * 60 * 60)) + "days ago";
        } else if (delayTime > 60 * 60) {
            shortString = (int) (delayTime / (60 * 60)) + "hours ago";
        } else if (delayTime > 60) {
            shortString = (int) (delayTime / (60)) + "mines ago";
        } else if (delayTime > 1) {
            shortString = delayTime + "sec ago";
        } else {
            shortString = "1 sec ago";
        }
        return shortString;
    }


    //=================================== get date ===================================


    /**
     * 获取当前时间戳字符串
     *
     * @return 返回当前时间的13位时间戳字符串
     */
    public static String getTimeStampString() {
        Date now = new Date();
        return String.valueOf(now.getTime());
    }

    /**
     * 获取当前时间的格式化字符串
     *
     * @return 返回格式为 "yyyy-MM-dd HH:mm:ss" 的当前时间字符串
     */
    public static String getCurrentDateFormatString() {
        return FULL_TIME_FORMAT_TEMPLATE.format(new Date());
    }

    /**
     * 获取当前时间的简单格式化字符串
     *
     * @return 返回格式为 "yyyyMMdd" 的当前时间字符串
     */
    public static String getSimpleDateFormatString() {
        return SIMPLE_TIME_FORMAT_TEMPLATE.format(new Date());
    }


    /**
     * 获取当前时间的文件名格式字符串
     *
     * @return 返回格式为 "yyyy_MM_dd_HH_mm_ss_SSS" 的当前时间字符串，适用于文件名
     */
    public static String getCurrentDateFormatFileNameString() {
        return FILE_NAME_TIME_FORMAT_TEMPLATE.format(new Date());
    }


    /**
     * 获取当前时间的日志格式字符串
     *
     * @return 返回格式为 "yyyy-MM-dd HH:mm:ss:SSS" 的当前时间字符串，适用于日志记录
     */
    public static String getCurrentLogDateFormat() {
        return LOG_TIME_FORMAT_TEMPLATE.format(new Date());
    }

    /**
     * 获取当前时间
     *
     * @return 返回当前时间的 Date 对象
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前时间
     *
     * @return 返回当前时间的 LocalDateTime 对象
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return dateToLocalDateTime(getCurrentDate());
    }


    /**
     * 将月份转换成英文月份
     *
     * @return 返回包含月份数字(1-12)到英文月份名称映射的 Map
     */
    public static Map<Integer, String> getMonth() {
        Map<Integer, String> mothsMap = new HashMap<>();
        mothsMap.put(1, "January");
        mothsMap.put(2, "February");
        mothsMap.put(3, "March");
        mothsMap.put(4, "April");
        mothsMap.put(5, "May");
        mothsMap.put(6, "June");
        mothsMap.put(7, "July");
        mothsMap.put(8, "August");
        mothsMap.put(9, "September");
        mothsMap.put(10, "October");
        mothsMap.put(11, "November");
        mothsMap.put(12, "December");
        return mothsMap;
    }

    /**
     * 将月份转换成中文月份
     *
     * @param month 月份字符串（1-12）
     * @return 对应的中文月份名称
     */
    public static String getMonthCh(String month) {
        String[] month_ch = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
        return month_ch[Integer.parseInt(month) - 1];
    }

    /**
     * 获取指定日期的星期几
     *
     * @param pTime 格式为 "yyyy-MM-dd" 的日期字符串
     * @return 返回中文格式的星期几（如：星期一、星期二等）
     */
    public static String getWeekCh(String pTime) {
        String[] weeks = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return weeks[dayForWeek - 1];
    }

    //=================================== conversion date ===================================


    /**
     * 获取时间戳的下一天
     *
     * @param timestamp 时间戳对象
     * @return 返回下一天的 23:59:59 时刻的 Date 对象
     */
    public static Date getDateByTimestampNextDay(Timestamp timestamp) {
        Date date = new Date(timestamp.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return calendar.getTime();
    }

    /**
     * 将 Date 对象转换为 Timestamp 对象
     *
     * @param date 要转换的 Date 对象
     * @return 转换后的 Timestamp 对象
     */
    public static Timestamp dateToTimeStamp(Date date) {
        String time = FULL_TIME_FORMAT_TEMPLATE.format(date);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        try {
            ts = Timestamp.valueOf(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }

    /**
     * 将 Timestamp 对象转换为 Date 对象
     *
     * @param timestamp 要转换的 Timestamp 对象
     * @return 转换后的 Date 对象
     */
    public static Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }


    /**
     * 将 LocalDate 对象转换为 Timestamp 对象
     *
     * @param localDate 要转换的 LocalDate 对象
     * @return 转换后的 Date 对象
     */
    public static Date localDateToTimestamp(LocalDate localDate) {
        Date from = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return dateToTimeStamp(from);
    }

    /**
     * 将 LocalDateTime 对象转换为 Timestamp 对象
     *
     * @param localDateTime 要转换的 LocalDateTime 对象
     * @return 转换后的 Date 对象
     */
    public static Date localDateTimeToTimestamp(LocalDateTime localDateTime) {
        Date from = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return dateToTimeStamp(from);
    }

    /**
     * 将 LocalDate 对象转换为 Date 对象
     *
     * @param localDate 要转换的 LocalDate 对象
     * @return 转换后的 Date 对象
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 LocalDateTime 对象转换为 Date 对象
     *
     * @param localDateTime 要转换的 LocalDateTime 对象
     * @return 转换后的 Date 对象
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 Date 对象转换为 LocalDate 对象
     *
     * @param date 要转换的 Date 对象
     * @return 转换后的 LocalDate 对象
     */
    public static LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将 Date 对象转换为 LocalDateTime 对象
     *
     * @param date 要转换的 Date 对象
     * @return 转换后的 LocalDateTime 对象
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将时间戳转换为 LocalDateTime 对象
     *
     * @param time 时间戳（毫秒）
     * @return 转换后的 LocalDateTime 对象
     */
    public static LocalDateTime timeToLocalDateTime(Long time) {
        Date date = timestampToDate(new Timestamp(time));
        return dateToLocalDateTime(date);
    }


    /**
     * 将时间格式化为指定格式的字符串
     *
     * @param format 日期格式模板
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static synchronized String parseDataString(String format, Date date) {
        if (StringUtil.isNotBlank(format)) {
            return dateToString(format, date);
        } else {
            return dateToString(date);
        }
    }


    /**
     * 将时间格式化为指定格式的字符串
     *
     * @param format 日期格式模板
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static synchronized String dateToString(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return (date == null || StringPool.EMPTY.equals(date.toString())) ? StringPool.EMPTY : simpleDateFormat.format(date);
    }


    /**
     * 将时间格式化为默认格式的字符串
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     */
    public static synchronized String dateToString(Date date) {
        return (date == null || StringPool.EMPTY.equals(date.toString())) ? StringPool.EMPTY : FULL_TIME_FORMAT_TEMPLATE.format(date);
    }

    /**
     * 将字符串解析为 Date 对象
     *
     * @param str 要解析的日期字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 解析后的 Date 对象，如果解析失败返回 null
     */
    public static synchronized Date stringToDate(String str) {
        try {
            return FULL_TIME_FORMAT_TEMPLATE.parse(str);
        } catch (ParseException ignore) {
        }
        return null;
    }

    /**
     * 将字符串按指定格式解析为 Date 对象
     *
     * @param format 日期格式模板
     * @param str 要解析的日期字符串
     * @return 解析后的 Date 对象，如果解析失败返回 null
     */
    public static synchronized Date stringToDate(String format, String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if (StringUtil.isBlank(format)) {
            simpleDateFormat =  FULL_TIME_FORMAT_TEMPLATE;
        }
        try {
            return simpleDateFormat.parse(str);
        } catch (ParseException ignore) {
        }
        return null;
    }

    /**
     * 将字符串转换为 Timestamp 对象
     *
     * @param str 要转换的日期字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 转换后的 Timestamp 对象，如果转换失败返回 null
     */
    public static Timestamp stringToTimeStamp(String str) {
        Date date;
        try {
            date = FULL_TIME_FORMAT_TEMPLATE.parse(str);
        } catch (ParseException e) {
            try {
                date = FULL_TIME_FORMAT_TEMPLATE.parse(str);
            } catch (ParseException e1) {
                return null;
            }
        }
        String time = FULL_TIME_FORMAT_TEMPLATE.format(date);
        return Timestamp.valueOf(time);
    }

    //=================================== get pressDate ===================================


    /**
     * 计算从2017年6月1日到当前日期的天数差
     *
     * @return 返回天数差值
     */
    public static int getPressDateBetween20170601() {
        return getPressDateByYMD(2017, 6, 1);
    }

    /**
     * 计算从2023年4月16日到当前日期的天数差
     *
     * @return 返回天数差值
     */
    public static int getPressDate() {
        return getPressDateByYMD(null, null, null);
    }

    /**
     * 计算从2023年4月16日到指定日期的天数差
     *
     * @param dateStr 日期字符串，格式为 "yyyy-MM-dd"
     * @return 返回天数差值
     * @throws IllegalArgumentException 当日期格式不正确时抛出
     */
    public static int getPressDateByDateStr(String dateStr) {
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(dateStr);
        Assert.isTrue(matcher.find(), "$year-$month-$day");
        String[] dateStrArray = matcher.group(0).split(StringPool.DASH);
        Integer year = ParseUtil.toInt(dateStrArray[0]);
        Integer month = ParseUtil.toInt(dateStrArray[1]);
        Integer day = ParseUtil.toInt(dateStrArray[2]);
        return getPressDateByYMD(year, month, day);
    }

    /**
     * 计算从2023年4月16日到指定日期的天数差
     *
     * @param year 年份
     * @param month 月份（1-12）
     * @param day 日期（1-31）
     * @return 返回天数差值
     */
    public static int getPressDateByYMD(Integer year, Integer month, Integer day) {
        Calendar pressData = Calendar.getInstance();
        pressData.set(Calendar.YEAR, PRESS_DATA_YEAR);
        pressData.set(Calendar.MONTH, PRESS_DATA_MONTH);
        pressData.set(Calendar.DAY_OF_MONTH, PRESS_DATA_DAY);
        long time1 = pressData.getTimeInMillis();
        Calendar cd = Calendar.getInstance();
        if (Objects.nonNull(year) && Objects.nonNull(month) && Objects.nonNull(day)) {
            cd.set(Calendar.YEAR, year);
            cd.set(Calendar.MONTH, month - 1);
            cd.set(Calendar.DAY_OF_MONTH, day);
        }
        cd.set(Calendar.HOUR_OF_DAY, 0);
        cd.set(Calendar.MINUTE, 0);
        cd.set(Calendar.SECOND, 0);
        long time2 = cd.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days + 1));
    }

    /**
     * 计算指定日期距离当前日期的天数差
     *
     * @param date 要计算的日期时间戳
     * @return 返回天数差值
     */
    public static int pressDateBetweenToDay(Timestamp date) {
        Calendar pressDate = Calendar.getInstance();
        int press_date = getPressDate();
        pressDate.setTimeInMillis(date.getTime());
        int reDate = DateUtil.getPressDateByYMD(pressDate.get(Calendar.YEAR), pressDate.get(Calendar.MONTH), pressDate.get(Calendar.DATE));
        return press_date - reDate;
    }

    /**
     * 根据 pressDate 获取对应的日期
     *
     * @param pressDate 天数差值
     * @return 返回对应的 Date 对象
     */
    public static Date getDateByPressDate(Integer pressDate) {
        Calendar pressData = Calendar.getInstance();
        pressData.set(Calendar.YEAR, PRESS_DATA_YEAR);
        pressData.set(Calendar.MONTH, PRESS_DATA_MONTH);
        pressData.set(Calendar.DAY_OF_MONTH, PRESS_DATA_DAY);
        pressData.add(Calendar.DATE, pressDate);
        return pressData.getTime();
    }

    //=================================== calculate date ===================================

    /**
     * 计算两个日期之间相差的小时数
     *
     * @param target 目标时间
     * @param now 当前时间
     * @return 返回相差的小时数
     */
    public static int aHoursB(Date target, Date now) {
        long l = now.getTime() - target.getTime();
        return ParseUtil.toInt(l / (60 * 60 * 1000));
    }

    /**
     * 计算两个时间戳之间相差的小时数
     *
     * @param target 目标时间戳
     * @param now 当前时间戳
     * @return 返回相差的小时数
     */
    public static int aHoursB(Timestamp target, Timestamp now) {
        Date oldTimeDate = timestampToDate(target);
        Date newTimeDate = timestampToDate(now);
        return aHoursB(oldTimeDate, newTimeDate);
    }

    /**
     * 计算两个日期之间相差的分钟数
     *
     * @param target 目标时间
     * @param now 当前时间
     * @return 返回相差的分钟数
     */
    public static long aMinutesB(Date target, Date now) {
        long l = now.getTime() - target.getTime();
        return l / (60 * 1000);
    }

    /**
     * 计算两个时间戳之间相差的分钟数
     *
     * @param target 目标时间戳
     * @param now 当前时间戳
     * @return 返回相差的分钟数
     */
    public static long aMinutesB(Timestamp target, Timestamp now) {
        Date oldTimeDate = timestampToDate(target);
        Date newTimeDate = timestampToDate(now);
        return aMinutesB(oldTimeDate, newTimeDate);
    }

    /**
     * 计算两个日期之间相差的秒数
     *
     * @param target 目标时间
     * @param now 当前时间
     * @return 返回相差的秒数
     */
    public static long aSecondsB(Date target, Date now) {
        long l = now.getTime() - target.getTime();
        return l / (1000);
    }

    /**
     * 计算两个时间戳之间相差的秒数
     *
     * @param target 目标时间戳
     * @param now 当前时间戳
     * @return 返回相差的秒数
     */
    public static long aSecondsB(Timestamp target, Timestamp now) {
        Date oldTimeDate = timestampToDate(target);
        Date newTimeDate = timestampToDate(now);
        return aSecondsB(oldTimeDate, newTimeDate);
    }

    /**
     * 判断时间戳是否是昨天
     *
     * @param oldTime 较小的时间戳
     * @param newTime 较大的时间戳（如果为空则使用当前时间）
     * @return 返回 YesterdayEnum 枚举值，表示时间关系
     */
    public static YesterdayEnum isYesterday(Timestamp oldTime, Timestamp newTime) {
        Date oldTimeDate = timestampToDate(oldTime);
        Date newTimeDate = timestampToDate(newTime);
        return isYesterday(oldTimeDate, newTimeDate);
    }

    /**
     * 判断日期是否是昨天
     *
     * @param oldTime 较小的日期
     * @param newTime 较大的日期（如果为空则使用当前时间）
     * @return 返回 YesterdayEnum 枚举值，表示时间关系
     */
    public static YesterdayEnum isYesterday(Date oldTime, Date newTime) {
        if (newTime == null) {
            newTime = new Date();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = format.format(newTime);
        Date today = null;
        try {
            today = format.parse(todayStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 判断到底是哪天
        assert today != null;
        if ((today.getTime() - oldTime.getTime()) > 0 && (today.getTime() - oldTime.getTime()) <= 86400000) {
            // 昨天
            return YesterdayEnum.YESTERDAY;
        } else if ((today.getTime() - oldTime.getTime()) <= 0) {
            // 至少是今天
            return YesterdayEnum.SAME_DAY;
        } else {
            // 至少是前天
            return YesterdayEnum.LEAST_THE_DAY_BEFORE_YESTERDAY;
        }

    }

    /**
     * 获取日期中的月份
     *
     * @param date 日期对象
     * @return 返回两位数的月份字符串（01-12）
     */
    public static String getMonth(Date date) {
        DateFormat f_month = new SimpleDateFormat("MM");
        return f_month.format(date);
    }

    /**
     * 获取日期中的日
     *
     * @param date 日期对象
     * @return 返回两位数的日期字符串（01-31）
     */
    public static String getDay(Date date) {
        DateFormat f_day = new SimpleDateFormat("dd");
        return f_day.format(date);
    }

    /**
     * 获取日期中的小时
     *
     * @param date 日期对象
     * @return 返回两位数的小时字符串（00-23）
     */
    public static String getHour(Date date) {
        DateFormat f_day = new SimpleDateFormat("HH");
        return f_day.format(date);
    }

    /**
     * 获取日期中的分钟
     *
     * @param date 日期对象
     * @return 返回两位数的分钟字符串（00-59）
     */
    public static String getMinutes(Date date) {
        DateFormat f_day = new SimpleDateFormat("mm");
        return f_day.format(date);
    }

    /**
     * 在日期上添加指定的秒数
     *
     * @param date 原始日期
     * @param seconds 要添加的秒数
     * @return 返回添加秒数后的 Timestamp 对象
     */
    public static Timestamp addSeconds(Date date, int seconds) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.SECOND, seconds);
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在日期上添加指定的分钟数
     *
     * @param date 原始日期
     * @param minutes 要添加的分钟数
     * @return 返回添加分钟数后的 Timestamp 对象
     */
    public static Timestamp addMinutes(Date date, int minutes) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MINUTE, minutes);
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在日期上添加指定的小时数
     *
     * @param date 原始日期
     * @param hours 要添加的小时数
     * @return 返回添加小时数后的 Timestamp 对象
     */
    public static Timestamp addHours(Date date, int hours) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, hours);
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在日期上添加指定的天数
     *
     * @param date 原始日期
     * @param day 要添加的天数
     * @return 返回添加天数后的 Timestamp 对象
     */
    public static Timestamp addDays(Date date, int day) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, day * 24);
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在日期上回滚指定的秒数
     *
     * @param date 原始日期
     * @param seconds 要回滚的秒数
     * @return 返回回滚秒数后的 Timestamp 对象
     */
    public static Timestamp rollSeconds(Date date, int seconds) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.SECOND, -1 * seconds);
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在日期上回滚指定的分钟数
     *
     * @param date 原始日期
     * @param minutes 要回滚的分钟数
     * @return 返回回滚分钟数后的 Timestamp 对象
     */
    public static Timestamp rollMinutes(Date date, int minutes) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MINUTE, -1 * minutes);
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在日期上回滚指定的小时数
     *
     * @param date 原始日期
     * @param hours 要回滚的小时数
     * @return 返回回滚小时数后的 Timestamp 对象
     */
    public static Timestamp rollHours(Date date, int hours) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, -1 * hours);
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在日期上回滚指定的天数
     *
     * @param date 原始日期
     * @param day 要回滚的天数
     * @return 返回回滚天数后的 Timestamp 对象
     */
    public static Timestamp rollDays(Date date, int day) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, -1 * (day * 24));
        return dateToTimeStamp(ca.getTime());
    }

    /**
     * 在 LocalDateTime 上添加指定的秒数
     *
     * @param date 原始 LocalDateTime
     * @param seconds 要添加的秒数
     * @return 返回添加秒数后的 LocalDateTime 对象
     */
    public static LocalDateTime addSeconds(LocalDateTime date, int seconds) {
        return dateToLocalDateTime(timestampToDate(addSeconds(localDateTimeToDate(date), seconds)));
    }

    /**
     * 在 LocalDateTime 上添加指定的分钟数
     *
     * @param date 原始 LocalDateTime
     * @param minutes 要添加的分钟数
     * @return 返回添加分钟数后的 LocalDateTime 对象
     */
    public static LocalDateTime addMinutes(LocalDateTime date, int minutes) {
        return dateToLocalDateTime(timestampToDate(addMinutes(localDateTimeToDate(date), minutes)));
    }

    /**
     * 在 LocalDateTime 上添加指定的小时数
     *
     * @param date 原始 LocalDateTime
     * @param hours 要添加的小时数
     * @return 返回添加小时数后的 LocalDateTime 对象
     */
    public static LocalDateTime addHours(LocalDateTime date, int hours) {
        return dateToLocalDateTime(timestampToDate(addHours(localDateTimeToDate(date), hours)));
    }

    /**
     * 在 LocalDateTime 上添加指定的天数
     *
     * @param date 原始 LocalDateTime
     * @param day 要添加的天数
     * @return 返回添加天数后的 LocalDateTime 对象
     */
    public static LocalDateTime addDays(LocalDateTime date, int day) {
        return dateToLocalDateTime(timestampToDate(addDays(localDateTimeToDate(date), day)));
    }

    /**
     * 在 LocalDateTime 上回滚指定的秒数
     *
     * @param date 原始 LocalDateTime
     * @param seconds 要回滚的秒数
     * @return 返回回滚秒数后的 LocalDateTime 对象
     */
    public static LocalDateTime rollSeconds(LocalDateTime date, int seconds) {
        return dateToLocalDateTime(timestampToDate(rollSeconds(localDateTimeToDate(date), seconds)));
    }

    /**
     * 在 LocalDateTime 上回滚指定的分钟数
     *
     * @param date 原始 LocalDateTime
     * @param minutes 要回滚的分钟数
     * @return 返回回滚分钟数后的 LocalDateTime 对象
     */
    public static LocalDateTime rollMinutes(LocalDateTime date, int minutes) {
        return dateToLocalDateTime(timestampToDate(rollMinutes(localDateTimeToDate(date), minutes)));
    }

    /**
     * 在 LocalDateTime 上回滚指定的小时数
     *
     * @param date 原始 LocalDateTime
     * @param hours 要回滚的小时数
     * @return 返回回滚小时数后的 LocalDateTime 对象
     */
    public static LocalDateTime rollHours(LocalDateTime date, int hours) {
        return dateToLocalDateTime(timestampToDate(rollHours(localDateTimeToDate(date), hours)));
    }

    /**
     * 在 LocalDateTime 上回滚指定的天数
     *
     * @param date 原始 LocalDateTime
     * @param day 要回滚的天数
     * @return 返回回滚天数后的 LocalDateTime 对象
     */
    public static LocalDateTime rollDays(LocalDateTime date, int day) {
        return dateToLocalDateTime(timestampToDate(rollDays(localDateTimeToDate(date), day)));
    }

    /**
     * 记录业务逻辑执行耗时
     *
     * @param log 日志对象
     * @param logTitle 日志标题
     * @param bizHandler 要执行的业务逻辑
     */
    public static void logCostTime(Logger log, String logTitle, SerializableClosure bizHandler) {
        long start = System.currentTimeMillis();
        bizHandler.accept();
        long end = System.currentTimeMillis();
        String logString = StringUtil.concat(logTitle, " cost time = {}");
        log.info(logString, end - start);
    }

    /**
     * 检查时间戳字符串是否合法
     *
     * @param timeStamp 要检查的时间戳字符串
     * @return 如果是13位数字的时间戳返回 true，否则返回 false
     */
    public static boolean isLegalTimeStampString(String timeStamp) {
        return StringUtil.isNotEmpty(timeStamp) && timeStamp.length() == 13;
    }

    /**
     * 检查时间戳是否在指定分钟数范围内
     *
     * @param timeStamp 要检查的时间戳字符串
     * @param timeLimit 时间范围（分钟）
     * @return 如果在指定范围内返回 true，否则返回 false
     */
    public static boolean isInMinuteTime(String timeStamp, int timeLimit) {
        if (StringUtil.isEmpty(timeStamp))
            return false;
        Calendar calendar = Calendar.getInstance();
        long timeStamp_long = ParseUtil.toLong(timeStamp);
        long now = calendar.getTime().getTime();
        long s = (Math.abs(now - timeStamp_long)) / (1000 * 60);
        return s <= timeLimit;
    }

    /**
     * 格式化 LocalDateTime 为指定格式的字符串
     *
     * @param localDateTime 要格式化的 LocalDateTime 对象
     * @param pattern 日期格式模板
     * @return 格式化后的日期字符串
     */
    public static String dateFormat(LocalDateTime localDateTime, String pattern) {
        if (Objects.isNull(localDateTime)) {
            return StringPool.EMPTY;
        }
        SimpleDateFormat TEMPLATE = new SimpleDateFormat(pattern);
        return TEMPLATE.format(localDateTimeToDate(localDateTime));
    }

    /**
     * 格式化 LocalDateTime 为默认格式的字符串
     *
     * @param localDateTime 要格式化的 LocalDateTime 对象
     * @return 格式化后的日期字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     */
    public static String dateFormat(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return StringPool.EMPTY;
        }
        return FULL_TIME_FORMAT_TEMPLATE.format(localDateTimeToDate(localDateTime));
    }

    /**
     * 判断时间 A 是否早于时间 B
     *
     * @param aTime 时间 A
     * @param bTime 时间 B
     * @return 如果 A 早于 B 返回 true，否则返回 false
     */
    public static Boolean aBeforeB(LocalDateTime aTime, LocalDateTime bTime) {

        int val = bTime.compareTo(aTime);
        return val > 0;
    }

    /**
     * 判断时间 A 是否晚于时间 B
     *
     * @param aTime 时间 A
     * @param bTime 时间 B
     * @return 如果 A 晚于 B 返回 true，否则返回 false
     */
    public static Boolean aAfterB(LocalDateTime aTime, LocalDateTime bTime) {
        int val = bTime.compareTo(aTime);
        return val < 0;
    }

    /**
     * 判断时间 A 是否等于时间 B
     *
     * @param aTime 时间 A
     * @param bTime 时间 B
     * @return 如果 A 等于 B 返回 true，否则返回 false
     */
    public static Boolean aEqB(LocalDateTime aTime, LocalDateTime bTime) {
        int val = bTime.compareTo(aTime);
        return (Objects.equals(val, 0));
    }

    /**
     * 计算两个日期之间的时间差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param unit 时间单位
     * @return 返回指定单位的时间差
     */
    public static long between(Date beginDate, Date endDate, DateUnit unit) {
        return between(beginDate, endDate, unit, true);
    }

    /**
     * 计算两个日期之间的时间差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param unit 时间单位
     * @param isAbs 是否取绝对值
     * @return 返回指定单位的时间差
     */
    public static long between(Date beginDate, Date endDate, DateUnit unit, boolean isAbs) {
        return (new DateBetween(beginDate, endDate, isAbs)).between(unit);
    }


    /**
     * 格式化两个日期之间的时间差
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param level 格式化级别
     * @return 返回格式化后的时间差字符串
     */
    public static String formatBetween(Date beginDate, Date endDate, BetweenFormatter.Level level) {
        return formatBetween(between(beginDate, endDate, DateUnit.MS), level);
    }

    /**
     * 格式化两个日期之间的时间差（使用默认级别）
     *
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 返回格式化后的时间差字符串
     */
    public static String formatBetween(Date beginDate, Date endDate) {
        return formatBetween(between(beginDate, endDate, DateUnit.MS));
    }

    /**
     * 格式化时间差
     *
     * @param betweenMs 时间差（毫秒）
     * @param level 格式化级别
     * @return 返回格式化后的时间差字符串
     */
    public static String formatBetween(long betweenMs, BetweenFormatter.Level level) {
        return (new BetweenFormatter(betweenMs, level)).format();
    }

    /**
     * 格式化时间差（使用默认级别）
     *
     * @param betweenMs 时间差（毫秒）
     * @return 返回格式化后的时间差字符串
     */
    public static String formatBetween(long betweenMs) {
        return (new BetweenFormatter(betweenMs, BetweenFormatter.Level.MILLISECOND)).format();
    }

}
