package cn.flood.log.services;


import cn.flood.log.model.Audit;

/**
 * 审计日志接口
 */
public interface IAuditService {
    void save(Audit audit);
}
