package cn.flood.tools.orika.test;

import cn.flood.tools.orika.OrikaMapperFactoryConfigurer;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import org.springframework.stereotype.Component;

/**
 * The configuration of mapping between {@link PersonSource} and {@link PersonDestination}.
 */
@Component
public class PersonMapping implements OrikaMapperFactoryConfigurer {

    /** {@inheritDoc} */
    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        ConverterFactory converterFactory = orikaMapperFactory.getConverterFactory(); // 注册 converter
        converterFactory.registerConverter("DateConverterId", new DateConverter()); // 这里给 DateConverter 设置一个 id 为 DateConverterId,如果不设置,则为全局注册

        orikaMapperFactory.classMap(PersonSource.class, PersonDestination.class)
                .field("firstName", "givenName")
                .field("lastName", "sirName")
                .fieldMap("birthDay", "birthDayFormat").converter("DateConverterId").add()
                .byDefault()
                .register();
    }

}
