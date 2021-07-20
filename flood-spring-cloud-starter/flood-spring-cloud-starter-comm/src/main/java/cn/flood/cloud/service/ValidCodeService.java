package cn.flood.cloud.service;

import cn.flood.captcha.Token;
import cn.flood.captcha.TokenEnum;


/**
 * <p>Title: ValidCodeService</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 */
public interface ValidCodeService {
    /**
     * 获取token
//     * @param deviceId 设备号
//     * @param tokenType token类型
     * @return
     */
    Token getToken(String deviceId, TokenEnum tokenType);
    /**
     * 获取验证码
//     * @param deviceId 前端唯一标识/手机号
//     * @return
     */
    String getCode(String deviceId);

    /**
     *
//     * @param tokenKey 前端唯一标识/手机号
//     * @param verifyCode 验证码
//     * @return
     */
    boolean validCode(String tokenKey, String verifyCode);

    /**
     * 删除验证码
//     * @param deviceId 前端唯一标识/手机号
//     * @return
     */
    boolean remove(String deviceId);
}
