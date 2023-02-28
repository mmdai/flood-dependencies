package cn.flood.tools.uid.baidu.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @������ DateUtils.java
 * @������ <pre>��ʱ�丨����(SimpleDateFormat���̲߳���ȫ�ģ�������˾�̬������)</pre>
 * @���� ׯ�ε��� linhuaichuan1989@126.com
 * @����ʱ�� 2019��7��8�� ����11:15:29
 * @�汾 1.0.0
 * @�޸ļ�¼ <pre>
 *     �汾                       �޸��� 		�޸����� 		 �޸���������
 *     ----------------------------------------------
 *     1.0.0 	ׯ�ε��� 	2019��7��8��
 *     ----------------------------------------------
 * </pre>
 */
public class DateUtils {

  /**
   * ����-��ʽ
   */
  public static final String DAY_PATTERN = "yyyy-MM-dd";

  /**
   * ����ʱ��-��ʽ
   */
  public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

  /**
   * ����ʱ��(������)-��ʽ
   */
  public static final String DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

  /**
   * ������df����
   */
  private static ThreadLocal<Map<String, DateFormat>> dfMap = new ThreadLocal<Map<String, DateFormat>>() {
    @Override
    protected Map<String, DateFormat> initialValue() {
      return new HashMap<String, DateFormat>(10);
    }
  };

  /**
   * @param pattern ��ʽ ���ʽ
   * @return DateFormat����
   * @�������� getDateFormat
   * @�������� <pre>����һ��DateFormat,ÿ���߳�ֻ��newһ��pattern��Ӧ��sdf</pre>
   */
  private static DateFormat getDateFormat(final String pattern) {
    Map<String, DateFormat> tl = dfMap.get();
    DateFormat df = tl.get(pattern);
    if (df == null) {
      df = new SimpleDateFormat(pattern);
      tl.put(pattern, df);
    }
    return df;
  }

  /**
   * @param date ʱ�����
   * @return ʱ���ַ���
   * @�������� formatByDateTimePattern
   * @�������� <pre>��ȡ'yyyy-MM-dd HH:mm:ss'��ʽ��ʱ���ַ���</pre>
   */
  public static String formatByDateTimePattern(Date date) {
    return getDateFormat(DATETIME_PATTERN).format(date);
  }

  /**
   * @param str ʱ���ַ���
   * @return ʱ�����
   * @�������� parseByDayPattern
   * @�������� <pre>����'yyyy-MM-dd'��ʽ��ʱ��</pre>
   */
  public static Date parseByDayPattern(String str) {
    return parseDate(str, DAY_PATTERN);
  }

  /**
   * @param str     ʱ���ַ���
   * @param pattern ��ʽ���ʽ
   * @return ʱ�����
   * @�������� parseDate
   * @�������� <pre>����ָ����ʽ��ʱ��</pre>
   */
  public static Date parseDate(String str, String pattern) {
    try {
      return getDateFormat(pattern).parse(str);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
