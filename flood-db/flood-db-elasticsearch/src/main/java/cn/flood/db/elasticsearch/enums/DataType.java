package cn.flood.db.elasticsearch.enums;

/**
 * program: esdemo description: es字段数据结构 author: X-Pacific zhang create: 2019-01-25 16:58
 **/
public enum DataType {
  keyword_type, text_type, byte_type, short_type, integer_type, long_type, float_type, double_type, boolean_type, date_type, nested_type, geo_point_type;


  public static DataType getDataTypeByStr(String str) {
    if ("keyword".equals(str)) {
      return keyword_type;
    } else if ("text".equals(str)) {
      return text_type;
    } else if ("byte".equals(str)) {
      return byte_type;
    } else if ("short".equals(str)) {
      return short_type;
    } else if ("integer".equals(str)) {
      return integer_type;
    } else if ("long".equals(str)) {
      return long_type;
    } else if ("float".equals(str)) {
      return float_type;
    } else if ("double".equals(str)) {
      return double_type;
    } else if ("boolean".equals(str)) {
      return boolean_type;
    } else if ("date".equals(str) || "datetime".equals(str)) {
      return date_type;
    } else {
      return text_type;
    }
  }
}
