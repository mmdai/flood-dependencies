package cn.flood.jwtp.controller;

import cn.flood.UserToken;
import cn.flood.jwtp.annotation.Ignore;
import cn.flood.jwtp.provider.TokenStore;
import cn.flood.jwtp.util.TokenUtil;
import cn.flood.rpc.response.Result;
import cn.flood.rpc.response.ResultWapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Ignore
@RestController
public class AuthCenterController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TokenStore tokenStore;

    @RequestMapping("/authentication")
    public Result authentication(@RequestParam("access_token") String access_token) {
        if (access_token == null || access_token.trim().isEmpty()) {
            return ResultWapper.error("access_token Not Found");
        }
        try {
            String tokenKey = tokenStore.getTokenKey();
            logger.debug("ACCESS_TOKEN: {} ,  TOKEN_KEY: {}", access_token, tokenKey);
            String userId = TokenUtil.parseToken(access_token, tokenKey);
            // 检查token是否存在系统中
            UserToken userToken = tokenStore.findToken(userId, access_token);
            if (userToken == null) {
                logger.debug("ERROR: UserToken Not Found");
                return ResultWapper.error("UserToken Not Found");
            }
            // 查询用户的角色和权限
            userToken.setRoles(tokenStore.findRolesByUserId(userId, userToken));
            userToken.setPermissions(tokenStore.findPermissionsByUserId(userId, userToken));
            return ResultWapper.ok(userToken);
        } catch (ExpiredJwtException e) {
            logger.debug("ERROR: ExpiredJwtException");
            return ResultWapper.error("ExpiredJwtException");
        } catch (Exception e) {
            return ResultWapper.error();
        }
    }
}
