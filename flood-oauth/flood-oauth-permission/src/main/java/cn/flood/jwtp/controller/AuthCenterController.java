package cn.flood.jwtp.controller;

import cn.flood.base.core.UserToken;
import cn.flood.base.core.constants.HeaderConstant;
import cn.flood.base.core.lang.StringPool;
import cn.flood.base.core.rpc.response.Result;
import cn.flood.base.core.rpc.response.ResultWapper;
import cn.flood.jwtp.annotation.Ignore;
import cn.flood.jwtp.constants.TokenConstant;
import cn.flood.jwtp.enums.PlatformEnum;
import cn.flood.jwtp.provider.TokenStore;
import cn.flood.jwtp.util.TokenUtil;
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

  @RequestMapping(TokenConstant.URL.AUTH)
  public Result authentication(@RequestParam(HeaderConstant.ACCESS_TOKEN) String access_token) {
    if (access_token == null || access_token.trim().isEmpty()) {
      return ResultWapper.error("access_token Not Found");
    }
    try {
      String tokenKey = tokenStore.getTokenKey();
      logger.debug("ACCESS_TOKEN: {} ,  TOKEN_KEY: {}", access_token, tokenKey);
      String subject = TokenUtil.parseToken(access_token, tokenKey);
      String[] subjects = subject.split(StringPool.COLON);
      int type = Integer.parseInt(subjects[0]);
      String tenantId = subjects[1];
      String userId = subjects[2];
      // 检查token是否存在系统中
      UserToken userToken = tokenStore
          .findToken(PlatformEnum.valueOfEnum(type), tenantId, userId, access_token);
      if (userToken == null) {
        logger.error("ERROR: UserToken Not Found");
        return ResultWapper.error("UserToken Not Found");
      }
      // 查询用户的角色和权限
      userToken.setRoles(tokenStore
          .findRolesByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, userToken));
      userToken.setPermissions(tokenStore
          .findPermissionsByUserId(PlatformEnum.valueOfEnum(type), tenantId, userId, userToken));
      return ResultWapper.ok(userToken);
    } catch (ExpiredJwtException e) {
      logger.error("ERROR: ExpiredJwtException");
      return ResultWapper.error("ExpiredJwtException");
    } catch (Exception e) {
      return ResultWapper.error();
    }
  }
}
