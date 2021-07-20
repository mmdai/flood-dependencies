package cn.flood.elasticsearch.enums;

/**
 * program: esdemo
 * description: es字段数据结构
 * author: X-Pacific zhang
 * create: 2019-01-25 16:58
 **/
public enum DataType {
    keyword_type,//keyword类型字段
    text_type,//text文本数据类型
    byte_type,//数字型数据类型
    short_type,//数字型数据类型
    integer_type,//数字型数据类型
    long_type,//数字型数据类型
    float_type,//数字型数据类型
    double_type,//数字型数据类型
    boolean_type,//布尔类型
    date_type,// 日期类型
    nested_type,//嵌套数据类型
    geo_point_type;//地理位置
}
