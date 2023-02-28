package cn.flood.base.core;

import java.io.Serializable;
import java.util.Date;

/**
 * Token实体类
 */
public class UserToken implements Serializable {

  /**
   * 自增主键
   */
  private Integer tokenId;
  /**
   * 用户id
   */
  private String userId;
  /**
   * access_token
   */
  private String accessToken;
  /**
   * 租户ID
   */
  private String tenantId;
  /**
   * 昵称
   */
  private String userName;
  /**
   * 账号
   */
  private String account;
  /**
   * 部门id
   */
  private String deptId;
  /**
   * 岗位id
   */
  private String postId;
  /**
   * refresh_token
   */
  private String refreshToken;
  /**
   * 过期时间
   */
  private Date expireTime;
  /**
   * 过期时间（秒）
   */
  private Long expireSecond;
  /**
   * refresh_token过期时间
   */
  private Date refreshTokenExpireTime;
  /**
   * refresh_token过期时间（秒）
   */
  private Long refreshTokenExpireSecond;
  /**
   * 用户角色
   */
  private String[] roles;
  /**
   * 用户权限
   */
  private String[] permissions;

  /**
   * 用户信息
   */
  private String userInfo;

  public Integer getTokenId() {
    return tokenId;
  }

  public void setTokenId(Integer tokenId) {
    this.tokenId = tokenId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Date getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Date expireTime) {
    this.expireTime = expireTime;
  }

  public String[] getRoles() {
    return roles;
  }

  public void setRoles(String[] roles) {
    this.roles = roles;
  }

  public String[] getPermissions() {
    return permissions;
  }

  public void setPermissions(String[] permissions) {
    this.permissions = permissions;
  }

  public Date getRefreshTokenExpireTime() {
    return refreshTokenExpireTime;
  }

  public void setRefreshTokenExpireTime(Date refreshTokenExpireTime) {
    this.refreshTokenExpireTime = refreshTokenExpireTime;
  }

  public String getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(String userInfo) {
    this.userInfo = userInfo;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getDeptId() {
    return deptId;
  }

  public void setDeptId(String deptId) {
    this.deptId = deptId;
  }

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }

  public Long getExpireSecond() {
    return expireSecond;
  }

  public void setExpireSecond(Long expireSecond) {
    this.expireSecond = expireSecond;
  }

  public Long getRefreshTokenExpireSecond() {
    return refreshTokenExpireSecond;
  }

  public void setRefreshTokenExpireSecond(Long refreshTokenExpireSecond) {
    this.refreshTokenExpireSecond = refreshTokenExpireSecond;
  }
}
