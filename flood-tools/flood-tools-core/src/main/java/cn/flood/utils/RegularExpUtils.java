package cn.flood.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class RegularExpUtils {

    //数字、汉字、字母
    public static boolean valid(String params) {
        boolean matches = validOrgCode(params);
        if (matches && !params.contains("@")) {
            return true;
        }
        return false;
    }

    //数字、汉字、字母
    public static boolean validOrgCode(String params) {
        if (StringUtils.isEmpty(params)) {
            return false;
        }
        String regix = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$";
        boolean matches = params.matches(regix);
        return matches;
    }

    //汉字、字母
    public static boolean check(String params) {
        String regix = "^[a-zA-Z\u4e00-\u9fa5]+$";
        boolean matches = params.matches(regix);
        return matches;
    }

    //手机号码
    public static boolean checkTelephone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        //String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        String regex = "^1(3|4|5|6|7|8|9)\\d{9}$";
        boolean matches = Pattern.matches(regex, phone);
        return matches;
    }

    //验证邮箱 存在@，且最大长度为25个字符！
    public static boolean checkEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        boolean contains = email.contains("@");
        return contains;
    }

    /**
     * 判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        String regex = "^[0-9]*$";
        boolean matches = Pattern.matches(regex, str);
        return matches;
    }

    /**
     * 不能包含的字符
     *
     * @param params
     * @return
     */
    public static boolean validName(String params) {
        if (StringUtils.isEmpty(params)) {
            return true;
        }
        String regStrs = "∏¥§℅€℃£℉№℡‰$¢∮※？?<>[]'&";
        for (char ch : regStrs.toCharArray()) {
            if (params.indexOf(String.valueOf(ch)) != -1) { // 等于-1表示这个字符串中没有o这个字符
                return true;
            }
        }
        return false;
    }

    /**
     * 数字、字母
     *
     * @param params
     * @return
     */
    public static boolean validCode(String params) {
        if (StringUtils.isEmpty(params)) {
            return false;
        }
        String regix = "^[a-z0-9A-Z]+$";
        boolean matches = params.matches(regix);
        return matches;
    }

    /**
     * 区号+座机号码+分机号码
     *
     * @param fixedPhone
     * @return
     */
    public static boolean isFixedPhone(String fixedPhone) {
        if (StringUtils.isEmpty(fixedPhone)) {
            return false;
        }
        String reg = "^[0-9-]{5,15}$";
        return Pattern.matches(reg, fixedPhone);
    }

    /**
     * 校验注册规则，
     * 默认包含以下4种字符类型的任意3种组合，
     * 默认密码长度为8位
     * 默认密码中不能包含账号
     * @param password
     * @param username
     * @return
     */
    public static boolean checkPasswordRule(String password, String username) {
        return checkPasswordRule(password, username, 3, 8, true);
    }

    /**
     * 校验注册规则，
     * 1. 包含以下4种字符类型的任意{pwdTypeSize}种组合
     * 2. 密码长度{pwdLength}，最小6位，最大30位
     * 3. 密码中不能包含账号校验{pwdNoAccountCheck}
     *
     * @param password
     * @param username
     * @param pwdTypeSize 密码类型数量
     * @param pwdLength   密码长度
     * @param pwdNoAccountCheck 是否需要密码中不能包含密码校验
     * @return
     */
    public static boolean checkPasswordRule(String password, String username, int pwdTypeSize, int pwdLength,
                                            Boolean pwdNoAccountCheck) {
        //String reg = "^((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])|(?=.*[a-z])(?=.*[A-Z])(?=.*[`~\\-_+=\\[\\]\\{\\}\\|;:'\",<.>/?!@#$%^&*()])|(?=.*[a-z])(?=.*[0-9])(?=.*[`~\\-_+=\\[\\]\\{\\}\\|;:'\",<.>/?!@#$%^&*()])|(?=.*[A-Z])(?=.*[0-9])(?=.*[`~\\-_+=\\[\\]\\{\\}\\|;:'\",<.>/?!@#$%^&*()])).{8,}+$";
        //数字
        final String REG_NUMBER = ".*\\d+.*";
        //小写字母
        final String REG_UPPERCASE = ".*[A-Z]+.*";
        //大写字母
        final String REG_LOWERCASE = ".*[a-z]+.*";
        //特殊符号(~!@#$%^&*()_+|<>,.?/:;'[]{}\)
        final String REG_SYMBOL = ".*[`~\\-_+=\\[\\]{}\\|;:'\",<.>/?!@#$%^&*()]+.*";
        pwdLength = pwdLength < 1 ? 1 : (pwdLength > 20 ? 20 : pwdLength);
        //密码为空及长度大于1位小于20位判断
        if (password == null || password.length() < pwdLength || password.length() > 20) {
            return false;
        }
        int i = 0;
        if (password.matches(REG_NUMBER)) {
            i++;
        }
        if (password.matches(REG_UPPERCASE)) {
            i++;
        }
        if (password.matches(REG_LOWERCASE)) {
            i++;
        }
        if (password.matches(REG_SYMBOL)) {
            i++;
        }
        boolean contains = false;
        if (username != null) {
            if (pwdNoAccountCheck) {
                contains = password.contains(username);
            }
        }
        if (i < pwdTypeSize || contains) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String ss = "aA1`~-_+=[]{}\\|;:'\",<.>/?!@#$%^&*()";
        System.out.println(checkPasswordRule(ss, "name", 4, 6, false));
    }
}
