package cn.flood.cloud.log.services;


import cn.flood.cloud.log.model.Audit;

/**
 * 审计日志接口
 */
public interface IAuditService {

  void save(Audit audit);
}
