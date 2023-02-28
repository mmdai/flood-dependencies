package cn.flood.tools.captcha;

/**
 * TODO 类描述
 * <p>
 * 图形验证码服务
 * <p>
 *
 * @author mmdai
 * @date 2017年6月26日
 */
public class TokenService {

  private TokenFactory tokenFactory;

  public TokenService(TokenFactory tokenFactory) {
    this.tokenFactory = tokenFactory;
  }

  /**
   * 获取图形验证码 TODO 方法描述
   *
   * @param
   * @param
   * @return
   * @throws
   */
  public Token getToken(TokenEnum tokenType) {
    return tokenFactory.getToken(tokenType);
  }

  /**
   * 获取幂等性token
   *
   * @return
   */
  public Token getIdempotentToken() {
    return tokenFactory.getIdempotentToken();
  }

  /**
   * 验证图形验证码 TODO 方法描述
   *
   * @param
   * @param
   * @return
   * @throws
   */
  public boolean checkToken(Token token, String captcha) {

    if (token == null || captcha == null) {
      return false;
    }
    if (!token.isValid(captcha)) {
      return false;
    }
    return true;

  }

  public TokenFactory getTokenFactory() {
    return tokenFactory;
  }

  public void setTokenFactory(TokenFactory tokenFactory) {
    this.tokenFactory = tokenFactory;
  }
}
