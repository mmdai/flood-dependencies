package cn.flood.db.mybatis.plus;

import cn.flood.base.core.enums.annotation.EnumHandler;
import cn.flood.db.mybatis.plus.typehandler.EnumKeyTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.atteo.classindex.ClassIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class EnumConfigurationHelper {

    private static final Logger log = LoggerFactory.getLogger(EnumConfigurationHelper.class);

    private static final Class HANDLER_ENUM_CLAZZ = EnumHandler.class;

    private static final Class HANDLER_CLAZZ = EnumKeyTypeHandler.class;

    public static void loadEnumHandler(SqlSessionFactory factory) throws ClassNotFoundException {
        log.info("EnumTypeHandler - start......");
        List<String> list = getJavaType();
        TypeHandlerRegistry typeHandlerRegistry = factory.getConfiguration().getTypeHandlerRegistry();
        for (String javaTypeClass : list) {
            typeHandlerRegistry.register(javaTypeClass, HANDLER_CLAZZ.getName());
            log.info("EnumTypeHandler - javaTypeClass: {}, TypeHandler: {}", javaTypeClass, HANDLER_CLAZZ.getName());
        }
        log.info("EnumTypeHandler - end......");
    }

    private static List<String> getJavaType() {
        List<String> list = new ArrayList<>();
        final Iterable<Class<?>> klasses = ClassIndex.getAnnotated(HANDLER_ENUM_CLAZZ);
        for (Class<?> clazz : klasses) {
            if (clazz.isEnum()) {
                list.add(clazz.getName());
            } else {
                log.warn("EnumTypeHandler - Not Enum:" + clazz.getName());
            }
        }
        return list;
    }
}
