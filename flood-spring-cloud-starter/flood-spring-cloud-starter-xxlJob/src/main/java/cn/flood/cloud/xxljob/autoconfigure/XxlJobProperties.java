package cn.flood.cloud.xxljob.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Title: XxlJobProperties</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2020</p>
 *
 * @author mmdai
 * @version 1.0
 * @date 2020/12/23
 */
@Data
@ConfigurationProperties(prefix = "xxl.job.executor")
public class XxlJobProperties {

  // xxl-job admin address list, such as "http://address" or "http://address01,http://address02"
  private String addresses;
  // xxl-job, access userToken
  private String accessToken;
  // xxl-job executor appname
  private String appname;
  // xxl-job executor registry-address: default use address to registry , otherwise use ip:port if address is null
  private String address;
  //
  private String ip;
  //
  private int port;
  // xxl-job executor log-path
  private String logpath;
  // xxl-job executor log-retention-days
  private int logretentiondays;
}
