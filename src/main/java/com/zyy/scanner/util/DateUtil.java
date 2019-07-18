package com.zyy.scanner.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类
 *
 * @author baofengXu
 * @date 2018/12/13
 * @since sdk 2.0
 */
public final class DateUtil {
    /*** 时区 */
    private static final String ZONE = "+8";

    private static final int[] DAY_OF_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String ALL_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String YM_PATTERN = "yyyy-MM";
    /*** 分隔符 ***/
    private static final String SEPARATOR_ONE = "-";
    private static final String SEPARATOR_TWO = "/";

    /*** "yyyy-MM-dd HH:mm:ss" */
    public static final DateTimeFormatter ALL_PATTERN_FORMATTER = DateTimeFormatter.ofPattern(ALL_PATTERN);
    /*** HH:mm */
    public static final DateTimeFormatter HH_MM_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * 以传入的日期为基数，计算数个月后的日期，如果计算数月前则传入负数
     *
     * @param date        日期
     * @param mouthAmount 月份数量
     * @return 计算后的日期
     */
    public static Date addMouth(Date date, int mouthAmount) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, mouthAmount);
        return calendar.getTime();
    }

    /**
     * 以传入的日期为基数，计算指定天数后的日期，如果计算多少天前的日期则传入负数
     *
     * @param date      日期
     * @param dayAmount 天数
     * @return 计算后的日期
     */
    public static Date addDay(Date date, int dayAmount) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, dayAmount);
        return calendar.getTime();
    }

    /**
     * 以传入的日期为基数，计算数小时后的日期，如果计算数小时前的日期则传入负数
     *
     * @param date       日期
     * @param hourAmount 小时数
     * @return 计算后的日期
     */
    public static Date addHour(Date date, int hourAmount) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(10, hourAmount);
        return calendar.getTime();
    }

    /**
     * 以传入的日期为基数，计算数分钟后的日期，如果计算数分钟前的日期则传入负数
     *
     * @param date         日期
     * @param minuteAmount 分钟数
     * @return 计算后的日期
     */
    public static Date addMinute(Date date, int minuteAmount) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(12, minuteAmount);
        return calendar.getTime();
    }

    /**
     * 以分钟维度对两个时间进行比较
     *
     * @param date        时间1
     * @param anotherDate 时间2
     * @return <li> 0: 两个时间相等 </li><li> 1: 时间1大于时间2 </li><li> -1: 时间2大于时间1 </li>
     */
    public static int compareHourAndMinute(Date date, Date anotherDate) {
        if (date == null) {
            date = new Date();
        }

        if (anotherDate == null) {
            anotherDate = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hourOfDay1 = cal.get(11);
        int minute1 = cal.get(12);

        cal.setTime(anotherDate);
        int hourOfDay2 = cal.get(11);
        int minute2 = cal.get(12);

        if (hourOfDay1 > hourOfDay2) {
            return 1;
        }

        if (hourOfDay1 == hourOfDay2) {
            return ((minute1 == minute2) ? 0 : (minute1 > minute2) ? 1 : -1);
        }

        return -1;
    }

    /**
     * 以秒维度对两个时间进行比较
     *
     * @param date        时间1
     * @param anotherDate 时间2
     * @return <li> 0: 两个时间相等 </li><li> 1: 时间1大于时间2 </li><li> -1: 时间2大于时间1 </li>
     */
    public static int compareIgnoreSecond(Date date, Date anotherDate) {
        if (date == null) {
            date = new Date();
        }

        if (anotherDate == null) {
            anotherDate = new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(13, 0);
        cal.set(14, 0);
        date = cal.getTime();

        cal.setTime(anotherDate);
        cal.set(13, 0);
        cal.set(14, 0);
        anotherDate = cal.getTime();

        return date.compareTo(anotherDate);
    }

    /**
     * 获取当前日期字符串，格式 yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String currentDate2String() {
        return date2String(new Date());
    }

    /**
     * 获取当前日期字符串，格式 yyyy-MM-dd
     */
    public static String currentDate2StringByDay() {
        return date2StringByDay(new Date());
    }

    /**
     * 获取当天结束时间，常用于时间区间比较，例如 2019-02-25 23:59:59.999
     */
    public static Date currentEndDate() {
        return getEndDate(new Date());
    }

    /**
     * 获取当天起始时间，常用于时间区间比较，例如 2019-02-25 00:00:00.000
     */
    public static Date currentStartDate() {
        return getStartDate(new Date());
    }

    /**
     * 时间转字符串，转换格式 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param date 待转换的时间
     */
    public static String date2String(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * 时间转字符串，根据传入的格式进行转换
     *
     * @param date    待转换的时间
     * @param pattern 时间格式，例如 yyyy-MM-dd HH:mm:ss.SSS 或 yyyy-MM-dd 等
     */
    public static String date2String(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 时间转字符串，转换格式 yyyy
     *
     * @param date 待转换的时间
     */
    public static String date2StringByYY(Date date) {
        return date2String(date, "yyyy");
    }

    /**
     * 时间转字符串，转换格式 yyyy-MM
     *
     * @param date 待转换的时间
     */
    public static String date2StringByYM(Date date) {
        return date2String(date, YM_PATTERN);
    }

    /**
     * JDK8 时间转字符串，转换格式 yyyy-MM
     *
     * @param date 待转换的时间
     */
    public static String date2StringByYM(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(YM_PATTERN));
    }

    /**
     * 时间转字符串，转换格式 yyyy-MM-dd
     *
     * @param date 待转换的时间
     */
    public static String date2StringByDay(Date date) {
        return date2String(date, "yyyy-MM-dd");
    }

    /**
     * 时间转字符串，转换格式 MM
     *
     * @param date 待转换的时间
     */
    public static String date2StringByMM(Date date) {
        return date2String(date, "MM");
    }

    /**
     * 时间转字符串，转换格式 dd
     *
     * @param date 待转换的时间
     */
    public static String date2StringByDD(Date date) {
        return date2String(date, "dd");
    }

    /**
     * 时间转字符串，转换格式 MM-dd
     *
     * @param date 待转换的时间
     */
    public static String date2StringByMD(Date date) {
        return date2String(date, "MM-dd");
    }

    /**
     * 时间转字符串，转换格式 HH:mm
     *
     * @param date 待转换的时间
     */
    public static String date2StringByHM(Date date) {
        return date2String(date, "HH:mm");
    }

    /**
     * 时间转字符串，转换格式 HH点mm分
     *
     * @param date 待转换的时间
     */
    public static String date2StringByOnOff(Date date) {
        return date2String(date, "HH点mm分");
    }

    /**
     * 时间转字符串，转换格式 yyy-MM-dd HH:mm
     *
     * @param date 待转换的时间
     */
    public static String date2StringByMinute(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm");
    }

    /**
     * 时间转字符串，转换格式 " HH:mm:ss"
     *
     * @param date 待转换的时间
     */
    public static String date2StringByHMS(Date date) {
        return date2String(date, " HH:mm:ss");
    }

    /**
     * 时间转字符串，转换格式 " HH:mm:ss.SSS"
     *
     * @param date 待转换的时间
     */
    public static String date2StringByHMSS(Date date) {
        return date2String(date, " HH:mm:ss.SSS");
    }

    /**
     * 时间转字符串，转换格式 "yyyy-MM-dd HH:mm:ss"
     *
     * @param date 待转换的时间
     */
    public static String date2StringBySecond(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 指定年月日，返回Date对象
     *
     * @param year  年
     * @param month 月
     * @param date  日
     */
    public static Date getDate(int year, int month, int date) {
        return getDate(year, month, date, 0, 0);
    }

    /**
     * 指定年月日小时分钟，返回Date对象
     *
     * @param year      年
     * @param month     月
     * @param date      日
     * @param hourOfDay 小时
     * @param minute    分钟
     */
    public static Date getDate(int year, int month, int date, int hourOfDay,
                               int minute) {
        return getDate(year, month, date, hourOfDay, minute, 0);
    }

    /**
     * 指定年月日小时分钟秒，返回Date对象
     *
     * @param year      年
     * @param month     月
     * @param date      日
     * @param hourOfDay 小时
     * @param minute    分钟
     * @param second    秒
     */
    public static Date getDate(int year, int month, int date, int hourOfDay,
                               int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, date, hourOfDay, minute, second);
        cal.set(14, 0);

        return cal.getTime();
    }

    /**
     * 返回当前是周几，从周日开始计算
     *
     * @param date 待转换的日期
     * @return 周日对应1，周一对应2，依次类推
     * @see Calendar#SUNDAY
     * @see Calendar#MONDAY
     * @see Calendar#TUESDAY
     * @see Calendar#WEDNESDAY
     * @see Calendar#THURSDAY
     * @see Calendar#FRIDAY
     * @see Calendar#SATURDAY
     */
    public static Integer getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Date getEndDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(11, 23);
        cal.set(12, 59);
        cal.set(13, 59);
        cal.set(14, 999);

        return cal.getTime();
    }

    /**
     * 查询某年某月有多少天
     *
     * @param year  年
     * @param month 从0开始的月份
     */
    public static int getMaxDayOfMonth(int year, int month) {
        if ((month == 1) && (isLeapYear(year))) {
            return 29;
        }

        return DAY_OF_MONTH[month];
    }

    /**
     * 根据传入的时间获取下一天
     *
     * @date 日期
     */
    public static Date getNextDay(Date date) {
        return addDay(date, 1);
    }

    /**
     * 获取传入日期的起始时间，例如 2019-02-25 00:00:00.000
     *
     * @param date 待处理日期
     */
    public static Date getStartDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);

        return cal.getTime();
    }

    /**
     * 主机运行时间换算成秒
     *
     * @param zjhrs 小时
     * @param zjmin 分钟
     * @param zjsec 秒
     * @return
     */
    public static Long setTime(Integer zjhrs, Integer zjmin, Integer zjsec) {
        Long second = 0L;
        second = Long.valueOf(zjhrs * 60 * 60 + zjmin * 60 + zjsec);
        return second;
    }

    /**
     * 时间转字符串，转换格式 HH:mm:ss
     *
     * @param date 待转换的时间
     */
    public static String getTime(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    /**
     * 时间转字符串，转换格式 HH:mm
     *
     * @param date 待转换的时间
     */
    public static String getTimeIgnoreSecond(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    /**
     * 判断是否闰年
     *
     * @param year 年份
     */
    public static boolean isLeapYear(int year) {
        Calendar calendar = Calendar.getInstance();
        return ((GregorianCalendar) calendar).isLeapYear(year);
    }

    /**
     * 字符串时间转Date对象，转换格式 yyyy-MM-dd
     *
     * @param str 待转换的时间
     */
    public static Date string2Date(String str) {
        return string2Date(str, "yyyy-MM-dd");
    }

    /**
     * 字符串时间转Date对象，转换格式 yyyy
     *
     * @param str 待转换的时间
     */
    public static Date string2DateByY(String str) {
        return string2Date(str, "yyyy");
    }

    /**
     * 字符串时间转Date对象
     *
     * @param str     待转换的时间
     * @param pattern 日期格式
     */
    public static Date string0Date(String str, String pattern) {
        if (null == str || "".equals(str)) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = dateFormat.parse(str);
        } catch (ParseException localParseException) {
        }
        return date;
    }

    /**
     * 字符串时间转Date对象，根据传入的格式自动转换，支持 yyyy-MM-dd HH:mm:ss ， yyyy/MM/dd HH:mm:ss
     *
     * @param str 待转换的时间
     */
    public static Date string0DateTime(String str) {
        if (str.split(SEPARATOR_ONE).length > 1) {
            return string2Date(str, "yyyy-MM-dd HH:mm:ss");
        } else if (str.split(SEPARATOR_TWO).length > 1) {
            return string2Date(str, "yyyy/MM/dd HH:mm:ss");
        }
        return string2Date(str, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 字符串时间转Date对象，转换格式 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param str 待转换的时间
     */
    public static Date string0DateTimeSSS(String str) {
        return string2Date(str, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * 字符串时间转Date对象，转换格式 yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param str 待转换的时间
     */
    public static Date string2Date(String str, String pattern) {
        if (null == str || "".equals(str)) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = dateFormat.parse(str);
        } catch (ParseException localParseException) {
        }
        return date;
    }

    /**
     * 字符串时间转Date对象，转换格式 yyyy-MM-dd HH:mm:ss
     *
     * @param str 待转换的时间
     */
    public static Date string2DateTime(String str) {
        return string2Date(str, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 根据传入的时间获取是第几周
     */
    public static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 根据传入的时间获取是第几周
     *
     * @param date 日期
     * @param type 查询类型，类型可以查看 Calendar 的常量
     * @see Calendar#YEAR
     * @see Calendar#MONTH
     * @see Calendar#WEEK_OF_YEAR
     * @see Calendar#WEEK_OF_MONTH
     * @see Calendar#DATE
     * @see Calendar#DAY_OF_MONTH
     * @see Calendar#DAY_OF_YEAR
     * @see Calendar#DAY_OF_WEEK ...
     */
    public static int get(Date date, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(type);
    }

    /**
     * 根据传入的时间获取年份
     */
    public static int getYear(Date date) {
        return get(date, Calendar.YEAR);
    }

    /**
     * 根据传入的时间获取月份
     */
    public static int getMonth(Date date) {
        return get(date, Calendar.MONTH);
    }

    /**
     * 根据传入的时间获取其所在月份第几天，1代表第一天
     */
    public static int getDay(Date date) {
        return get(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据年月获取月初时间
     *
     * @param year  年
     * @param month 月
     */
    public static Date getStartMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return getStartDate(calendar.getTime());
    }

    /**
     * 根据年月获取月末时间
     *
     * @param year  年
     * @param month 月
     */
    public static Date getEndMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, getMaxDayOfMonth(year, month));
        return getEndDate(calendar.getTime());
    }

    /**
     * 计算两个时间差值
     *
     * @param date1 时间基数
     * @param date2 比较值
     * @return 毫秒
     */
    public static long getTimeDifference(Date date1, Date date2) {
        long temp = date1.getTime() - date2.getTime();
        return temp;
    }

    /**
     * 字符串转java.sql.Date类型
     *
     * @param strDate
     * @return
     */
    public static java.sql.Date toDate(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }

    public static Date toDate(String strDate, String pattern) throws Exception {
        if (pattern == null || "".equals(pattern)) {
            pattern = DEFAULT_PATTERN;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(strDate);
    }

    public static String toString(Date date, String pattern) {
        if (pattern == null || "".equals(pattern)) {
            pattern = DEFAULT_PATTERN;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 字符串转java.sql.Time类型,例如：“12:00”或者“12:00:00”
     *
     * @param strDate
     * @return
     */
    public static java.sql.Time toTime(String strDate) {
        String str = strDate;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Time date = new java.sql.Time(d.getTime());
        return date;
    }

    /**
     * 获取系统当前日期【2018-06-14】
     *
     * @return
     */
    public static String getSysCurDateStr() {
        Date curDate = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdFormat.format(curDate);
        return dateStr;
    }

    /**
     * 获取系统当前日期【2018-06-14 16:30:00】---字符串格式
     *
     * @return
     */
    public static String getSysCurDateTimeStr() {
        Date curDate = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdFormat.format(curDate);
        return dateStr;
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    @Deprecated
    public static Date getSysCurDateTime() {
        return string0DateTime(getSysCurDateTimeStr());
    }

    /**
     * 获取系统当前日期【2018-06-14】
     *
     * @return
     */
    public static String getSysCurDate() {
        Date curDate = new Date();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdFormat.format(curDate);
        return dateStr;
    }

    /**
     * 获取时间的前一年月的第一天【2017-06-01】
     *
     * @param dateStr 日期字符串
     * @return 对应日期前一年月第一天Date对象
     */
    public static Date getPreYear(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date curDate = format.parse(dateStr);
            calendar.setTime(curDate);
            //年份-1
            calendar.add(Calendar.YEAR, -1);
            //设置月的第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;
    }

    /**
     * 获取时间对应月的第一天【2018-06-01】
     *
     * @param dateStr 日期字符串
     * @return 对应日期月份第一天的Date对象
     */
    public static Date getMonthFirstDay(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date curDate = format.parse(dateStr);
            calendar.setTime(curDate);
            //设置月的第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;
    }

    /**
     * 获取时间明年下个月的第一天【2019-07-01】
     *
     * @param dateStr 日期字符串
     * @return 对应日期下个月第一天Date对象
     */
    public static Date getNextYear(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date curDate = sdFormat.parse(dateStr);
            calendar.setTime(curDate);
            //年份+1
            calendar.add(Calendar.YEAR, 1);
            //月份+1
            calendar.add(Calendar.MONTH, 1);
            //设置月的第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前时间的下一个月第一天【2018-07-01】
     *
     * @param dateStr 日期字符串
     * @return 对应日期下一个月第一天Date对象
     */
    public static Date getNextMonth(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date curDate = sdFormat.parse(dateStr);
            calendar.setTime(curDate);
            //月份+1
            calendar.add(Calendar.MONTH, 1);
            //设置月的第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;
    }

    /**
     * 获取月份的最后一天【2018-06-30】
     *
     * @param dateStr 日期字符串
     * @return 对应日期月份的最后一天Date对象
     */
    public static Date getMonthLastDay(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date curDate = sdFormat.parse(dateStr);
            calendar.setTime(curDate);
            //月份+1
            calendar.add(Calendar.MONTH, 1);
            //设置月的第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            //设置月的最后一天
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return calendar.getTime();
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;
    }

    /**
     * 获取给定日期的前一天
     *
     * @param curDateStr 日期字符串
     * @return 对应日期前一天日期字符串
     */
    public static String getPreDay(String curDateStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date curDate = sdFormat.parse(curDateStr);
            calendar.setTime(curDate);
            //日-1
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return date2String(calendar.getTime(), "yyyy-MM-dd");
        } catch (ParseException px) {
            px.printStackTrace();
        }
        return null;
    }

    /**
     * Date对象类型转换
     *
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 获取指定日期的毫秒
     *
     * @param localDateTime
     * @return long milli
     */
    public static Long getLongMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取指定日期的秒
     *
     * @param localDateTime
     * @return long milli
     */
    public static Long getLongSecond(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 功能描述: 判断当前时间是否是今天
     * [inputJudgeDate]
     *
     * @return : java.lang.Boolean
     * @author : wangdong
     * @date : 2018/10/17 15:59
     */
    public static Boolean judgeToday(Date inputJudgeDate) {
        //获取当前系统时间
        long longDate = System.currentTimeMillis();
        Date nowDate = new Date(longDate);
        String format = date2StringBySecond(nowDate);
        String judegTodayFormat = date2StringBySecond(inputJudgeDate);
        String subDate = format.substring(0, 10);
        //定义每天的24h时间范围
        String beginTime = subDate + " 00:00:00";
        String endTime = subDate + " 23:59:59";
        Date paseBeginTime;
        Date paseEndTime;
        paseBeginTime = string2DateTime(beginTime);
        paseEndTime = string2DateTime(endTime);
        return (inputJudgeDate.after(paseBeginTime) && inputJudgeDate.before(paseEndTime)) || judegTodayFormat.equals(beginTime) || judegTodayFormat.equals(endTime);
    }

    /**
     * 功能描述: 获取时间差，格式为\天\时\分\秒
     * [beginTime, endTime]
     *
     * @return : java.lang.String
     * @author : wangdong
     * @date : 2018/10/22 10:17
     */
    public static String getTimeDiff(Date beginTime, Date endTime) {
        //获取毫秒数
        Long startLong = beginTime.getTime();
        Long endLong = endTime.getTime();
        //计算时间差,单位毫秒
        Long ms = endLong - startLong;
        String timeDiff = "0秒";
        //时间差转换为 \天\时\分\秒
        if (ms != 0) {
            timeDiff = longTimeToDay(ms);
        }
        return timeDiff;
    }

    /**
     * 功能描述: 转换函数
     * [ms]
     *
     * @return : java.lang.String
     * @author : wangdong
     * @date : 2018/11/7 14:04
     */
    private static String longTimeToDay(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append("天");
        }
        if (hour > 0) {
            sb.append(hour).append("小时");
        }
        if (minute > 0) {
            sb.append(minute).append("分");
        }
        if (second > 0) {
            sb.append(second).append("秒");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond).append("毫秒");
        }
        return sb.toString();
    }

    /**
     * 以年为维度添加或减去指定的时间
     *
     * @param date 日期
     * @param num  年数
     */
    public static Date getDateByYear(Date date, int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, num);
        Date nextDate = c.getTime();
        return nextDate;
    }

    /**
     * 获取给定时间的小时和分钟，并将其转换成秒数返回
     *
     * @param date
     * @return Integer
     * @author zhouyun
     * @date 2019/1/17 14:27
     */
    public static Integer getSecond(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return (hours * 60 + minutes) * 60;
    }

    /**
     * 时间戳转为string时间格式
     *
     * @param key
     * @return
     * @Auth lixiaoran
     * @Date 2019-04-18
     */
    public static String getStringByTimeStamp(Long key) {
        // 设置时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(key);
    }

    /**
     * 时间冲突检查， true 冲突, false 不冲突
     * 考虑了跨天的场景
     *
     * @param srcStart
     * @param srcEnd
     * @param distStart
     * @param distEnd
     * @return
     */
    public static boolean timeConflict(LocalTime srcStart, LocalTime srcEnd, LocalTime distStart, LocalTime distEnd) {
        // 开始 > src结束 && 结束 < src开始， 当中需要考虑跨天场景
        return ((srcEnd.isBefore(srcStart) || distStart.isBefore(srcEnd))
                && (distEnd.isBefore(distStart) || distEnd.isAfter(srcStart))) ||
                ((srcEnd.isBefore(srcStart) || srcStart.isBefore(distEnd))
                        && (distEnd.isBefore(distStart) || srcEnd.isAfter(distStart)));
    }

    /**
     * 根据当前时间戳获取当前时周几
     *
     * @param dt
     * @return
     * @throws
     * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
     */
    public static Integer getWeekByDate(Long dt) {
        return getWeekByDate(new Date(dt));
    }

    /**
     * 根据当前时间获取当前时周几
     *
     * @param dt
     * @return
     * @throws
     * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
     */
    public static Integer getWeekByDate(Date dt) {
        Integer[] weekDays = {7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 根据时间戳和天数获取同一时间
     *
     * @param time
     * @param span
     * @return
     * @throws Exception
     */
    public static Long getTimeByDateAndDaySpan(Long time, Integer span) throws Exception {
        return getTimeByDateAndDaySpan(new Timestamp(time), span);
    }

    /**
     * 根据时间和天数获取同一时间
     *
     * @param time
     * @param span
     * @return
     * @throws Exception
     */
    public static Long getTimeByDateAndDaySpan(Timestamp time, Integer span) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DATE, span);
        return calendar.getTimeInMillis();
    }

    /**
     * 根据 LocalDate & LocalTime 获取时间戳
     *
     * @param date
     * @param time
     * @return
     */
    public static Long getLongTimeByDataAndTime(LocalDate date, LocalTime time) {
        return getLongTimeByDataTime(LocalDateTime.of(date, time));
    }

    /**
     * 根据 LocalDateTime 获取时间戳
     *
     * @param dateTime
     * @return
     */
    public static Long getLongTimeByDataTime(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of(ZONE)).toEpochMilli();
    }

}