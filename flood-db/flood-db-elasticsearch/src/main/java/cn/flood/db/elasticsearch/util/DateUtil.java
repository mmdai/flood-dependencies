package cn.flood.db.elasticsearch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateUtil {

  public static final String PATTERN_YM = "yyyy-MM";

  public static final String PATTERN_YMD1 = "yyyyMMdd";

  public static final String PATTERN_YMD2 = "yyyy-MM-dd";

  public static final String PATTERN_YMD3 = "yyyy/MM/dd";

  public static final String PATTERN_YMDHM = "yyyyMMddHHmm";

  public static final String PATTERN_YMDHMS1 = "yyyy-MM-dd HH:mm:ss";

  public static final String PATTERN_YMDHMS2 = "yyyyMMddHHmmss";

  /**
   * 根据时间格式获取对应的时间格式化工具
   */
  private static SimpleDateFormat getSimpleDateFormat(String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    return sdf;
  }


  public static boolean checkDatePattern(String str) {
    SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_YMDHMS1);
    boolean check = true;
    try {
      sdf.parse(str);
    } catch (ParseException e) {
      check = false;
    }
    return check;
  }

  public static boolean checkDatePattern(String str, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    boolean check = true;
    try {
      sdf.parse(str);
    } catch (ParseException e) {
      check = false;
    }
    return check;
  }

  /**
   * 时间转字符串 yyyy-MM-dd
   */
  public static String dateToShortString(Date d) {
    if (d != null) {
      return getSimpleDateFormat(PATTERN_YMD2).format(d);
    } else {
      return "";
    }
  }

  /**
   * 时间转字符串 yyyy-MM-dd HH:mm:ss
   */
  public static String dateToString(Date d) {
    if (d != null) {
      return getSimpleDateFormat(PATTERN_YMDHMS1).format(d);
    } else {
      return "";
    }
  }

  /**
   * 获取当天开始时间
   *
   * @return
   */
  public static Date getDayStartTime(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }

  /**
   * 获取当天的结束时间
   *
   * @return
   */
  public static Date getDayEndTime(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    return cal.getTime();
  }

  /**
   * 获取本周开始时间
   *
   * @return
   */
  public static Date getBeginDayOfWeek() {
    Date date = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    if (dayofweek == 1) {
      dayofweek += 7;
    }
    cal.add(Calendar.DATE, 2 - dayofweek);
    return getDayStartTime(cal.getTime());
  }

  /**
   * 获取本周结束时间
   *
   * @return
   */
  public static Date getEndDayOfWeek() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getBeginDayOfWeek());
    cal.add(Calendar.DAY_OF_WEEK, 6);
    Date weekEndSta = cal.getTime();
    return getDayEndTime(weekEndSta);
  }

  /**
   * 获取上周的开始时间
   *
   * @return
   */
  public static Date getBeginDayOfLastWeek(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
    if (dayofweek == 1) {
      dayofweek += 7;
    }
    cal.add(Calendar.DATE, 2 - dayofweek - 7);
    return getDayStartTime(cal.getTime());
  }

  /**
   * 获取上周的结束时间
   *
   * @return
   */
  public static Date getEndDayOfLastWeek(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(getBeginDayOfLastWeek(date));
    cal.add(Calendar.DAY_OF_WEEK, 6);
    Date weekEndSta = cal.getTime();
    return getDayEndTime(weekEndSta);
  }

  /**
   * 获得昨天零点
   *
   * @return Date
   */
  public static Date getYesterDay() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.HOUR, 0);
    return cal.getTime();
  }

  /**
   * 获得昨天字符串
   *
   * @return Date
   */
  public static String getYesterDayShortString() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.HOUR, 0);
    return dateToShortString(cal.getTime());
  }

  /**
   * 日期按天加减
   *
   * @param date
   * @param hours
   * @return
   */
  public static Date addDayTime(Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, days);
    return cal.getTime();
  }

  /**
   * 日期按小时加减
   *
   * @param date
   * @param hours
   * @return
   */
  public static Date addHourTime(Date date, int hours) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.HOUR, hours);
    return cal.getTime();
  }

  /**
   * 字符串转时间
   */
  public static Date strToDate(String str) {
    Date date = null;
    if (str == null) {
      return date;
    }
    int len = str.length();
    if (len == 20) { //2003-07-16T01:24:32Z
      try {
        str = str.substring(0, 10) + str.substring(11, 19);
        date = getSimpleDateFormat(PATTERN_YMDHMS1).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (str.indexOf('-') > 0 && len == 19) {// "yyyy-MM-dd HH:mm:ss"
      try {
        date = getSimpleDateFormat(PATTERN_YMDHMS1).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (str.indexOf('-') > 0 && len == 7) {
      try {
        date = getSimpleDateFormat(PATTERN_YM).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (str.indexOf('-') > 0 && (8 <= len && len <= 10)) {// "yyyy-MM-dd"
      try {
        date = getSimpleDateFormat(PATTERN_YMD2).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (str.indexOf('/') > 0 && (8 <= len && len <= 10)) {// "yyyy/MM/dd"
      try {
        date = getSimpleDateFormat(PATTERN_YMD3).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (len == 14) {// "yyyyMMddHHmmss"
      try {
        date = getSimpleDateFormat(PATTERN_YMDHMS2).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (len == 8) {// yyyymmdd
      try {
        date = getSimpleDateFormat(PATTERN_YMD1).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else if (len == 12) {// yyyyMMddHHmm
      try {
        date = getSimpleDateFormat(PATTERN_YMDHM).parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return date;
  }
}