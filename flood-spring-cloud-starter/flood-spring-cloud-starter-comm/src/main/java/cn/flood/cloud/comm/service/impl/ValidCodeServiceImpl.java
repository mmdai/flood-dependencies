package cn.flood.cloud.comm.service.impl;

import cn.flood.base.core.Func;
import cn.flood.cloud.comm.service.ValidCodeService;
import cn.flood.base.core.lang.StringPool;
import cn.flood.db.redis.util.RedisUtil;
import cn.flood.tools.captcha.Token;
import cn.flood.tools.captcha.TokenEnum;
import cn.flood.tools.captcha.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * <p>Title: ValidCodeServiceImpl</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/8/15
 */
public class ValidCodeServiceImpl implements ValidCodeService {

    /**
     * redis 存放key
     */
    private static final String KEY_TOKEN = "flood:cert:";
    /**
     * redis 存放时间
     */
    private static final int BEING_TIME = 300;//seconds

    @Autowired
    private TokenService tokenService;

    /**
     *
     * @param deviceId 设备号
     * @param tokenType token类型
     * @return
     */
    @Override
    public Token getToken(String deviceId, TokenEnum tokenType) {
        Token token = null;
        switch (tokenType) {
            case CAPTCHA:
                token = tokenService.getToken(TokenEnum.CAPTCHA);
                break;
            case MOBILE:
                token = tokenService.getToken(TokenEnum.MOBILE);
                break;
            case EMAIL:
                token = tokenService.getToken(TokenEnum.EMAIL);
                break;
            default:
        }
        saveToken(deviceId, tokenType, token);
        return token;
    }

    /**
     *
     * @param driverId
     * @param token
     * @return
     */
    private void saveToken(String driverId,TokenEnum tokenType, Token token) {
        StringBuilder tokenKey = new StringBuilder(KEY_TOKEN);
        tokenKey.append(tokenType.getName()).append(StringPool.COLON).append(driverId);
        RedisUtil.getStringHandler().setAsObj(tokenKey.toString(), token, BEING_TIME, TimeUnit.SECONDS);
    }

    /**
     *
     * @param tokenKey
     * @param tokenType
     * @param verifyCode
     * @return
     */
    @Override
    public boolean validToken(String tokenKey, TokenEnum tokenType, String verifyCode) {
        StringBuilder key = new StringBuilder(KEY_TOKEN);
        key.append(tokenType.getName()).append(StringPool.COLON).append(tokenKey);
        Token token = RedisUtil.getStringHandler().getAsObj(key.toString());
        if(Func.isEmpty(token)){
            return false;
        }
        return token.isValid(verifyCode);
    }

    /**
     *
     * @param deviceId
     * @param tokenType
     * @return
     */
    @Override
    public boolean remove(String deviceId, TokenEnum tokenType) {
        StringBuilder tokenKey = new StringBuilder(KEY_TOKEN);
        tokenKey.append(tokenType.getName()).append(StringPool.COLON).append(deviceId);
        return RedisUtil.getStringHandler().remove(tokenKey.toString()) > 0;
    }
}
