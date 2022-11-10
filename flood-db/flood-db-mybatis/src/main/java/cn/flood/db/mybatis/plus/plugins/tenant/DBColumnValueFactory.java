package cn.flood.db.mybatis.plus.plugins.tenant;

/**
 * 数据库字段值Factory
 *
 * @Author iloveoverfly
 * @LocalDateTime 2020/6/11 15:03
 **/
public interface DBColumnValueFactory {

    /**
     * 生成多租户数据库查询值
     * 字符串需要添加''号
     * 非字符串不需要添加
     *
     * @return
     */
    String buildColumnValue(Object queryValue);
}
