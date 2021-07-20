/**  
* <p>Title: ValidationUtils.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月19日  
* @version 1.0  
*/  
package cn.flood.lang;

import java.util.regex.Pattern;

/**  
* <p>Title: ValidationUtils</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月19日  
*/
public class ValidationUtils {
	
	 /**
     * Don't let anyone instantiate this class
     */
    private ValidationUtils() {
        throw new AssertionError("Cannot create instance");
    }

	
	 /**
     * 手机号简单校验正则
     */
    public static final Pattern PATTERN_MOBILE = Pattern.compile("1\\d{10}");
    /**
     * 手机号较为严格校验正则
     */
    public static final Pattern PATTERN_STRICT_MOBILE = Pattern.compile("1[345678]\\d{9}");
    /**
     * IPv4的正则
     */
    public static final Pattern PATTERN_IPV4_REGEX = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");

    /**
     * IPv6的正则
     */
    public static final Pattern PATTERN_IPV6_REGEX =
            Pattern.compile("^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^::([\\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){2}:([\\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){4}:((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$|^:((:[\\da-fA-F]{1,4}){1,6}|:)$|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)$|^([\\da-fA-F]{1,4}:){2}((:[\\da-fA-F]{1,4}){1,4}|:)$|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)$|^([\\da-fA-F]{1,4}:){4}((:[\\da-fA-F]{1,4}){1,2}|:)$|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?$|^([\\da-fA-F]{1,4}:){6}:$");


    /**
     * 判断是否是手机号(中国)
     *
     * @param mobile 手机号
     * @return 验证成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isMobile(String mobile) {
        return isMatchRegex(PATTERN_MOBILE, mobile);
    }

    /**
     * 判断是否是手机号(中国),使用较为严格的规则,但正则具有时效性,如果新增了号段则校验不通过.
     *
     * @param mobile 手机号
     * @return 验证成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isStrictMobile(String mobile) {
        return isMatchRegex(PATTERN_STRICT_MOBILE, mobile);
    }

    /**
     * 判断是否是IPv4
     *
     * @param ip ip
     * @return 验证成功则返回{@code true},否则返回{@code false}
     */
    public static boolean isIPv4(String ip) {
        return isMatchRegex(PATTERN_IPV4_REGEX, ip);
    }

    /**
     * 判断是否是IPv6
     *
     * @param ip IP地址
     * @return 如果是IPv6则返回{@code true},否则返回{@code false}
     */
    public static boolean isIPv6(String ip) {
        return isMatchRegex(PATTERN_IPV6_REGEX, ip);
    }

    /**
     * 校验
     *
     * @param pattern 正则
     * @param value   待验证的字符串
     * @return 验证成功则返回{@code true},否则返回{@code false}
     */
    private static boolean isMatchRegex(Pattern pattern, String value) {
        //空字符串则直接返回{@code false}
        if (pattern == null || StringUtils.isEmpty(value)) {
            return false;
        }
        //正则表达式为{@code null}则全匹配
        return pattern.matcher(value).matches();
    }


}
