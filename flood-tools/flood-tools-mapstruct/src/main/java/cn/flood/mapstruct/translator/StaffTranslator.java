package cn.flood.mapstruct.translator;

import cn.flood.mapstruct.converter.BaseConverter;
import cn.flood.mapstruct.domain.Staff;
import cn.flood.mapstruct.entity.StaffEntity;
import cn.flood.mapstruct.enums.StatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface StaffTranslator extends BaseConverter<Staff, StaffEntity> {

    @Override
    @Mappings({
            @Mapping(source = "description", target = "staffDescription"),
            @Mapping(source = "department.id", target = "departmentId"),
            @Mapping(target = "birthday", dateFormat = "yyyyMMdd"),
            @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(target = "salary", numberFormat = "0.00")
    })
    StaffEntity source2target(Staff source);


    @Override
    @Mappings({
            @Mapping(source = "staffDescription", target = "description"),
            @Mapping(source = "departmentId", target = "department.id"),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "birthday", dateFormat = "yyyyMMdd"),
            @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    Staff target2Source(StaffEntity target);


    default Integer statusEnum2Int(StatusEnum value){
        return Objects.isNull(value) ? null : value.getValue();
    }

    default StatusEnum int2StatusEnum(Integer value){
        return Objects.isNull(value) ? null : StatusEnum.getByValue(value);
    }
}
