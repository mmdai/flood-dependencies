package cn.flood.cloud.comm.service;

import cn.flood.tools.captcha.Token;
import cn.flood.tools.captcha.TokenEnum;


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
     *
//     * @param tokenKey 前端唯一标识/手机号
//     * @param verifyCode 验证码
//     * @return
     */
    boolean validToken(String tokenKey, TokenEnum tokenType, String verifyCode);

    /**
     * 删除验证码
//     * @param deviceId 前端唯一标识/手机号
//     * @return
     */
    boolean remove(String deviceId, TokenEnum tokenType);
}
