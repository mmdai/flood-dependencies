package cn.flood.base.core.regular;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证⼯具类 常量
 *
 * @author mmdai
 * @version 1.0
 * @date 2022/5/30 9:32
 */
public class Validation {

  /**
   * Email正则表达式="^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
   */
  //public static final String EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
  public static final String EMAIL = "(\\w+(\\.\\w+)*@\\w+(\\.\\w+)+)";
  /**
   * 电话号码正则表达式= (^(\d{2,4}[-_－—]?)?\d{3,8}([-_－—]?\d{3,8})?([-_－—]?\d{1,7})?$)|(^0?1[35]\d{9}$)
   */
  public static final String PHONE = "(^(\\d{2,4}[-_－—]?)?\\d{3,8}([-_－—]?\\d{3,8})?([-_－—]?\\d{1,7})?$)|(^0?1[35]\\d{9}$)";
  /**
   * ⼿机号码正则表达式=^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9])\d{8}$
   */
  public static final String MOBILE = "(^1[1|2|3|4|5|6|7|8|9][0-9]{9}$)";
  /**
   * Integer正则表达式 ^-?(([1-9]\d*$)|0)
   */
  public static final String INTEGER = "(^-?(([1-9]\\d*$)|0))";
  /**
   * 正整数正则表达式 >=0 ^[1-9]\d*|0$
   */
  public static final String INTEGER_NEGATIVE = "(^[1-9]\\d*|0$)";
  /**
   * 负整数正则表达式 <=0 ^-[1-9]\d*|0$
   */
  public static final String INTEGER_POSITIVE = "(^-[1-9]\\d*|0$)";
  /**
   * Double正则表达式 ^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$
   */
  public static final String DOUBLE = "(^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$)";
  /**
   * 正Double正则表达式 >=0 ^[1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0$
   */
  public static final String DOUBLE_NEGATIVE = "(^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$)";
  /**
   * 负Double正则表达式 <= 0 ^(-([1-9]\d*\.\d*|0\.\d*[1-9]\d*))|0?\.0+|0$
   */
  public static final String DOUBLE_POSITIVE = "(^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$)";
  /**
   * 年龄正则表达式 ^(?:[1-9][0-9]?|1[01][0-9]|120)$ 匹配0-120岁
   */
  public static final String AGE = "(^(?:[1-9][0-9]?|1[01][0-9]|120)$)";
  /**
   * 邮编正则表达式 [0-9]\d{5}(?!\d) 国内6位邮编
   */
  public static final String CODE = "([0-9]\\d{5}(?!\\d))";
  /**
   * 匹配由数字、26个英⽂字母或者下划线组成的字符串 ^\w+$
   */
  public static final String STR_ENG_NUM_ = "(^\\w+$)";
  /**
   * 匹配由数字和26个英⽂字母组成的字符串 ^[A-Za-z0-9]+$
   */
  public static final String STR_ENG_NUM = "(^[A-Za-z0-9]+)";
  /**
   * 匹配由数字和26个英⽂字母组成的字符串 ^[A-Za-z0-9]{2,36}$
   */
  public static final String STR_ENG_NUM_36 = "(^[A-Za-z0-9]{1,36}$)";
  /**
   * 匹配由26个英⽂字母组成的字符串 ^[A-Za-z]+$
   */
  public static final String STR_ENG = "(^[A-Za-z]+$)";
  /**
   * 匹配由数字和26个英⽂字母组成的字符串 ^[A-Za-z0-9]{2,36}$
   */
  public static final String STR_ENG_36 = "(^[A-Za-z]{1,36}$)";
  /**
   * ⽇期正则 ⽀持： YYYY-MM-DD YYYY/MM/DD YYYY_MM_DD YYYYMMDD YYYY.MM.DD的形式
   */
  public static final String DATE_ALL =
      "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(10|12|0?[13578])([-\\/\\._]?)(3[01]|[12][0-9]|0?[1-9])$)"
          + "|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(11|0?[469])([-\\/\\._]?)(30|[12][0-9]|0?[1-9])$)"
          + "|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._]?)(0?2)([-\\/\\._]?)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([3579][26]00)"
          + "([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)"
          + "|(^([1][89][0][48])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([2-9][0-9][0][48])([-\\/\\._]?)"
          + "(0?2)([-\\/\\._]?)(29)$)"
          + "|(^([1][89][2468][048])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._]?)(0?2)"
          + "([-\\/\\._]?)(29)$)|(^([1][89][13579][26])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$)|"
          + "(^([2-9][0-9][13579][26])([-\\/\\._]?)(0?2)([-\\/\\._]?)(29)$))";
  /**
   * ⽇期正则 ⽀持YYYY-MM-DD
   */
  public static final String DATE_FORMAT1 = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";

  /**
   * URL正则表达式 匹配 http www ftp
   */
  public static final String URL =
      "(^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?"
          + "(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*"
          + "(\\w*:)*(\\w*\\+)*(\\w*\\.)*" + "(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$)";
  /**
   * 机构代码
   */
  public static final String JIGOU_CODE = "(^[A-Z0-9]{8}-[A-Z0-9]$)";
  /**
   * 匹配数字组成的字符串 ^[0-9]+$
   */
  public static final String STR_NUM = "(^[0-9]+$)";
  /**
   * 中国⾝份证
   */
  public static final String CH_IDCARD = "(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
  /**
   * 中国姓名
   */
  public static final String CH_NAME = "(^[\u2E80-\uFE4F·]{2,20}(?<!·)$)";
  /**
   * 银行卡号
   */
  public static final String BANK_CARD = "(^\\d{15,32}$)";
  /**
   * US身份证
   */
  public static final String US_IDCARD = "(^[1-9][0-9]{15}$)";
  /**
   * US姓名
   */
  public static final String US_NAME = "(^[a-zA-Z|.|,|\\s]{5,100}|[\u4e00-\u9fa5|.]{2,20}$)";
  /**
   * 通用
   */
  public static final String COMM_NAME = "( ^[a-zA-Z0-9]{2,64}$)";

    /**
     *
     * 匹配是否符合正则表达式pattern 匹配返回true
     * @param str 匹配的字符串
     * @param pattern 匹配模式
     * @return boolean
     */
    public static boolean Regular(String str,String pattern){
        if(null == str || str.trim().length()<=0)
            return false;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }
//    public static void main(String[] args){
//        System.out.println(Regular("20211211", DATE_FORMAT1));
//    }
}
