package cn.flood.base.core.date;



import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
* <p>Title: DateUtils</p>  
* <p>Description: 时间日期工具类</p>  
* @author mmdai  
* @date 2019年2月18日
 */
public final class DateUtils {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    
    public static final String YYYY_MM_DD_ = "yyyy/MM/dd";

    public static final String YYYYMMDD = "yyyyMMdd";
    
    public static final String YYMMDD = "yyMMdd";

    /**
     * 24小时时间正则表达式
     */
    public static final String MATCH_TIME_24 = "(([0-1][0-9])|2[0-3]):[0-5][0-9]:[0-5][0-9]";
    /**
     * 日期正则表达式
     */
    public static final String REGEX_DATA = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";


    public static String today;

    public static String yesterday;

    /**
     * Don't let anyone instantiate this class
     */
    private DateUtils() {
        throw new AssertionError("Cannot create instance");
    }
    
    /**
     * 
     * <p>Title: getCurrentDate</p>  
     * <p>Description: 获取当前时间</p>  
     * @return
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    /**
     * 
     * <p>Title: getString2Date</p>  
     * <p>Description: </p>  
     * @param format
     * @param str
     * @return
     */
    public static LocalDate getString2Date(String format, String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(str, formatter);
    }
    
    /**
     * 
     * <p>Title: getYesterdayDate</p>  
     * <p>Description: 获取昨天时间</p>  
     * @return
     */
    public static LocalDate getYesterdayDate() {
        return LocalDate.now().plusDays(-1);
    }
    
    /**
     * 计算从某个日期开始之后n天的日期
     * @param from 开始日期
     * @param n 
     * @return
     */
    public static LocalDate getNDayFromDate(LocalDate from, int n) {
        return from.plusDays(n);
    }


    /**
     * 计算从某个日期开始之后n月的日期
     * @param from 开始日期
     * @param n 
     * @return
     */
    public static LocalDate getNMonthDate(LocalDate from, int n) {
        return from.plusMonths(n);

    }

    /**
     * 计算从某个日期开始之后n周的日期
     * @param from 开始日期
     * @param n 
     * @return
     */
    public static LocalDate getNWeekDate(LocalDate from, int n) {
        return from.plusWeeks(n);
    }


    /**
     * 
     * <p>Title: getCurrentFormatDate</p>  
     * <p>Description: 获取当前时间格式化日期</p>  
     * @param formatDate
     * @return
     */
    public static String getCurrentFormatDate(String formatDate) {
        LocalDate date = getCurrentDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
        return date.format(formatter);
    }
    
    /**
     * 
     * <p>Title: getCurrentFormatDate</p>  
     * <p>Description: 获取当前格式：yyyy-MM-dd时间</p>
     * @return
     */
    public static String getCurrentFormatDate() {
    	LocalDate date = getCurrentDate();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return date.format(formatter);
    }
    
    /**
     * 
     * <p>Title: resetToday</p>  
     * <p>Description: 重置日期</p>
     */
    public static void resetToday() {
        String todayStr = getDate2String(YYYY_MM_DD, getCurrentDate());
        if ((today == null) || !today.equals(todayStr)) {
            today = todayStr;
            yesterday = getDate2String(YYYY_MM_DD, getYesterdayDate());
        }
    }
    /**
     * 
     * <p>Title: getDate2String</p>  
     * <p>Description: 格式化给定日期</p>  
     * @param format
     * @param date
     * @return
     */
    public static final String getDate2String(String format, LocalDate date) {
        if (date != null) {
            if (ObjectUtils.isEmpty(format)) {
                format = YYYY_MM_DD;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return date.format(formatter);
        } else {
            return "";
        }
    }

    /**
     *
     * @param format
     * @param dateStr
     * @return
     */
    public static LocalDate tryParseString2Date(String format, String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter);
    }
    /**
     *
     * <p>Title: getCurrentTimeMillis</p>
     * <p>Description: 获取当前时间戳</p>
     * @return
     */
    public static long getCurrentTimeMillis() {
        return Clock.systemDefaultZone().millis();
    }

    /**
     * 
     * <p>Title: getOffDays</p>  
     * <p>Description: 从某个时间戳到现在时间戳转换成天数</p>  
     * @param l
     * @return
     */
    public static int getOffDays(long l) {
        return getOffDays(l, getCurrentTimeMillis());
    }
    /**
     * 
     * <p>Title: getOffDays</p>  
     * <p>Description: 从某个时间戳到某个时间戳转换成天数</p>  
     * @param from
     * @param to
     * @return
     */
    public static int getOffDays(long from, long to) {
        return getOffMinutes(from, to) / 1440;
    }
    /**
     * 
     * <p>Title: getOffDays</p>  
     * <p>Description: 从某个时间戳到现在时间戳转换成分钟</p>  
     * @param l
     * @return
     */
    public static int getOffMinutes(long l) {
        return getOffMinutes(l, getCurrentTimeMillis());
    }
    /**
     * 
     * <p>Title: getOffMinutes</p>  
     * <p>Description: 从某个时间戳到某个时间戳转换成分钟</p>  
     * @param from
     * @param to
     * @return
     */
    public static int getOffMinutes(long from, long to) {
        return (int) ((to - from) / 60L);
    }

    /**
     * 获取n天后的日期
     * @param n
     * @param format
     * @return
     */
    public static String getLastNDayDate(int n, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.now().plusDays(n).format(formatter);
    }
    /**
     * <p>Title: getLastNWeekBeginDate</p>
     * <p>Description: 获取n个周后的第一天日期</p>
     * @param n
     * @param formatter
     * @return
     */
    public static String getLastNWeekBeginDate(int n, DateTimeFormatter formatter) {
        return LocalDate.now().plusWeeks(n).with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1).format(formatter);
    }

    /**
     * <p>Title: getLastNWeekBeginDate</p>
     * <p>Description: 获取n个周后的第一天日期</p>
     * @param format
     * @param dateStr
     * @param n
     * @return
     */
    public static String getLastNWeekBeginDate(String format, String dateStr, int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter).plusWeeks(n).with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1).format(formatter);
    }

    /**
     *
     * <p>Title: getLastNWeekBeginDate</p>
     * <p>Description: 获取n个周后的第一天日期</p>
     * @param n
     * @return
     */
    public static String getLastNWeekBeginDate(int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return getLastNWeekBeginDate(n, formatter);
    }

    /**
     * <p>Title: getLastNWeekEndDate</p>
     * <p>Description: 获取n个周后的最一天日期</p>
     * @param n
     * @param formatter
     * @return
     */
    public static String getLastNWeekEndDate(int n, DateTimeFormatter formatter) {
        return LocalDate.now().plusWeeks(n).with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 7).format(formatter);
    }

    /**
     * <p>Title: getLastNWeekEndDate</p>
     * <p>Description: 获取n个周后的最一天日期</p>
     * @param format
     * @param dateStr
     * @param n
     * @return
     */
    public static String getLastNWeekEndDate(String format, String dateStr, int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter).plusWeeks(n).with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 7).format(formatter);
    }

    /**
     * <p>Title: getLastNWeekEndDate</p>
     * <p>Description: 获取n个周后的最一天日期</p>
     * @param n
     * @return
     */
    public static String getLastNWeekEndDate(int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return getLastNWeekEndDate(n, formatter);
    }
    /**
     * 
     * <p>Title: getLastNMonthBeginDate</p>  
     * <p>Description: 获取n个月后的第一天日期</p>  
     * @param n
     * @param formatter
     * @return
     */
    public static String getLastNMonthBeginDate(int n, DateTimeFormatter formatter) {
        return LocalDate.now().plusMonths(n).with(TemporalAdjusters.firstDayOfMonth()).format(formatter);
    }

    /**
     * <p>Title: getLastNMonthBeginDate</p>
     * <p>Description: 获取n个月后的第一天日期</p>
     * @param format
     * @param dateStr
     * @param n
     * @return
     */
    public static String getLastNMonthBeginDate(String format, String dateStr, int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter).plusMonths(n).with(TemporalAdjusters.firstDayOfMonth()).format(formatter);
    }
    /**
     * 
     * <p>Title: getLastNMonthBeginDate</p>  
     * <p>Description: 获取n个月后的第一天日期</p>  
     * @param n
     * @return
     */
    public static String getLastNMonthBeginDate(int n) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return getLastNMonthBeginDate(n, formatter);
    }
    /**
     * 
     * <p>Title: getLastNMonthEndDate</p>  
     * <p>Description: 获取n个月后的最后一天时间</p>  
     * @param n
     * @return
     */
    public static String getLastNMonthEndDate(int n) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    	return getLastNMonthEndDate(n, formatter);
    }
    /**
     * 
     * <p>Title: getLastNMonthEndDate</p>  
     * <p>Description: 获取n个月后的最后一天时间</p>  
     * @param n
     * @param formatter
     * @return
     */
    public static String getLastNMonthEndDate(int n, DateTimeFormatter formatter) {
        return LocalDate.now().plusMonths(n).with(TemporalAdjusters.lastDayOfMonth()).format(formatter);
    }

    /**
     * <p>Title: getLastNMonthEndDate</p>
     * <p>Description: 获取n个月后的最后一天时间</p>
     * @param format
     * @param dateStr
     * @param n
     * @return
     */
    public static String getLastNMonthEndDate(String format, String dateStr, int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter).plusMonths(n).with(TemporalAdjusters.lastDayOfMonth()).format(formatter);
    }
    
    /**
     * 
     * <p>Title: getLastNYearBeginDate</p>  
     * <p>Description: 获取n年后的第一天</p>  
     * @param n
     * @return
     */
    public static String getLastNYearBeginDate(int n) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    	return getLastNYearBeginDate(n, formatter);
    }
    /**
     * 
     * <p>Title: getLastNYearBeginDate</p>  
     * <p>Description: 获取n年后的第一天</p>  
     * @param n
     * @param formatter
     * @return
     */
    public static String getLastNYearBeginDate(int n, DateTimeFormatter formatter) {
        return LocalDate.now().plusYears(n).with(TemporalAdjusters.firstDayOfYear()).format(formatter);
    }

    /**
     * <p>Title: getLastNYearBeginDate</p>
     * <p>Description: 获取n年后的第一天</p>
     * @param format
     * @param dateStr
     * @param n
     * @return
     */
    public static String getLastNYearBeginDate(String format, String dateStr, int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter).plusYears(n).with(TemporalAdjusters.firstDayOfYear()).format(formatter);
    }
    /**
     * 
     * <p>Title: getLastNYearEndDate</p>  
     * <p>Description: 获取n年后的最后一天</p>  
     * @param n
     * @return
     */
    public static String getLastNYearEndDate(int n) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD);
        return getLastNYearEndDate(n, formatter);
    }
    /**
     * 
     * <p>Title: getLastNYearEndDate</p>  
     * <p>Description: 获取n年后的最后一天</p>  
     * @param n
     * @param formatter
     * @return
     */
    public static String getLastNYearEndDate(int n, DateTimeFormatter formatter) {
        return LocalDate.now().plusYears(n).with(TemporalAdjusters.lastDayOfYear()).format(formatter);
    }

    /**
     * <p>Title: getLastNYearEndDate</p>
     * <p>Description: 获取n年后的最后一天</p>
     * @param format
     * @param dateStr
     * @param n
     * @return
     */
    public static String getLastNYearEndDate(String format, String dateStr, int n) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter).plusYears(n).with(TemporalAdjusters.lastDayOfYear()).format(formatter);
    }
    /**
     * 获取当年最后一天日期
     * @return
     */
    public static LocalDate getLastDateOfCurrentYear() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
    }
    /**
     * 
     * <p>Title: getDaysBetween</p>  
     * <p>Description: 两个日期的天数差</p>  
     * @param from
     * @param to
     * @return
     */
    public static int getDaysBetween(LocalDate from, LocalDate to) {
    	ZoneId zone = ZoneId.systemDefault();
        return getOffDays(from.atStartOfDay().atZone(zone).toInstant().getEpochSecond(),
        		to.atStartOfDay().atZone(zone).toInstant().getEpochSecond());
    }
    
    /**
     * 计算当年剩余的天数
     * @return
     */
    public static int getRemainingDays() {
        return getDaysBetween(LocalDate.now(), getLastDateOfCurrentYear());
    }
    
    /**
     * 
     * <p>Title: compareDate</p>  
     * <p>Description: 用于日期比较(from>to==1,from=to==0,from<to==-1)</p>  
     * @param from 
     * @param to
     * @param format
     * @return
     */
    public static int compareDate(LocalDate from, LocalDate to, String format){
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        from  = getString2Date(format, from.format(formatter));
        to  = getString2Date(format, to.format(formatter));
        
        if(from.isAfter(to)){
            return 1;
        }else if(from.isBefore(to)){
            return -1;
        }else{
            return 0;
        }
    }

    /**
     * <p>Title: compareDateStr</p>
     * <p>Description: 用于日期比较(from>to==1,from=to==0,from<to==-1)</p>
     * @param fromStr
     * @param toStr
     * @param format
     * @return
     */
    public static int compareDateStr(String fromStr, String toStr, String format){
        LocalDate from = getString2Date(format, fromStr);
        LocalDate to = getString2Date(format, toStr);
        if(from.isAfter(to)){
            return 1;
        }else if(from.isBefore(to)){
            return -1;
        }else{
            return 0;
        }
    }


    /**
     * 根据日期获取每年第几周
     * @param dateStr
     * @return
     */
    public static int getWeekByDate(String dateStr){
        String[] date = dateStr.split("-");
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        return LocalDateTime.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), 0, 0, 0).get(weekFields.weekOfYear());
    }

    /**
     * 根据year 和 week 获取第一天数据
     * @param year
     * @param week
     * @return
     */
    public static String getDateByYearWeek(int year, int week, String format){
        WeekFields weekFields=WeekFields.of(Locale.FRANCE);
        LocalDate monday = LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfYear(),week)
                .with(weekFields.dayOfWeek(),1L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(monday);
    }

    /**
     * LocalDate转Date
     * @param localDate
     * @return
     */
    public static Date localDate2Date(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Date转LocalDate
     * @param date
     */
    public static LocalDate date2LocalDate(Date date) {
        if(null == date) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    /**
     * 获取本周第一天
     */
    public static LocalDate getCurrentWeekFirstDate() {
        return LocalDate.now().minusWeeks(0).with(DayOfWeek.MONDAY);
    }

    /**
     * 获取本周最后一天
     */
    public static LocalDate getCurrentWeekLastDate() {
        return LocalDate.now().minusWeeks(0).with(DayOfWeek.SUNDAY);
    }

    /**
     * 获取本月第一天
     */
    public static LocalDate getCurrentMonthFirstDate() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月最后一天
     */
    public static LocalDate getCurrentMonthLastDate() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取指定周第一天
     *
     * @param date    日期字符串
     * @param pattern 格式化
     */
    public static LocalDate getWeekFirstDate(String date, String pattern) {
        return parseLocalDate(date, pattern).minusWeeks(0).with(DayOfWeek.MONDAY);
    }

    /**
     * 获取指定周第一天
     *
     * @param localDate 日期
     */
    public static LocalDate getWeekFirstDate(LocalDate localDate) {
        return localDate.minusWeeks(0).with(DayOfWeek.MONDAY);
    }

    /**
     * 获取指定周最后一天
     *
     * @param date    日期字符串
     * @param pattern 格式化
     */
    public static LocalDate getWeekLastDate(String date, String pattern) {
        return parseLocalDate(date, pattern).minusWeeks(0).with(DayOfWeek.SUNDAY);
    }

    /**
     * 获取指定周最后一天
     *
     * @param localDate 日期
     */
    public static LocalDate getWeekLastDate(LocalDate localDate) {
        return localDate.minusWeeks(0).with(DayOfWeek.SUNDAY);
    }


    /**
     * 获取指定月份月第一天
     *
     * @param date    日期字符串
     * @param pattern 格式化
     */
    public static LocalDate getMonthFirstDate(String date, String pattern) {
        return parseLocalDate(date, pattern).with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取指定月份月第一天
     *
     * @param localDate
     */
    public static LocalDate getMonthFirstDate(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取当前星期
     *
     * @return 1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日
     */
    public static int getCurrentWeek() {
        return LocalDate.now().getDayOfWeek().getValue();
    }


    /**
     * 获取当前星期
     *
     * @param localDate 日期
     */
    public static int getWeek(LocalDate localDate) {
        return localDate.getDayOfWeek().getValue();
    }

    /**
     * 获取指定日期的星期
     *
     * @param date    日期字符串
     * @param pattern 格式化
     */
    public static int getWeek(String date, String pattern) {
        return parseLocalDate(date, pattern).getDayOfWeek().getValue();
    }

    /**
     * 日期相隔天数
     *
     * @param startLocalDate 起日期
     * @param endLocalDate   止日期
     */
    public static long intervalDays(LocalDate startLocalDate, LocalDate endLocalDate) {
        return endLocalDate.toEpochDay() - startLocalDate.toEpochDay();
    }

    /**
     * 日期相隔小时
     *
     * @param startLocalDateTime 起日期时间
     * @param endLocalDateTime   止日期时间
     */
    public static long intervalHours(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return Duration.between(startLocalDateTime, endLocalDateTime).toHours();
    }

    /**
     * 日期相隔分钟
     *
     * @param startLocalDateTime 起日期时间
     * @param endLocalDateTime   止日期时间
     */
    public static long intervalMinutes(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return Duration.between(startLocalDateTime, endLocalDateTime).toMinutes();
    }

    /**
     * 日期相隔毫秒数
     *
     * @param startLocalDateTime 起日期时间
     * @param endLocalDateTime   止日期时间
     */
    public static long intervalMillis(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return Duration.between(startLocalDateTime, endLocalDateTime).toMillis();
    }

    /**
     * 当前是否闰年
     */
    public static boolean isCurrentLeapYear() {
        return LocalDate.now().isLeapYear();
    }

    /**
     * 是否闰年
     */
    public static boolean isLeapYear(LocalDate localDate) {
        return localDate.isLeapYear();
    }

    /**
     * 是否当天
     *
     * @param localDate 日期
     */
    public static boolean isToday(LocalDate localDate) {
        return LocalDate.now().equals(localDate);
    }

    /**
     * 获取此日期时间与默认时区<Asia/Shanghai>组合的时间毫秒数
     *
     * @param localDateTime 日期时间
     */
    public static Long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取此日期时间与指定时区组合的时间毫秒数
     *
     * @param localDateTime 日期时间
     */
    public static Long toSelectEpochMilli(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 计算距今天指定天数的日期
     *
     * @param days
     * @return
     */
    public static String getDateAfterDays(int days) {
        Calendar date = Calendar.getInstance();// today
        date.add(Calendar.DATE, days);
        SimpleDateFormat simpleDate = new SimpleDateFormat(YYYY_MM_DD);
        return simpleDate.format(date.getTime());
    }

    /**
     * 在指定的日期的前几天或后几天
     *
     * @param source 源日期(yyyy-MM-dd)
     * @param days   指定的天数,正负皆可
     * @return
     * @throws ParseException
     */
    public static String addDays(String source, int days) {
        Date date = localDateToDate(parseLocalDate(source, YYYY_MM_DD));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 日期字符串转日期
     *
     * @param date    日期字符串
     * @param pattern 格式化
     */
    public static LocalDate parseLocalDate(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }
    /**
     * LocalDate转换成Date
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 24小时时间校验
     *
     * @param time
     * @return
     */
    public static boolean isValidate24(String time) {
        Pattern p = Pattern.compile(MATCH_TIME_24);
        return p.matcher(time).matches();
    }

    /**
     * 日期校验
     *
     * @param date
     * @return
     */
    public static boolean isDate(String date) {
        Pattern pat = Pattern.compile(REGEX_DATA);
        Matcher mat = pat.matcher(date);
        return mat.matches();
    }



    public static void main(String[] args) {
//    	System.out.println(getRemainingDays());
//    	System.out.println(LocalDateTime.now().minusWeeks(-1));
//        System.out.println(getLastNWeekEndDate(YYYY_MM_DD, "2020-05-11",0));
       System.out.println(getDateByYearWeek(2021, 1, YYYY_MM_DD));
       System.out.println(getWeekByDate("2021-01-04"));
    }


}
