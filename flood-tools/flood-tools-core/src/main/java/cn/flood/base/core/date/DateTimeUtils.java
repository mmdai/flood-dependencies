package cn.flood.base.core.date;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import org.springframework.util.ObjectUtils;

public class DateTimeUtils {

  public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

  public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

  public static final String YYYYMMDDHHMM = "yyyyMMddHHmm";

  public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

  public static final String YYYYMMDD_HH = "yyyyMMdd_HH";

  public static final String YYYY_MM_DD_00_00_00 = "yyyy-MM-dd 00:00:00";

  public static String today;

  public static String yesterday;

  /**
   * Don't let anyone instantiate this class
   */
  private DateTimeUtils() {
    throw new AssertionError("Cannot create instance");
  }

  /**
   * <p>Title: getCurrentDate</p>
   * <p>Description: 获取当前时间</p>
   *
   * @return
   */
  public static LocalDateTime getCurrentDate() {
    return LocalDateTime.now();
  }

  /**
   * <p>Title: getString2Date</p>
   * <p>Description: </p>
   *
   * @param format
   * @param str
   * @return
   */
  public static LocalDateTime getString2Date(String format, String str) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(str, formatter);
  }

  /**
   * <p>Title: getYesterdayDate</p>
   * <p>Description: 获取昨天时间</p>
   *
   * @return
   */
  public static LocalDateTime getYesterdayDate() {
    return LocalDateTime.now().plusDays(-1);
  }

  /**
   * 计算从某个日期开始之后n天的日期
   *
   * @param from 开始日期
   * @param n
   * @return
   */
  public static LocalDateTime getNDayFromDate(LocalDateTime from, int n) {
    return from.plusDays(n);
  }

  /**
   * 计算从某个日期开始之后n分钟的日期
   *
   * @param from 开始日期
   * @param n
   * @return
   */
  public static LocalDateTime getNMinuteFromDate(LocalDateTime from, int n) {
    return from.plusMinutes(n);
  }


  /**
   * 计算从某个日期开始之后n月的日期
   *
   * @param from 开始日期
   * @param n
   * @return
   */
  public static LocalDateTime getNMonthDate(LocalDateTime from, int n) {
    return from.plusMonths(n);

  }

  /**
   * long to timestamp
   *
   * @param timestamp
   * @return
   */
  public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
    Instant instant = Instant.ofEpochMilli(timestamp);
    ZoneId zone = ZoneId.systemDefault();
    return LocalDateTime.ofInstant(instant, zone);
  }

  /**
   * 计算从某个日期开始之后n周的日期
   *
   * @param from 开始日期
   * @param n
   * @return
   */
  public static LocalDateTime getNWeekDate(LocalDateTime from, int n) {
    return from.plusWeeks(n);
  }

  /**
   * <p>Title: getCurrentTimeMillis</p>
   * <p>Description: 获取当前时间戳</p>
   *
   * @return
   */
  public static long getCurrentTimeMillis() {
    return Clock.systemDefaultZone().millis();
  }

  /**
   * <p>Title: getCurrentFormatDate</p>
   * <p>Description: 获取当前时间格式化日期</p>
   *
   * @param formatDate
   * @return
   */
  public static String getCurrentFormatDate(String formatDate) {
    LocalDateTime date = getCurrentDate();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatDate);
    return date.format(formatter);
  }

  /**
   * <p>Title: getCurrentFormatDate</p>
   * <p>Description: 获取当前格式：yyyy-MM-dd HH:mm:ss 时间</p>
   *
   * @return
   */
  public static String getCurrentFormatDate() {
    LocalDateTime date = getCurrentDate();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    return date.format(formatter);
  }

  /**
   * <p>Title: resetToday</p>
   * <p>Description: 重置日期</p>
   */
  public static void resetToday() {
    String todayStr = getDate2String(YYYY_MM_DD_HH_MM_SS, getCurrentDate());
    if ((today == null) || !today.equals(todayStr)) {
      today = todayStr;
      yesterday = getDate2String(YYYY_MM_DD_HH_MM_SS, getYesterdayDate());
    }
  }

  /**
   * 时间字符串转时间
   *
   * @param time    时间字符串
   * @param pattern 格式化
   */
  public static LocalTime parseLocalTime(String time, String pattern) {
    return LocalTime.parse(time, DateTimeFormatter.ofPattern(pattern));
  }

  /**
   * 判断当前时间是否在指定时间范围
   *
   * @param from 开始时间
   * @param to   结束时间
   * @return 结果
   */
  public static boolean between(LocalTime from, LocalTime to) {
    LocalTime now = LocalTime.now();
    return now.isAfter(from) && now.isBefore(to);
  }

  /**
   * <p>Title: getDate2String</p>
   * <p>Description: 格式化给定日期</p>
   *
   * @param format
   * @param date
   * @return
   */
  public static final String getDate2String(String format, LocalDateTime date) {
    if (date != null) {
      if (ObjectUtils.isEmpty(format)) {
        format = YYYY_MM_DD_HH_MM_SS;
      }
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
      return date.format(formatter);
    } else {
      return "";
    }
  }

  /**
   * 尝试解析，如果出错就返回null
   *
   * @param format
   * @param dateStr
   * @return 正常解析的话，就返回一个date，否则返回null
   */
  public static LocalDateTime tryParseString2DateTime(String format, String dateStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(dateStr, formatter);
  }


  /**
   * <p>Title: getLong2Date</p>
   * <p>Description: 毫秒时间戳转换成时间</p>
   *
   * @param l
   * @return
   */
  public static LocalDateTime getLong2Date(long l) {
    Instant instant = Instant.ofEpochMilli(l);
    ZoneId zone = ZoneId.systemDefault();
    return LocalDateTime.ofInstant(instant, zone);
  }

  /**
   * <p>Title: getDate2Long</p>
   * <p>Description: 时间转换成毫秒</p>
   *
   * @param date
   * @return
   */
  public static long getDate2Long(LocalDateTime date) {
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = date.atZone(zone).toInstant();
    return instant.toEpochMilli();
  }

  /**
   * <p>Title: getOffDays</p>
   * <p>Description: 从某个时间戳到现在时间戳转换成天数</p>
   *
   * @param l
   * @return
   */
  public static int getOffDays(long l) {
    return getOffDays(l, getCurrentTimeMillis());
  }

  /**
   * <p>Title: getOffDays</p>
   * <p>Description: 从某个时间戳到某个时间戳转换成天数</p>
   *
   * @param from
   * @param to
   * @return
   */
  public static int getOffDays(long from, long to) {
    return getOffMinutes(from, to) / 1440;
  }

  /**
   * <p>Title: getOffDays</p>
   * <p>Description: 从某个时间戳到现在时间戳转换成分钟</p>
   *
   * @param l
   * @return
   */
  public static int getOffMinutes(long l) {
    return getOffMinutes(l, getCurrentTimeMillis());
  }

  /**
   * <p>Title: getOffMinutes</p>
   * <p>Description: 从某个时间戳到某个时间戳转换成分钟</p>
   *
   * @param from
   * @param to
   * @return
   */
  public static int getOffMinutes(long from, long to) {
    return (int) ((to - from) / 60L);
  }

  /**
   * 获取n天后的日期
   *
   * @param n
   * @param format
   * @return
   */
  public static String getLastNDayDate(int n, String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.now().plusDays(n).format(formatter);
  }

  /**
   * <p>Title: getLastNWeekBeginDate</p>
   * <p>Description: 获取n个周后的第一天日期</p>
   *
   * @param n
   * @param formatter
   * @return
   */
  public static String getLastNWeekBeginDate(int n, DateTimeFormatter formatter) {
    return LocalDateTime.now().plusWeeks(n).with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1)
        .format(formatter);
  }

  /**
   * <p>Title: getLastNWeekBeginDate</p>
   * <p>Description: 获取n个周后的第一天日期</p>
   *
   * @param format
   * @param dateStr
   * @param n
   * @return
   */
  public static String getLastNWeekBeginDate(String format, String dateStr, int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(dateStr, formatter).plusWeeks(n)
        .with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1).format(formatter);
  }

  /**
   * <p>Title: getLastNWeekBeginDate</p>
   * <p>Description: 获取n个周后的第一天日期</p>
   *
   * @param n
   * @return
   */
  public static String getLastNWeekBeginDate(int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    return getLastNWeekBeginDate(n, formatter);
  }

  /**
   * <p>Title: getLastNWeekEndDate</p>
   * <p>Description: 获取n个周后的最一天日期</p>
   *
   * @param n
   * @param formatter
   * @return
   */
  public static String getLastNWeekEndDate(int n, DateTimeFormatter formatter) {
    return LocalDateTime.now().plusWeeks(n).with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 7)
        .format(formatter);
  }

  /**
   * <p>Title: getLastNWeekEndDate</p>
   * <p>Description: 获取n个周后的最一天日期</p>
   *
   * @param format
   * @param dateStr
   * @param n
   * @return
   */
  public static String getLastNWeekEndDate(String format, String dateStr, int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(dateStr, formatter).plusWeeks(n)
        .with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 7).format(formatter);
  }

  /**
   * <p>Title: getLastNWeekEndDate</p>
   * <p>Description: 获取n个周后的最一天日期</p>
   *
   * @param n
   * @return
   */
  public static String getLastNWeekEndDate(int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    return getLastNWeekEndDate(n, formatter);
  }

  /**
   * <p>Title: getLastNMonthBeginDate</p>
   * <p>Description: 获取n个月后的第一天日期</p>
   *
   * @param n
   * @param formatter
   * @return
   */
  public static String getLastNMonthBeginDate(int n, DateTimeFormatter formatter) {
    return LocalDateTime.now().plusMonths(n).with(TemporalAdjusters.firstDayOfMonth())
        .format(formatter);
  }

  /**
   * <p>Title: getLastNMonthBeginDate</p>
   * <p>Description: 获取n个月后的第一天日期</p>
   *
   * @param format
   * @param dateStr
   * @param n
   * @return
   */
  public static String getLastNMonthBeginDate(String format, String dateStr, int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(dateStr, formatter).plusMonths(n)
        .with(TemporalAdjusters.firstDayOfMonth()).format(formatter);
  }

  /**
   * <p>Title: getLastNMonthBeginDate</p>
   * <p>Description: 获取n个月后的第一天日期</p>
   *
   * @param n
   * @return
   */
  public static String getLastNMonthBeginDate(int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    return getLastNMonthBeginDate(n, formatter);
  }

  /**
   * <p>Title: getLastNMonthEndDate</p>
   * <p>Description: 获取n个月后的最后一天时间</p>
   *
   * @param n
   * @return
   */
  public static String getLastNMonthEndDate(int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    return getLastNMonthEndDate(n, formatter);
  }

  /**
   * <p>Title: getLastNMonthEndDate</p>
   * <p>Description: 获取n个月后的最后一天时间</p>
   *
   * @param n
   * @param formatter
   * @return
   */
  public static String getLastNMonthEndDate(int n, DateTimeFormatter formatter) {
    return LocalDateTime.now().plusMonths(n).with(TemporalAdjusters.lastDayOfMonth())
        .format(formatter);
  }

  /**
   * <p>Title: getLastNMonthEndDate</p>
   * <p>Description: 获取n个月后的最后一天时间</p>
   *
   * @param format
   * @param dateStr
   * @param n
   * @return
   */
  public static String getLastNMonthEndDate(String format, String dateStr, int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(dateStr, formatter).plusMonths(n)
        .with(TemporalAdjusters.lastDayOfMonth()).format(formatter);
  }

  /**
   * <p>Title: getLastNYearBeginDate</p>
   * <p>Description: 获取n年后的第一天</p>
   *
   * @param n
   * @return
   */
  public static String getLastNYearBeginDate(int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    return getLastNYearBeginDate(n, formatter);
  }

  /**
   * <p>Title: getLastNYearBeginDate</p>
   * <p>Description: 获取n年后的第一天</p>
   *
   * @param n
   * @param formatter
   * @return
   */
  public static String getLastNYearBeginDate(int n, DateTimeFormatter formatter) {
    return LocalDateTime.now().plusYears(n).with(TemporalAdjusters.firstDayOfYear())
        .format(formatter);
  }

  /**
   * <p>Title: getLastNYearBeginDate</p>
   * <p>Description: 获取n年后的第一天</p>
   *
   * @param format
   * @param dateStr
   * @param n
   * @return
   */
  public static String getLastNYearBeginDate(String format, String dateStr, int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(dateStr, formatter).plusYears(n).with(TemporalAdjusters.firstDayOfYear())
        .format(formatter);
  }

  /**
   * <p>Title: getLastNYearEndDate</p>
   * <p>Description: 获取n年后的最后一天</p>
   *
   * @param n
   * @return
   */
  public static String getLastNYearEndDate(int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    return getLastNYearEndDate(n, formatter);
  }

  /**
   * <p>Title: getLastNYearEndDate</p>
   * <p>Description: 获取n年后的最后一天</p>
   *
   * @param n
   * @param formatter
   * @return
   */
  public static String getLastNYearEndDate(int n, DateTimeFormatter formatter) {
    return LocalDateTime.now().plusYears(n).with(TemporalAdjusters.lastDayOfYear())
        .format(formatter);
  }

  /**
   * <p>Title: getLastNYearEndDate</p>
   * <p>Description: 获取n年后的最后一天</p>
   *
   * @param format
   * @param dateStr
   * @param n
   * @return
   */
  public static String getLastNYearEndDate(String format, String dateStr, int n) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDate.parse(dateStr, formatter).plusYears(n).with(TemporalAdjusters.lastDayOfYear())
        .format(formatter);
  }

  /**
   * 获取当年最后一天日期
   *
   * @return
   */
  public static LocalDateTime getLastDateOfCurrentYear() {
    return LocalDateTime.now().with(TemporalAdjusters.lastDayOfYear());
  }

  /**
   * <p>Title: getDaysBetween</p>
   * <p>Description: 两个日期的天数差</p>
   *
   * @param from
   * @param to
   * @return
   */
  public static int getDaysBetween(LocalDateTime from, LocalDateTime to) {
    ZoneId zone = ZoneId.systemDefault();
    return getOffDays(from.atZone(zone).toInstant().getEpochSecond(),
        to.atZone(zone).toInstant().getEpochSecond());
  }

  /**
   * 计算当年剩余的天数
   *
   * @return
   */
  public static int getRemainingDays() {
    return getDaysBetween(LocalDateTime.now(), getLastDateOfCurrentYear());
  }

  /**
   * <p>Title: compareDate</p>
   * <p>Description: 用于日期比较(from>to==1,from=to==0,from<to==-1)</p>
   *
   * @param from
   * @param to
   * @param format
   * @return
   */
  public static int compareDate(LocalDateTime from, LocalDateTime to, String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    from = getString2Date(format, from.format(formatter));
    to = getString2Date(format, to.format(formatter));

    if (from.isAfter(to)) {
      return 1;
    } else if (from.isBefore(to)) {
      return -1;
    } else {
      return 0;
    }
  }

  /**
   * <p>Title: compareDateStr</p>
   * <p>Description: 用于日期比较(from>to==1,from=to==0,from<to==-1)</p>
   *
   * @param fromStr
   * @param toStr
   * @param format
   * @return
   */
  public static int compareDateStr(String fromStr, String toStr, String format) {
    LocalDateTime from = getString2Date(format, fromStr);
    LocalDateTime to = getString2Date(format, toStr);
    if (from.isAfter(to)) {
      return 1;
    } else if (from.isBefore(to)) {
      return -1;
    } else {
      return 0;
    }
  }


  /**
   * 根据日期获取每年第几周
   *
   * @param dateStr
   * @return
   */
  public static int getWeekByDate(String dateStr) {
    String[] date = dateStr.split("-");
    WeekFields weekFields = WeekFields.of(Locale.FRANCE);
    return LocalDateTime
        .of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), 0, 0,
            0).get(weekFields.weekOfYear());
  }

  /**
   * 根据year 和 week 获取第一天数据
   *
   * @param year
   * @param week
   * @return
   */
  public static String getDateByYearWeek(int year, int week, String format) {
    WeekFields weekFields = WeekFields.of(Locale.FRANCE);
    LocalDateTime monday = LocalDateTime.now()
        .withYear(year)
        .with(weekFields.weekOfYear(), week)
        .with(weekFields.dayOfWeek(), 1L);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return formatter.format(monday);
  }

  /**
   * LocalDateTime
   *
   * @param localDateTime
   * @return
   */
  public static Date localDateTime2Date(LocalDateTime localDateTime) {
    if (null == localDateTime) {
      return null;
    }
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
    return Date.from(zonedDateTime.toInstant());
  }

  /**
   * Date转LocalDateTime
   *
   * @param date
   */
  public static LocalDateTime date2LocalDate(Date date) {
    if (null == date) {
      return null;
    }
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static void main(String[] args) {
//    	System.out.println(getRemainingDays());
//    	System.out.println(LocalDateTime.now().minusWeeks(-1));
//        System.out.println(getLastNWeekEndDate(YYYY_MM_DD, "2020-05-11",0));
    System.out.println(getDateByYearWeek(2021, 1, YYYY_MM_DD_HH_MM_SS));
    System.out.println(getWeekByDate("2021-01-04"));
  }
}
