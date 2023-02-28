package cn.flood.cloud.comm.chat.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 聊天请求参数
 */
@Data
public class ChatRequest implements Serializable {

  /**
   * 路径
   */
  private String path;
  /**
   * 数据
   */
  private String data;
  /**
   * 用户访问token
   */
  private String accessToken;
  /**
   * 用户信息随机数
   */
  private String uuid;
  /**
   * 管理员访问token
   */
  private String adminAccessToken;
  /**
   * 管理员信息随机数
   */
  private String adminUuid;
  /**
   * 成员访问token
   */
  private String memberAccessToken;
  /**
   * 成员信息随机数
   */
  private String memberUuid;
}
