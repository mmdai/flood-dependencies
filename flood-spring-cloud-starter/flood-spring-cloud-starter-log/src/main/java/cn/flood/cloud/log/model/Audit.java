package cn.flood.cloud.log.model;

import cn.flood.cloud.log.enums.ActionEnum;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 审计日志
 */
@Getter
@Setter
public class Audit {

  /**
   * 操作时间
   */
  private LocalDateTime timestamp;
  /**
   * 应用名
   */
  private String applicationName;
  /**
   * 类名
   */
  private String className;
  /**
   * 方法名
   */
  private String methodName;
  /**
   * 用户id
   */
  private String userId;
  /**
   * 用户名
   */
  private String userName;
  /**
   * 租户id
   */
  private String clientId;
  /**
   * 操作信息
   */
  private String operation;
  /**
   * 请求IP
   */
  private String requestIP;
  /**
   * 主机IP
   */
  private String hostIP;
  /**
   * 请求参数
   */
  private String param;
  /**
   * 操作类型
   */
  private ActionEnum actionType;
}
