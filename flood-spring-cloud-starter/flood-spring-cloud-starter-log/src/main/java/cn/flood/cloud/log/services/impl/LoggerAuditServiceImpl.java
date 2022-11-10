package cn.flood.cloud.log.services.impl;


import cn.flood.cloud.log.model.Audit;
import cn.flood.cloud.log.services.IAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.time.format.DateTimeFormatter;

/**
 * 审计日志实现类-打印日志
 *
 * @author zlt
 * @date 2020/2/3
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@ConditionalOnProperty(name = "spring.audit-log.log-type", havingValue = "logger", matchIfMissing = true)
public class LoggerAuditServiceImpl implements IAuditService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String MSG_PATTERN = "{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}";

    /**
     * 格式为：{时间}|{应用名}|{类名}|{方法名}|{用户id}|{用户名}|{租户id}|{操作类型}|{请求IP}|{主机IP}|{请求参数}|{操作信息}
     * 例子：2020-02-04 09:13:34.650|user-center|com.central.user.controller.SysUserController|saveOrUpdate|1|admin|webApp|OTHER|xxx.xxx.xxx.xxx|xxx.xxx.xxx.xxx|json|新增用户:admin
     */
    @Override
    public void save(Audit audit) {
        log.info(MSG_PATTERN
                , audit.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                , audit.getApplicationName(), audit.getClassName(), audit.getMethodName()
                , audit.getUserId(), audit.getUserName(), audit.getClientId()
                , audit.getActionType(), audit.getRequestIP(), audit.getHostIP()
                , audit.getOperation(), audit.getParam());
    }
}
