package cn.flood.mybatis.plus;

import cn.flood.enums.annotation.EnumHandler;
import cn.flood.mybatis.plus.typehandler.EnumKeyTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.atteo.classindex.ClassIndex;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EnumConfigurationHelper {

    private static final Class HANDLER_ENUM_CLAZZ = EnumHandler.class;

    private static final Class HANDLER_CLAZZ = EnumKeyTypeHandler.class;

    public static void loadEnumHandler(SqlSessionFactory factory) throws ClassNotFoundException {
        log.info("EnumTypeHandler - start......");
        List<String> list = getJavaType();
        TypeHandlerRegistry typeHandlerRegistry = factory.getConfiguration().getTypeHandlerRegistry();
        for (String javaTypeClass : list) {
            typeHandlerRegistry.register(javaTypeClass, HANDLER_CLAZZ.getName());
            log.info("EnumTypeHandler - javaTypeClass:" + javaTypeClass + ", TypeHandler:" + HANDLER_CLAZZ.getName());
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