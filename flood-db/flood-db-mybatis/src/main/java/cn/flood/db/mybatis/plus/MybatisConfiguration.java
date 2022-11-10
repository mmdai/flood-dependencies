package cn.flood.db.mybatis.plus;

import cn.flood.db.mybatis.plus.languagedriver.ConditionsLanguageDriver;
import cn.flood.db.mybatis.plus.mapper.MybatisAutoMapperBuilder;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * 拓展 mybatis 的配置对象
 *
 * @author mmdai
 */
public class MybatisConfiguration extends Configuration{
    /** 自动注入工具 */
    private MybatisAutoMapperBuilder mybatisAutoMapperBuilder;
    // fixed for mybatis-3.5.1
    protected final MapperRegistry mapperRegistry;

    public MybatisConfiguration() {
        super();
        getLanguageRegistry().register(ConditionsLanguageDriver.class);
        this.mapperRegistry = new MybatisMapperRegistry(this);
        this.mybatisAutoMapperBuilder = new MybatisAutoMapperBuilder(this);
    }

    public MybatisAutoMapperBuilder getMybatisAutoMapperBuilder() {
        return mybatisAutoMapperBuilder;
    }

    /**
     * 以下代码都是从 org.apache.ibatis.session.Configuration 直接搬过来用的
     */
    @Override
    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }
}
