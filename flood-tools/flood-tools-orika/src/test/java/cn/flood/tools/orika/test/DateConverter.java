package cn.flood.tools.orika.test;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author mmdai
 * @version 1.0
 * @date 2022/6/1 11:49
 */
public class DateConverter extends CustomConverter<Date, String> {

    @Override
    public String convert(Date date, Type<? extends String> type, MappingContext mappingContext) {
        LocalDateTime time = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(formatter);
    }
}
